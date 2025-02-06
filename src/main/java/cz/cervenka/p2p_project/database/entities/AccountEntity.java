package cz.cervenka.p2p_project.database.entities;

import cz.cervenka.p2p_project.config.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountEntity {

    private int id;
    private int accountNumber;
    private Long balance;

    public AccountEntity() {}

    public AccountEntity(int id, int accountNumber, Long balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    /**
     * Finds an account by account number and bank ID.
     * @param accountNumber Number account is being found by.
     * @return Found account
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
                            rs.getLong("balance")
                    );
                }
            }
        }
        return null;
    }
    /**
     * Retrieves all accounts in database.
     * @return All valid accounts.
     */
    public static List<AccountEntity> getAll() throws SQLException {
        List<AccountEntity> products = new ArrayList<>();
        String sql = "SELECT * FROM account";
        try (Connection conn = ConnectionManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
                while (result.next()) {
                    products.add(new AccountEntity(
                            result.getInt("id"),
                            result.getInt("account_number"),
                            result.getLong("balance")
                    ));
                }
            }
        return products;
    }

    /**
     * Saves or updates the account in the database.
     */
    public void save() throws SQLException {
        String query;
        try (Connection conn = ConnectionManager.getConnection()) {
            if (this.id == 0) {
                // Insert new account
                query = "INSERT INTO account (account_number, balance) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, this.accountNumber);
                    stmt.setDouble(2, this.balance);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.id = rs.getInt(1);
                        }
                    }
                }
            } else {
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
    public void updateBalance(Long amount) throws SQLException {
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

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}