package com.financemanager.model;

/**
 * Enum representing different types of financial accounts
 * Demonstrates enum usage and categorization
 */
public enum AccountType {
    CHECKING("Checking Account", "Daily transactions and bill payments"),
    SAVINGS("Savings Account", "Long-term savings with interest"),
    CREDIT_CARD("Credit Card", "Credit line for purchases"),
    INVESTMENT("Investment Account", "Stocks, bonds, and other investments"),
    CASH("Cash", "Physical cash on hand"),
    LOAN("Loan Account", "Personal or business loans"),
    MORTGAGE("Mortgage", "Home or property loans");

    private final String displayName;
    private final String description;

    AccountType(String displayName, String description) {
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
