package cz.cervenka.p2p_project.database.entities;

import cz.cervenka.p2p_project.config.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountEntity {

    private int id;
    private int accountNumber;
    private Long balance;
    private int version; // Optimistic locking

    public AccountEntity() {}

    public AccountEntity(int id, int accountNumber, Long balance, int version) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.version = version;
    }

    /**
     * Finds an account by account number.
     * @param accountNumber Account number.
     * @return AccountEntity object if found, otherwise null.
     */
    public static AccountEntity findByAccountNumber(int accountNumber) throws SQLException {
        String query = "SELECT * FROM account WHERE account_number = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AccountEntity(
                            rs.getInt("id"),
                            rs.getInt("account_number"),
                            rs.getLong("balance"),
                            rs.getInt("version")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all accounts from the database.
     * @return List of all accounts.
     */
    public static List<AccountEntity> getAll() throws SQLException {
        List<AccountEntity> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        try (Connection conn = ConnectionManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                accounts.add(new AccountEntity(
                        result.getInt("id"),
                        result.getInt("account_number"),
                        result.getLong("balance"),
                        result.getInt("version")
                ));
            }
        }
        return accounts;
    }

    /**
     * Saves a new account or updates an existing one.
     */
    public void save() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            if (this.id == 0) {
                // Insert new account
                String query = "INSERT INTO account (account_number, balance, version) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, this.accountNumber);
                    stmt.setLong(2, this.balance);
                    stmt.setInt(3, 0); // Initial version 0
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.id = rs.getInt(1);
                        }
                    }
                }
            } else {
                // Update existing account
                String query = "UPDATE account SET balance = ?, version = version + 1 WHERE id = ? AND version = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setLong(1, this.balance);
                    stmt.setInt(2, this.id);
                    stmt.setInt(3, this.version);
                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated == 0) {
                        throw new SQLException("Optimistic locking failure: Account updated by another transaction.");
                    }
                    this.version++; // Update local version
                }
            }
        }
    }

    /**
     * Updates the account balance using optimistic locking.
     */
    public void updateBalance(Long amount) throws SQLException {
        int retries = 3;
        while (retries > 0) {
            AccountEntity freshAccount = findByAccountNumber(this.accountNumber);
            if (freshAccount == null) throw new SQLException("Account not found.");

            freshAccount.balance += amount;
            try {
                freshAccount.save();
                this.balance = freshAccount.balance;
                this.version = freshAccount.version;
                return;
            } catch (SQLException e) {
                retries--;
                if (retries == 0) throw e; // Fail after max retries
            }
        }
    }

    /**
     * Deletes the account from the database.
     */
    public void delete() throws SQLException {
        String query = "DELETE FROM account WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves the total balance across all accounts.
     * @return Total balance.
     */
    public static Long getTotalBalance() throws SQLException {
        String query = "SELECT SUM(balance) AS total_balance FROM account";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getLong("total_balance");
            }
        }
        return 0L;
    }

    /**
     * Retrieves the total number of accounts.
     * @return Total account count.
     */
    public static Long getCount() throws SQLException {
        String query = "SELECT COUNT(*) AS total_accounts FROM account";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getLong("total_accounts");
            }
        }
        return 0L;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public int getVersion() {
        return version;
    }
}
