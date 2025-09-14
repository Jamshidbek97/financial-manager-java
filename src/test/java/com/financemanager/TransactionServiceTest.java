package com.financemanager;

import com.financemanager.model.*;
import com.financemanager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransactionService
 * Demonstrates testing of service layer and business logic
 */
@DisplayName("Transaction Service Tests")
class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
    }

    @Test
    @DisplayName("Should create income transaction successfully")
    void shouldCreateIncomeTransactionSuccessfully() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("1000.00");
        String description = "Salary";
        Category category = Category.SALARY;

        // When
        Transaction transaction = transactionService.createIncomeTransaction(accountId, amount, description, category);

        // Then
        assertNotNull(transaction);
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(category, transaction.getCategory());
        assertTrue(transaction.isIncome());
        assertFalse(transaction.isExpense());
    }

    @Test
    @DisplayName("Should create expense transaction successfully")
    void shouldCreateExpenseTransactionSuccessfully() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Groceries";
        Category category = Category.FOOD;

        // When
        Transaction transaction = transactionService.createExpenseTransaction(accountId, amount, description, category);

        // Then
        assertNotNull(transaction);
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(category, transaction.getCategory());
        assertFalse(transaction.isIncome());
        assertTrue(transaction.isExpense());
    }

    @Test
    @DisplayName("Should create transfer between accounts")
    void shouldCreateTransferBetweenAccounts() {
        // Given
        String fromAccountId = "ACC_001";
        String toAccountId = "ACC_002";
        BigDecimal amount = new BigDecimal("500.00");
        String description = "Monthly savings";

        // When
        Transaction[] transfer = transactionService.createTransfer(fromAccountId, toAccountId, amount, description);

        // Then
        assertNotNull(transfer);
        assertEquals(2, transfer.length);

        // Check debit transaction
        Transaction debit = transfer[0];
        assertEquals(fromAccountId, debit.getAccountId());
        assertEquals(TransactionType.EXPENSE, debit.getType());
        assertEquals(amount, debit.getAmount());
        assertTrue(debit.getDescription().contains("Transfer to"));
        assertEquals(Category.OTHER_EXPENSE, debit.getCategory());

        // Check credit transaction
        Transaction credit = transfer[1];
        assertEquals(toAccountId, credit.getAccountId());
        assertEquals(TransactionType.INCOME, credit.getType());
        assertEquals(amount, credit.getAmount());
        assertTrue(credit.getDescription().contains("Transfer from"));
        assertEquals(Category.OTHER_INCOME, credit.getCategory());
    }

    @Test
    @DisplayName("Should throw exception for transfer to same account")
    void shouldThrowExceptionForTransferToSameAccount() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("500.00");
        String description = "Invalid transfer";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransfer(accountId, accountId, amount, description));
    }

    @Test
    @DisplayName("Should throw exception for null account ID")
    void shouldThrowExceptionForNullAccountId() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(null, TransactionType.INCOME, amount, description, category));
    }

    @Test
    @DisplayName("Should throw exception for empty account ID")
    void shouldThrowExceptionForEmptyAccountId() {
        // Given
        String accountId = "";
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, TransactionType.INCOME, amount, description, category));
    }

    @Test
    @DisplayName("Should throw exception for null transaction type")
    void shouldThrowExceptionForNullTransactionType() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, null, amount, description, category));
    }

    @Test
    @DisplayName("Should throw exception for null amount")
    void shouldThrowExceptionForNullAmount() {
        // Given
        String accountId = "ACC_001";
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, TransactionType.INCOME, null, description, category));
    }

    @Test
    @DisplayName("Should throw exception for zero amount")
    void shouldThrowExceptionForZeroAmount() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = BigDecimal.ZERO;
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, TransactionType.INCOME, amount, description, category));
    }

    @Test
    @DisplayName("Should throw exception for negative amount")
    void shouldThrowExceptionForNegativeAmount() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("-100.00");
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, TransactionType.INCOME, amount, description, category));
    }

    @Test
    @DisplayName("Should throw exception for null description")
    void shouldThrowExceptionForNullDescription() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("100.00");
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, TransactionType.INCOME, amount, null, category));
    }

    @Test
    @DisplayName("Should throw exception for empty description")
    void shouldThrowExceptionForEmptyDescription() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("100.00");
        String description = "";
        Category category = Category.OTHER_INCOME;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.createTransaction(accountId, TransactionType.INCOME, amount, description, category));
    }

    @Test
    @DisplayName("Should generate unique transaction IDs")
    void shouldGenerateUniqueTransactionIds() {
        // Given
        String accountId = "ACC_001";
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test transaction";
        Category category = Category.OTHER_INCOME;

        // When
        Transaction transaction1 = transactionService.createTransaction(accountId, TransactionType.INCOME, amount, description, category);
        Transaction transaction2 = transactionService.createTransaction(accountId, TransactionType.INCOME, amount, description, category);

        // Then
        assertNotEquals(transaction1.getId(), transaction2.getId());
        assertNotNull(transaction1.getId());
        assertNotNull(transaction2.getId());
        assertTrue(transaction1.getId().startsWith("TXN_"));
        assertTrue(transaction2.getId().startsWith("TXN_"));
    }
}
