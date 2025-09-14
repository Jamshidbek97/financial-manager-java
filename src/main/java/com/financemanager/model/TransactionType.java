package com.financemanager.model;

/**
 * Enum representing transaction types
 */
public enum TransactionType {
    INCOME("Income", "Money coming in"),
    EXPENSE("Expense", "Money going out");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
