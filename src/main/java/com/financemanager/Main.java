package com.financemanager;

import com.financemanager.model.*;
import com.financemanager.service.FinanceService;
import com.financemanager.service.TransactionService;
import com.financemanager.view.FinanceManagerGUI;

import java.math.BigDecimal;

/**
 * Main application class for Personal Finance Manager
 * Demonstrates application startup, dependency injection, and sample data
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Personal Finance Manager...");
        
        // Initialize services
        FinanceService financeService = new FinanceService();
        TransactionService transactionService = financeService.getTransactionService();
        
        // Create sample data for demonstration
        createSampleData(financeService, transactionService);
        
        // Launch GUI
        FinanceManagerGUI.launch(financeService);
        
        System.out.println("Personal Finance Manager started successfully!");
    }
    
    /**
     * Creates sample data to demonstrate the application
     */
    private static void createSampleData(FinanceService financeService, TransactionService transactionService) {
        // Create sample accounts
        Account checkingAccount = new Account("ACC_001", "Main Checking", AccountType.CHECKING, new BigDecimal("2500.00"));
        Account savingsAccount = new Account("ACC_002", "Emergency Savings", AccountType.SAVINGS, new BigDecimal("10000.00"));
        Account creditCard = new Account("ACC_003", "Credit Card", AccountType.CREDIT_CARD, new BigDecimal("-500.00"));
        
        financeService.addAccount(checkingAccount);
        financeService.addAccount(savingsAccount);
        financeService.addAccount(creditCard);
        
        // Create sample transactions
        try {
            // Income transactions
            Transaction salary = transactionService.createIncomeTransaction(
                "ACC_001", new BigDecimal("3000.00"), "Monthly Salary", Category.SALARY);
            financeService.addTransaction(salary);
            
            Transaction freelance = transactionService.createIncomeTransaction(
                "ACC_001", new BigDecimal("500.00"), "Freelance Project", Category.FREELANCE);
            financeService.addTransaction(freelance);
            
            // Expense transactions
            Transaction rent = transactionService.createExpenseTransaction(
                "ACC_001", new BigDecimal("1200.00"), "Monthly Rent", Category.HOUSING);
            financeService.addTransaction(rent);
            
            Transaction groceries = transactionService.createExpenseTransaction(
                "ACC_001", new BigDecimal("150.00"), "Weekly Groceries", Category.FOOD);
            financeService.addTransaction(groceries);
            
            Transaction gas = transactionService.createExpenseTransaction(
                "ACC_001", new BigDecimal("60.00"), "Gas Station", Category.TRANSPORTATION);
            financeService.addTransaction(gas);
            
            Transaction netflix = transactionService.createExpenseTransaction(
                "ACC_001", new BigDecimal("15.99"), "Netflix Subscription", Category.ENTERTAINMENT);
            financeService.addTransaction(netflix);
            
            // Transfer from checking to savings
            Transaction[] transfer = transactionService.createTransfer(
                "ACC_001", "ACC_002", new BigDecimal("500.00"), "Monthly Savings");
            financeService.addTransaction(transfer[0]);
            financeService.addTransaction(transfer[1]);
            
        } catch (Exception e) {
            System.err.println("Error creating sample data: " + e.getMessage());
        }
        
        System.out.println("Sample data created successfully!");
        System.out.println("Total Balance: $" + financeService.getTotalBalance());
    }
}
