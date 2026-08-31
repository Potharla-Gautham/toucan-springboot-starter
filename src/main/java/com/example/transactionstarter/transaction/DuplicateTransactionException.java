package com.example.transactionstarter.transaction;

public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String transactionId) {
        super("Transaction ID already exists: " + transactionId);
    }
}