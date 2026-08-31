package com.example.transactionstarter.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class TransactionService {

    private static final String PENDING = "PENDING";
    private static final String COMPLETED = "COMPLETED";
    private static final String FAILED = "FAILED";
    private static final Set<String> SUPPORTED_STATUSES = Set.of(PENDING, COMPLETED, FAILED);

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction createTransaction(CreateTransactionRequest request) {
        validateStatus(request.transactionStatus());
        if (transactionRepository.existsById(request.transactionId())) {
            throw new DuplicateTransactionException(request.transactionId());
        }

        Transaction transaction = new Transaction(
                request.transactionId(),
                request.customerId(),
                request.amount(),
                request.currency(),
                request.transactionType(),
                request.transactionStatus());
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @Transactional
    public Transaction updateTransactionStatus(String transactionId, String requestedStatus) {
        validateStatus(requestedStatus);
        Transaction transaction = getTransaction(transactionId);
        if (!isAllowedTransition(transaction.getTransactionStatus(), requestedStatus)) {
            throw new InvalidStatusTransitionException(
                    transaction.getTransactionStatus(), requestedStatus);
        }

        transaction.setTransactionStatus(requestedStatus);
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getCustomerTransactions(String customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }

    private void validateStatus(String status) {
        if (!SUPPORTED_STATUSES.contains(status)) {
            throw new UnsupportedTransactionStatusException(status);
        }
    }

    // Status transition rules: PENDING -> COMPLETED or FAILED (terminal states, no backward moves)
    private boolean isAllowedTransition(String currentStatus, String requestedStatus) {
        return PENDING.equals(currentStatus)
                && (COMPLETED.equals(requestedStatus) || FAILED.equals(requestedStatus));
    }
}