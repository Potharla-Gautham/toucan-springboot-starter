package com.example.transactionstarter.transaction;

import jakarta.validation.constraints.NotBlank;

public record UpdateTransactionStatusRequest(@NotBlank String transactionStatus) {
}