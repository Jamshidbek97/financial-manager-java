package com.financemanager;

import com.financemanager.model.*;
import com.financemanager.service.FinanceService;
import com.financemanager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FinanceService
 * Demonstrates testing skills, JUnit 5 usage, and test-driven development
 */
@DisplayName("Finance Service Tests")
class FinanceServiceTest {

    private FinanceService financeService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        financeService = new FinanceService();
        transactionService = financeService.getTransactionService();
    }

    @Test
    @DisplayName("Should add account successfully")
    void shouldAddAccountSuccessfully() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, BigDecimal.ZERO);

        // When
        financeService.addAccount(account);

        // Then
        assertTrue(financeService.getAccount("ACC_001").isPresent());
        assertEquals(1, financeService.getAllAccounts().size());
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate account")
    void shouldThrowExceptionWhenAddingDuplicateAccount() {
        // Given
        Account account1 = new Account("ACC_001", "Account 1", AccountType.CHECKING, BigDecimal.ZERO);
        Account account2 = new Account("ACC_001", "Account 2", AccountType.SAVINGS, BigDecimal.ZERO);

        // When & Then
        financeService.addAccount(account1);
        assertThrows(IllegalArgumentException.class, () -> financeService.addAccount(account2));
    }

    @Test
    @DisplayName("Should calculate total balance correctly")
    void shouldCalculateTotalBalanceCorrectly() {
        // Given
        Account account1 = new Account("ACC_001", "Account 1", AccountType.CHECKING, new BigDecimal("1000.00"));
        Account account2 = new Account("ACC_002", "Account 2", AccountType.SAVINGS, new BigDecimal("2000.00"));
        Account account3 = new Account("ACC_003", "Account 3", AccountType.CREDIT_CARD, new BigDecimal("-500.00"));

        // When
        financeService.addAccount(account1);
        financeService.addAccount(account2);
        financeService.addAccount(account3);

        // Then
        BigDecimal totalBalance = financeService.getTotalBalance();
        assertEquals(new BigDecimal("2500.00"), totalBalance);
    }

    @Test
    @DisplayName("Should add transaction and update account balance")
    void shouldAddTransactionAndUpdateAccountBalance() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, new BigDecimal("1000.00"));
        financeService.addAccount(account);

        Transaction income = transactionService.createIncomeTransaction(
            "ACC_001", new BigDecimal("500.00"), "Salary", Category.SALARY);

        // When
        financeService.addTransaction(income);

        // Then
        assertEquals(new BigDecimal("1500.00"), account.getBalance());
        assertEquals(1, financeService.getAllTransactions().size());
    }

    @Test
    @DisplayName("Should calculate monthly income correctly")
    void shouldCalculateMonthlyIncomeCorrectly() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, BigDecimal.ZERO);
        financeService.addAccount(account);

        Transaction salary = transactionService.createIncomeTransaction(
            "ACC_001", new BigDecimal("3000.00"), "Salary", Category.SALARY);
        Transaction freelance = transactionService.createIncomeTransaction(
            "ACC_001", new BigDecimal("500.00"), "Freelance", Category.FREELANCE);

        financeService.addTransaction(salary);
        financeService.addTransaction(freelance);

        // When
        BigDecimal monthlyIncome = financeService.getMonthlyIncome(Month.from(LocalDateTime.now()), LocalDateTime.now().getYear());

        // Then
        assertEquals(new BigDecimal("3500.00"), monthlyIncome);
    }

    @Test
    @DisplayName("Should calculate monthly expenses correctly")
    void shouldCalculateMonthlyExpensesCorrectly() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, new BigDecimal("1000.00"));
        financeService.addAccount(account);

        Transaction rent = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("1200.00"), "Rent", Category.HOUSING);
        Transaction groceries = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("150.00"), "Groceries", Category.FOOD);

        financeService.addTransaction(rent);
        financeService.addTransaction(groceries);

        // When
        BigDecimal monthlyExpenses = financeService.getMonthlyExpenses(Month.from(LocalDateTime.now()), LocalDateTime.now().getYear());

        // Then
        assertEquals(new BigDecimal("1350.00"), monthlyExpenses);
    }

    @Test
    @DisplayName("Should group expenses by category")
    void shouldGroupExpensesByCategory() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, new BigDecimal("1000.00"));
        financeService.addAccount(account);

        Transaction rent = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("1200.00"), "Rent", Category.HOUSING);
        Transaction groceries = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("150.00"), "Groceries", Category.FOOD);
        Transaction moreGroceries = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("100.00"), "More Groceries", Category.FOOD);

        financeService.addTransaction(rent);
        financeService.addTransaction(groceries);
        financeService.addTransaction(moreGroceries);

        // When
        Map<Category, BigDecimal> expensesByCategory = financeService.getExpensesByCategory(Month.from(LocalDateTime.now()), LocalDateTime.now().getYear());

        // Then
        assertEquals(2, expensesByCategory.size());
        assertEquals(new BigDecimal("1200.00"), expensesByCategory.get(Category.HOUSING));
        assertEquals(new BigDecimal("250.00"), expensesByCategory.get(Category.FOOD));
    }

    @Test
    @DisplayName("Should search transactions by description")
    void shouldSearchTransactionsByDescription() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, new BigDecimal("1000.00"));
        financeService.addAccount(account);

        Transaction rent = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("1200.00"), "Monthly Rent Payment", Category.HOUSING);
        Transaction groceries = transactionService.createExpenseTransaction(
            "ACC_001", new BigDecimal("150.00"), "Weekly Groceries", Category.FOOD);

        financeService.addTransaction(rent);
        financeService.addTransaction(groceries);

        // When
        List<Transaction> searchResults = financeService.searchTransactions("rent");

        // Then
        assertEquals(1, searchResults.size());
        assertEquals("Monthly Rent Payment", searchResults.get(0).getDescription());
    }

    @Test
    @DisplayName("Should validate transaction feasibility")
    void shouldValidateTransactionFeasibility() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, new BigDecimal("100.00"));
        financeService.addAccount(account);

        // When & Then
        assertTrue(financeService.canMakeTransaction("ACC_001", new BigDecimal("50.00"), TransactionType.EXPENSE));
        assertFalse(financeService.canMakeTransaction("ACC_001", new BigDecimal("150.00"), TransactionType.EXPENSE));
        assertTrue(financeService.canMakeTransaction("ACC_001", new BigDecimal("1000.00"), TransactionType.INCOME));
    }

    @Test
    @DisplayName("Should remove account and associated transactions")
    void shouldRemoveAccountAndAssociatedTransactions() {
        // Given
        Account account = new Account("ACC_001", "Test Account", AccountType.CHECKING, new BigDecimal("1000.00"));
        financeService.addAccount(account);

        Transaction transaction = transactionService.createIncomeTransaction(
            "ACC_001", new BigDecimal("500.00"), "Salary", Category.SALARY);
        financeService.addTransaction(transaction);

        assertEquals(1, financeService.getAllTransactions().size());

        // When
        financeService.removeAccount("ACC_001");

        // Then
        assertFalse(financeService.getAccount("ACC_001").isPresent());
        assertEquals(0, financeService.getAllTransactions().size());
    }
}
