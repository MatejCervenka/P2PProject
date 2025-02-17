package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AccountService {
    private static final ConcurrentHashMap<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    /**
     * Creates an account in the bank.
     * @return Account number if created, -1 if failed.
     */
    public int createAccount() {
        try {
            AccountEntity account = new AccountEntity();
            account.setAccountNumber(generateUniqueAccountNumber());
            account.setBalance(0L);
            account.save();
            accountLocks.put(account.getAccountNumber(), new ReentrantLock()); // Add lock for new account
            return account.getAccountNumber();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Deposits given amount of money into a specific account.
     * @param accountNumber Account to deposit into.
     * @param amount Amount to deposit.
     * @return true if deposited successfully, false otherwise.
     */
    public boolean deposit(int accountNumber, Long amount) {
        ReentrantLock lock = accountLocks.computeIfAbsent(accountNumber, k -> new ReentrantLock());
        lock.lock();
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null) {
                account.updateBalance(amount); // Database transaction handles atomic update
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * Withdraws given amount of money from a specific account.
     * @param accountNumber Account to withdraw from.
     * @param amount Amount to withdraw.
     * @return true if withdrawn successfully, false otherwise.
     */
    public boolean withdraw(int accountNumber, Long amount) {
        ReentrantLock lock = accountLocks.computeIfAbsent(accountNumber, k -> new ReentrantLock());
        lock.lock();
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null && account.getBalance() >= amount) {
                account.updateBalance(-amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * Retrieves a total balance of a specific account.
     * @param accountNumber Account number.
     * @return Account balance or -1 if failed.
     */
    public Long getBalance(int accountNumber) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            return (account != null) ? account.getBalance() : -1L;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Removes an account from the bank.
     * @param accountNumber Account number.
     * @return true if removed successfully, false otherwise.
     */
    public boolean removeAccount(int accountNumber) {
        ReentrantLock lock = accountLocks.computeIfAbsent(accountNumber, k -> new ReentrantLock());
        lock.lock();
        try {
            AccountEntity account = AccountEntity.findByAccountNumber(accountNumber);
            if (account != null && account.getBalance() == 0) {
                account.delete();
                accountLocks.remove(accountNumber); // Remove lock after deletion
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * Retrieves the total funds across all accounts.
     * @return The total balance of all accounts.
     */
    public Long getTotalFunds() {
        try {
            return AccountEntity.getTotalBalance();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Retrieves the total number of accounts in the system.
     * @return The number of accounts.
     */
    public Long getTotalAccounts() {
        try {
            return AccountEntity.getCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    /**
     * Generates a unique 5-digit account number.
     * @return Unique 5-digit number.
     */
    private int generateUniqueAccountNumber() {
        return (int) (Math.random() * 90000) + 10000;
    }
}
