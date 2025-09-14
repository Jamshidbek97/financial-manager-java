package com.financemanager.model;

/**
 * Enum representing transaction categories for better organization
 */
public enum Category {
    // Income categories
    SALARY("Salary", "Regular employment income"),
    FREELANCE("Freelance", "Freelance or contract work"),
    INVESTMENT("Investment Returns", "Dividends, interest, capital gains"),
    BUSINESS("Business Income", "Business revenue"),
    GIFT("Gift", "Gifts and donations received"),
    OTHER_INCOME("Other Income", "Miscellaneous income"),

    // Expense categories
    HOUSING("Housing", "Rent, mortgage, utilities"),
    FOOD("Food & Dining", "Groceries, restaurants, food delivery"),
    TRANSPORTATION("Transportation", "Gas, public transit, car maintenance"),
    HEALTHCARE("Healthcare", "Medical expenses, insurance, pharmacy"),
    ENTERTAINMENT("Entertainment", "Movies, games, hobbies, subscriptions"),
    SHOPPING("Shopping", "Clothing, electronics, general purchases"),
    EDUCATION("Education", "Tuition, books, courses"),
    TRAVEL("Travel", "Vacations, hotels, flights"),
    INSURANCE("Insurance", "Auto, health, life insurance"),
    UTILITIES("Utilities", "Electricity, water, internet, phone"),
    OTHER_EXPENSE("Other Expense", "Miscellaneous expenses");

    private final String displayName;
    private final String description;

    Category(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIncomeCategory() {
        return this == SALARY || this == FREELANCE || this == INVESTMENT || 
               this == BUSINESS || this == GIFT || this == OTHER_INCOME;
    }

    public boolean isExpenseCategory() {
        return !isIncomeCategory();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
