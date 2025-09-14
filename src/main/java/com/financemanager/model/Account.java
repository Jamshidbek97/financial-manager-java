package com.financemanager.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a financial account (checking, savings, credit card, etc.)
 * Demonstrates OOP principles: encapsulation, data validation, and immutability
 */
public class Account {
    private final String id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account(String id, String name, AccountType type, BigDecimal initialBalance) {
        this.id = Objects.requireNonNull(id, "Account ID cannot be null");
        this.name = Objects.requireNonNull(name, "Account name cannot be null");
        this.type = Objects.requireNonNull(type, "Account type cannot be null");
        this.balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public AccountType getType() { return type; }
    public BigDecimal getBalance() { return balance; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters with validation
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Account name cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void setType(AccountType type) {
        this.type = Objects.requireNonNull(type, "Account type cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasSufficientFunds(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Account{id='%s', name='%s', type=%s, balance=%s}", 
                           id, name, type, balance);
    }
}
