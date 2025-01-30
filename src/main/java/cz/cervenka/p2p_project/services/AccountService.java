package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    public int createAccount() {
        try {
            AccountEntity account = new AccountEntity();
            account.setAccountNumber(generateUniqueAccountNumber());
            account.setBalance(0L);
            account.save();
            return account.getAccountNumber();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Error code for failure
        }
    }

    public boolean deposit(int accountNumber, Long amount) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null) {
                account.updateBalance(amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdraw(int accountNumber, Long amount) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null && account.getBalance() >= amount) {
                account.updateBalance(-amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Long getBalance(int accountNumber) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null) {
                return account.getBalance();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    public boolean removeAccount(int accountNumber) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null && account.getBalance() == 0) {
                account.delete();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int generateUniqueAccountNumber() {
        // Generate a random 5-digit unique account number.
        return (int) (Math.random() * 90000) + 10000;
    }

    /**
     * Retrieves the total funds across all accounts.
     * @return The total balance of all accounts.
     */
    public Long getTotalFunds() {
        Long totalFunds = 0L;
        try {
            // Assume you have a method that gets all accounts.
            List<AccountEntity> allAccounts = AccountEntity.getAll(); // This method would return all accounts in the database.
            for (AccountEntity account : allAccounts) {
                totalFunds += account.getBalance();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalFunds;
    }

    /**
     * Retrieves the total number of accounts in the system.
     * @return The number of accounts.
     */
    public int getTotalAccounts() {
        int accountCount = 0;
        try {
            // Assume you have a method that gets all accounts.
            List<AccountEntity> allAccounts = AccountEntity.getAll(); // This method would return all accounts in the database.
            accountCount = allAccounts.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountCount;
    }

    public List<AccountEntity> getAccounts() {
        List<AccountEntity> allAccounts = new ArrayList<>();
        try {
            allAccounts = AccountEntity.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allAccounts;
    }
}
