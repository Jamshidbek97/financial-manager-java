package com.financemanager.service;

import com.financemanager.model.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service class for transaction-related operations
 * Demonstrates factory pattern and transaction validation
 */
public class TransactionService {
    
    /**
     * Creates a new transaction with validation
     */
    public Transaction createTransaction(String accountId, TransactionType type, 
                                       BigDecimal amount, String description, Category category) {
        validateTransactionInput(accountId, type, amount, description);
        
        String transactionId = generateTransactionId();
        return new Transaction(transactionId, accountId, type, amount, description, category);
    }
    
    /**
     * Creates a quick income transaction
     */
    public Transaction createIncomeTransaction(String accountId, BigDecimal amount, 
                                             String description, Category category) {
        return createTransaction(accountId, TransactionType.INCOME, amount, description, category);
    }
    
    /**
     * Creates a quick expense transaction
     */
    public Transaction createExpenseTransaction(String accountId, BigDecimal amount, 
                                              String description, Category category) {
        return createTransaction(accountId, TransactionType.EXPENSE, amount, description, category);
    }
    
    /**
     * Transfers money between accounts
     */
    public Transaction[] createTransfer(String fromAccountId, String toAccountId, 
                                      BigDecimal amount, String description) {
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        String transferId = generateTransactionId();
        String descriptionWithTransfer = description + " (Transfer)";
        
        Transaction debit = new Transaction(
            transferId + "_debit", 
            fromAccountId, 
            TransactionType.EXPENSE, 
            amount, 
            "Transfer to " + toAccountId + " - " + descriptionWithTransfer,
            Category.OTHER_EXPENSE
        );
        
        Transaction credit = new Transaction(
            transferId + "_credit", 
            toAccountId, 
            TransactionType.INCOME, 
            amount, 
            "Transfer from " + fromAccountId + " - " + descriptionWithTransfer,
            Category.OTHER_INCOME
        );
        
        return new Transaction[]{debit, credit};
    }
    
    private void validateTransactionInput(String accountId, TransactionType type, 
                                        BigDecimal amount, String description) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    }
    
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
