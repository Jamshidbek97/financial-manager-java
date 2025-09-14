package com.financemanager.view;

import com.financemanager.model.*;
import com.financemanager.service.FinanceService;
import com.financemanager.service.TransactionService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Main GUI class for Personal Finance Manager
 * Demonstrates JavaFX usage, event handling, and modern UI design
 */
public class FinanceManagerGUI extends Application {
    private FinanceService financeService;
    private TableView<Account> accountTable;
    private TableView<Transaction> transactionTable;
    private Label totalBalanceLabel;
    private ComboBox<AccountType> accountTypeCombo;
    private ComboBox<TransactionType> transactionTypeCombo;
    private ComboBox<Category> categoryCombo;
    private TextField accountNameField;
    private TextField transactionAmountField;
    private TextField transactionDescriptionField;
    private TextField searchField;

    private static FinanceService staticFinanceService;
    
    public static void launch(FinanceService financeService) {
        staticFinanceService = financeService;
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        this.financeService = staticFinanceService;
        primaryStage.setTitle("Personal Finance Manager - Java Interview Project");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Create top section with balance overview
        VBox topSection = createTopSection();
        mainLayout.setTop(topSection);

        // Create center section with tabs
        TabPane tabPane = createTabPane();
        mainLayout.setCenter(tabPane);

        // Create bottom section with quick actions
        HBox bottomSection = createBottomSection();
        mainLayout.setBottom(bottomSection);

        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Refresh data
        refreshData();
    }

    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(10));
        topSection.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%); " +
                           "-fx-background-radius: 10; -fx-padding: 20;");

        Label titleLabel = new Label("Personal Finance Manager");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        totalBalanceLabel = new Label("Total Balance: $0.00");
        totalBalanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        totalBalanceLabel.setTextFill(Color.WHITE);

        topSection.getChildren().addAll(titleLabel, totalBalanceLabel);
        return topSection;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();

        // Accounts Tab
        Tab accountsTab = new Tab("Accounts");
        accountsTab.setContent(createAccountsTab());
        accountsTab.setClosable(false);

        // Transactions Tab
        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setContent(createTransactionsTab());
        transactionsTab.setClosable(false);

        // Analytics Tab
        Tab analyticsTab = new Tab("Analytics");
        analyticsTab.setContent(createAnalyticsTab());
        analyticsTab.setClosable(false);

        tabPane.getTabs().addAll(accountsTab, transactionsTab, analyticsTab);
        return tabPane;
    }

    private VBox createAccountsTab() {
        VBox accountsTab = new VBox(10);
        accountsTab.setPadding(new Insets(10));

        // Account table
        accountTable = new TableView<>();
        accountTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Account, String> nameCol = new TableColumn<>("Account Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Account, AccountType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Account, BigDecimal> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setCellFactory(column -> new TableCell<Account, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("$" + String.format("%.2f", item));
                    if (item.compareTo(BigDecimal.ZERO) < 0) {
                        setTextFill(Color.RED);
                    } else {
                        setTextFill(Color.GREEN);
                    }
                }
            }
        });

        accountTable.getColumns().addAll(nameCol, typeCol, balanceCol);

        // Add account form
        HBox addAccountForm = new HBox(10);
        addAccountForm.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Name:");
        accountNameField = new TextField();
        accountNameField.setPromptText("Account name");

        Label typeLabel = new Label("Type:");
        accountTypeCombo = new ComboBox<>();
        accountTypeCombo.setItems(FXCollections.observableArrayList(AccountType.values()));

        Button addAccountBtn = new Button("Add Account");
        addAccountBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addAccountBtn.setOnAction(e -> addAccount());

        addAccountForm.getChildren().addAll(nameLabel, accountNameField, typeLabel, accountTypeCombo, addAccountBtn);

        accountsTab.getChildren().addAll(new Label("Accounts"), accountTable, addAccountForm);
        return accountsTab;
    }

    private VBox createTransactionsTab() {
        VBox transactionsTab = new VBox(10);
        transactionsTab.setPadding(new Insets(10));

        // Search bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search transactions...");
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> searchTransactions());
        searchBar.getChildren().addAll(new Label("Search:"), searchField, searchBtn);

        // Transaction table
        transactionTable = new TableView<>();
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Transaction, TransactionType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(column -> new TableCell<Transaction, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("$" + String.format("%.2f", item));
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if (transaction.isExpense()) {
                        setTextFill(Color.RED);
                    } else {
                        setTextFill(Color.GREEN);
                    }
                }
            }
        });

        TableColumn<Transaction, Category> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        transactionTable.getColumns().addAll(descCol, typeCol, amountCol, categoryCol, dateCol);

        // Add transaction form
        HBox addTransactionForm = new HBox(10);
        addTransactionForm.setAlignment(Pos.CENTER_LEFT);

        Label amountLabel = new Label("Amount:");
        transactionAmountField = new TextField();
        transactionAmountField.setPromptText("0.00");

        Label descLabel = new Label("Description:");
        transactionDescriptionField = new TextField();
        transactionDescriptionField.setPromptText("Transaction description");

        Label transTypeLabel = new Label("Type:");
        transactionTypeCombo = new ComboBox<>();
        transactionTypeCombo.setItems(FXCollections.observableArrayList(TransactionType.values()));

        Label catLabel = new Label("Category:");
        categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(Category.values()));

        Button addTransactionBtn = new Button("Add Transaction");
        addTransactionBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        addTransactionBtn.setOnAction(e -> addTransaction());

        addTransactionForm.getChildren().addAll(amountLabel, transactionAmountField, descLabel, 
                                              transactionDescriptionField, transTypeLabel, transactionTypeCombo,
                                              catLabel, categoryCombo, addTransactionBtn);

        transactionsTab.getChildren().addAll(new Label("Transactions"), searchBar, transactionTable, addTransactionForm);
        return transactionsTab;
    }

    private VBox createAnalyticsTab() {
        VBox analyticsTab = new VBox(10);
        analyticsTab.setPadding(new Insets(10));

        Label analyticsLabel = new Label("Financial Analytics");
        analyticsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Create analytics content
        VBox analyticsContent = new VBox(10);
        
        Button refreshAnalyticsBtn = new Button("Refresh Analytics");
        refreshAnalyticsBtn.setOnAction(e -> refreshAnalytics());
        
        TextArea analyticsText = new TextArea();
        analyticsText.setEditable(false);
        analyticsText.setPrefRowCount(15);

        analyticsContent.getChildren().addAll(refreshAnalyticsBtn, analyticsText);
        analyticsTab.getChildren().addAll(analyticsLabel, analyticsContent);

        // Store reference for updates
        analyticsTab.setUserData(analyticsText);

        return analyticsTab;
    }

    private HBox createBottomSection() {
        HBox bottomSection = new HBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10));
        bottomSection.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Button refreshBtn = new Button("Refresh Data");
        refreshBtn.setOnAction(e -> refreshData());

        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        exitBtn.setOnAction(e -> Platform.exit());

        bottomSection.getChildren().addAll(refreshBtn, exitBtn);
        return bottomSection;
    }

    private void addAccount() {
        try {
            String name = accountNameField.getText().trim();
            AccountType type = accountTypeCombo.getValue();

            if (name.isEmpty() || type == null) {
                showAlert("Error", "Please fill in all fields");
                return;
            }

            String accountId = "ACC_" + System.currentTimeMillis();
            Account account = new Account(accountId, name, type, BigDecimal.ZERO);
            financeService.addAccount(account);

            accountNameField.clear();
            accountTypeCombo.setValue(null);
            refreshData();
            showAlert("Success", "Account added successfully!");

        } catch (Exception e) {
            showAlert("Error", "Failed to add account: " + e.getMessage());
        }
    }

    private void addTransaction() {
        try {
            String amountText = transactionAmountField.getText().trim();
            String description = transactionDescriptionField.getText().trim();
            TransactionType type = transactionTypeCombo.getValue();
            Category category = categoryCombo.getValue();

            if (amountText.isEmpty() || description.isEmpty() || type == null || category == null) {
                showAlert("Error", "Please fill in all fields");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Error", "Amount must be positive");
                return;
            }

            // Get first account for demo (in real app, user would select)
            List<Account> accounts = financeService.getAllAccounts();
            if (accounts.isEmpty()) {
                showAlert("Error", "No accounts available. Please add an account first.");
                return;
            }

            String accountId = accounts.get(0).getId();
            TransactionService transactionService = financeService.getTransactionService();
            Transaction transaction = transactionService.createTransaction(accountId, type, amount, description, category);
            financeService.addTransaction(transaction);

            // Clear form
            transactionAmountField.clear();
            transactionDescriptionField.clear();
            transactionTypeCombo.setValue(null);
            categoryCombo.setValue(null);
            refreshData();
            showAlert("Success", "Transaction added successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        } catch (Exception e) {
            showAlert("Error", "Failed to add transaction: " + e.getMessage());
        }
    }

    private void searchTransactions() {
        String searchTerm = searchField.getText().trim();
        List<Transaction> results = financeService.searchTransactions(searchTerm);
        transactionTable.setItems(FXCollections.observableArrayList(results));
    }

    private void refreshData() {
        // Refresh accounts
        accountTable.setItems(FXCollections.observableArrayList(financeService.getAllAccounts()));

        // Refresh transactions
        transactionTable.setItems(FXCollections.observableArrayList(financeService.getAllTransactions()));

        // Refresh total balance
        BigDecimal totalBalance = financeService.getTotalBalance();
        totalBalanceLabel.setText("Total Balance: $" + String.format("%.2f", totalBalance));
        
        if (totalBalance.compareTo(BigDecimal.ZERO) < 0) {
            totalBalanceLabel.setTextFill(Color.RED);
        } else {
            totalBalanceLabel.setTextFill(Color.WHITE);
        }
    }

    private void refreshAnalytics() {
        TextArea analyticsText = (TextArea) ((VBox) ((Tab) ((TabPane) ((BorderPane) 
            ((Scene) ((Stage) totalBalanceLabel.getScene().getWindow()).getScene()).getRoot()).getCenter())
            .getTabs().get(2)).getContent()).getChildren().get(1);

        StringBuilder analytics = new StringBuilder();
        analytics.append("=== FINANCIAL ANALYTICS ===\n\n");
        
        // Total balance
        BigDecimal totalBalance = financeService.getTotalBalance();
        analytics.append("Total Balance: $").append(String.format("%.2f", totalBalance)).append("\n\n");
        
        // Account summary
        analytics.append("=== ACCOUNT SUMMARY ===\n");
        for (Account account : financeService.getAllAccounts()) {
            analytics.append(account.getName()).append(" (").append(account.getType().getDisplayName())
                   .append("): $").append(String.format("%.2f", account.getBalance())).append("\n");
        }
        
        // Recent transactions
        analytics.append("\n=== RECENT TRANSACTIONS ===\n");
        List<Transaction> recentTransactions = financeService.getAllTransactions();
        recentTransactions.stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(10)
                .forEach(transaction -> {
                    analytics.append(transaction.getType().getDisplayName()).append(": ")
                           .append(transaction.getDescription()).append(" - $")
                           .append(String.format("%.2f", transaction.getAmount())).append("\n");
                });

        analyticsText.setText(analytics.toString());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
