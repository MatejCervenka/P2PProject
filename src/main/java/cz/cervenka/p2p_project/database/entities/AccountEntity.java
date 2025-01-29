package cz.cervenka.p2p_project.database.entities;

import cz.cervenka.p2p_project.config.ConnectionManager;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountEntity {

    private int id;
    private int accountNumber;
    private double balance;
    private int bankId;

    public AccountEntity() {}

    public AccountEntity(int id, int accountNumber, double balance, int bankId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.bankId = bankId;
    }

    /**
     * Finds an account by account number and bank ID.
     */
    public static AccountEntity findByAccountNumberAndBankId(int accountNumber, int bankId) throws SQLException {
        String query = "SELECT * FROM account WHERE account_number = ? AND bank_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountNumber);
            stmt.setInt(2, bankId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AccountEntity(
                            rs.getInt("id"),
                            rs.getInt("account_number"),
                            rs.getDouble("balance"),
                            rs.getInt("bank_id")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Saves or updates the account in the database.
     */
    public void save() throws SQLException {
        String query;
        try (Connection conn = ConnectionManager.getConnection()) {
            if (this.id == 0) {
                // Insert new account
                query = "INSERT INTO account (account_number, balance, bank_id) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, this.accountNumber);
                    stmt.setDouble(2, this.balance);
                    stmt.setInt(3, this.bankId);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.id = rs.getInt(1);
                        }
                    }
                }
            } else {
                // Update existing account
                query = "UPDATE account SET balance = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setDouble(1, this.balance);
                    stmt.setInt(2, this.id);
                    stmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Updates the account balance.
     */
    public void updateBalance(double amount) throws SQLException {
        this.balance += amount;
        save();
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }
}
