package com.example.transactionstarter.transaction;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String currentStatus, String requestedStatus) {
        super("Invalid status transition from " + currentStatus + " to " + requestedStatus);
    }
}