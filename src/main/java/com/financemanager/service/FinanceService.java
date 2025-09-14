package com.financemanager.service;

import com.financemanager.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for finance management
 * Demonstrates service layer pattern, collections usage, and streams
 */
public class FinanceService {
    private final Map<String, Account> accounts;
    private final List<Transaction> transactions;
    private final TransactionService transactionService;

    public FinanceService() {
        this.accounts = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.transactionService = new TransactionService();
    }

    // Account Management
    public void addAccount(Account account) {
        if (accounts.containsKey(account.getId())) {
            throw new IllegalArgumentException("Account with ID " + account.getId() + " already exists");
        }
        accounts.put(account.getId(), account);
    }

    public Optional<Account> getAccount(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void removeAccount(String accountId) {
        if (!accounts.containsKey(accountId)) {
            throw new IllegalArgumentException("Account with ID " + accountId + " not found");
        }
        accounts.remove(accountId);
        // Remove all transactions for this account
        transactions.removeIf(transaction -> transaction.getAccountId().equals(accountId));
    }

    // Transaction Management
    public void addTransaction(Transaction transaction) {
        Account account = accounts.get(transaction.getAccountId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found for transaction");
        }

        // Update account balance
        account.updateBalance(transaction.getSignedAmount());
        
        // Add transaction
        transactions.add(transaction);
    }

    public List<Transaction> getTransactionsForAccount(String accountId) {
        return transactions.stream()
                .filter(transaction -> transaction.getAccountId().equals(accountId))
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    // Analytics and Reporting
    public BigDecimal getTotalBalance() {
        return accounts.values().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMonthlyIncome(Month month, int year) {
        return transactions.stream()
                .filter(transaction -> transaction.isIncome())
                .filter(transaction -> transaction.getDate().getMonth() == month)
                .filter(transaction -> transaction.getDate().getYear() == year)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMonthlyExpenses(Month month, int year) {
        return transactions.stream()
                .filter(transaction -> transaction.isExpense())
                .filter(transaction -> transaction.getDate().getMonth() == month)
                .filter(transaction -> transaction.getDate().getYear() == year)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Category, BigDecimal> getExpensesByCategory(Month month, int year) {
        return transactions.stream()
                .filter(transaction -> transaction.isExpense())
                .filter(transaction -> transaction.getDate().getMonth() == month)
                .filter(transaction -> transaction.getDate().getYear() == year)
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    public List<Transaction> searchTransactions(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTransactions();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        return transactions.stream()
                .filter(transaction -> 
                    transaction.getDescription().toLowerCase().contains(lowerSearchTerm) ||
                    transaction.getCategory().getDisplayName().toLowerCase().contains(lowerSearchTerm)
                )
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    // Budget Management
    public Map<Category, BigDecimal> getMonthlyBudgetRecommendations() {
        Map<Category, BigDecimal> recommendations = new HashMap<>();
        
        // Get average expenses for the last 3 months
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        
        for (Category category : Category.values()) {
            if (category.isExpenseCategory()) {
                BigDecimal averageExpense = transactions.stream()
                        .filter(transaction -> transaction.getCategory() == category)
                        .filter(transaction -> transaction.getDate().isAfter(threeMonthsAgo))
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(3), 2, java.math.RoundingMode.HALF_UP);
                
                // Add 10% buffer to average expense
                BigDecimal recommendation = averageExpense.multiply(BigDecimal.valueOf(1.1));
                recommendations.put(category, recommendation);
            }
        }
        
        return recommendations;
    }

    // Data validation and business rules
    public boolean canMakeTransaction(String accountId, BigDecimal amount, TransactionType type) {
        Account account = accounts.get(accountId);
        if (account == null) return false;
        
        if (type == TransactionType.EXPENSE) {
            return account.hasSufficientFunds(amount);
        }
        return true; // Income transactions are always allowed
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }
}
