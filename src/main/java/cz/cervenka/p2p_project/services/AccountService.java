package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class including methods operating with database system.
 */
public class AccountService {

    /**
     * Creates account in bank.
     * @return Byte value whether account was created or not.
     */
    public int createAccount() {
        try {
            AccountEntity account = new AccountEntity();
            account.setAccountNumber(generateUniqueAccountNumber());
            account.setBalance(0L);
            account.save();
            return account.getAccountNumber();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Deposits given amount of money into specific account.
     * @param accountNumber Account where money is being deposited to.
     * @param amount Amount of money being deposited.
     * @return Byte value whether money was deposited or not.
     */
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

    /**
     * Withdraws given amount of money from specific account.
     * @param accountNumber Account where money is being withdrawn from.
     * @param amount Amount of money being withdrawn.
     * @return Byte value whether money was withdrawn or not.
     */
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

    /**
     * Retrieves a total balance of specific account.
     * @param accountNumber Account from which balance is getting retrieved.
     * @return Balance of specific account.
     */
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

    /**
     * Removes account from bank.
     * @param accountNumber Account which is being removed.
     * @return Byte value whether account was removed or not.
     */
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

    /**
     * Generates a random 5-digit number representing number of account.
     * @return Random 5-digit number.
     */
    private int generateUniqueAccountNumber() {
        return (int) (Math.random() * 90000) + 10000;
    }

    /**
     * Retrieves the total funds across all accounts.
     * @return The total balance of all accounts.
     */
    public Long getTotalFunds() {
        Long totalFunds = 0L;
        try {
            List<AccountEntity> allAccounts = AccountEntity.getAll();
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
    public Long getTotalAccounts() {
        long accountCount = 0L;
        try {
            List<AccountEntity> allAccounts = AccountEntity.getAll();
            accountCount = allAccounts.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountCount;
    }

    /**
     * Retrieves all accounts in the system.
     * @return The list of accounts.
     */
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