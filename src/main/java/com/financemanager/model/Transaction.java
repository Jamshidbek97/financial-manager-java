package com.financemanager.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a financial transaction
 * Demonstrates complex object modeling and validation
 */
public class Transaction {
    private final String id;
    private final String accountId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private Category category;
    private LocalDateTime date;
    private final LocalDateTime createdAt;

    public Transaction(String id, String accountId, TransactionType type, 
                      BigDecimal amount, String description, Category category) {
        this.id = Objects.requireNonNull(id, "Transaction ID cannot be null");
        this.accountId = Objects.requireNonNull(accountId, "Account ID cannot be null");
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.description = description;
        this.category = category;
        this.date = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        
        validateAmount();
    }

    private void validateAmount() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
    }

    // Getters
    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public LocalDateTime getDate() { return date; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters with validation
    public void setType(TransactionType type) {
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null");
    }

    public void setAmount(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        validateAmount();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDate(LocalDateTime date) {
        this.date = date != null ? date : LocalDateTime.now();
    }

    // Business methods
    public BigDecimal getSignedAmount() {
        return type == TransactionType.INCOME ? amount : amount.negate();
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', accountId='%s', type=%s, amount=%s, description='%s'}", 
                           id, accountId, type, amount, description);
    }
}
