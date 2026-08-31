package com.example.transactionstarter.transaction;

public class UnsupportedTransactionStatusException extends RuntimeException {

    public UnsupportedTransactionStatusException(String status) {
        super("Unsupported transaction status: " + status);
    }
}