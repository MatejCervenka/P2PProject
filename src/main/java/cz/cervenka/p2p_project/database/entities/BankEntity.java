package cz.cervenka.p2p_project.database.entities;

import cz.cervenka.p2p_project.config.ConnectionManager;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankEntity {

    private int id;
    private String ipAddress;

    public BankEntity() {
    }

    public BankEntity(int id, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
    }


    /**
     * Finds a bank by its code.
     *
     * @param bankCode The bank's code (IP address).
     * @return A BankEntity instance or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public static BankEntity findByCode(String bankCode) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "SELECT * FROM bank WHERE ip_address = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, bankCode);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new BankEntity(
                            result.getInt("id"),
                            result.getString("ip_address")
                    );
                }
            }
            return null;
        }
    }

    /**
     * Get the total funds in the bank.
     *
     * @return The sum of all balances in the bank's accounts.
     */
    public double getTotalFunds() throws SQLException {
        String query = "SELECT SUM(balance) AS total FROM account WHERE bank_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Get the number of accounts in the bank.
     *
     * @return The count of accounts in the bank.
     */
    public int getAccountCount() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM account WHERE bank_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    public static BankEntity findById(int id) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "SELECT * FROM bank WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    return new BankEntity(
                            result.getInt("id"),
                            result.getString("ip_address")
                    );
                }
            }
            return null;
        }
    }

    public static List<BankEntity> getAll() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            List<BankEntity> banks = new ArrayList<>();
            String sql = "SELECT * FROM bank";
            try (Statement statement = conn.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {
                while (result.next()) {
                    banks.add(new BankEntity(
                            result.getInt("id"),
                            result.getString("ip_address")
                    ));
                }
            }
            return banks;
        }
    }

    public void save() throws SQLException {
        String sql;
        try (Connection conn = ConnectionManager.getConnection()) {
            if (this.id == 0) {
                sql = "INSERT INTO bank (ip_address) VALUES (?)";
                try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, this.ipAddress);
                    statement.executeUpdate();
                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                }
            } else {
                sql = "UPDATE bank SET ip_address = ? WHERE id = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setString(1, this.ipAddress);
                    statement.setInt(2, this.id);
                    statement.executeUpdate();
                }
            }
        }
    }

    public void delete() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            if (this.id != 0) {
                String sql = "DELETE FROM bank WHERE id = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setInt(1, this.id);
                    statement.executeUpdate();
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
