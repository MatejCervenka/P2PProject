package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.sql.SQLException;

public class AccountService {

    public int createAccount(int bankId) {
        try {
            AccountEntity account = new AccountEntity();
            account.setBankId(bankId);
            account.setAccountNumber(generateUniqueAccountNumber());
            account.setBalance(0.0);
            account.save();
            return account.getAccountNumber();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Error code for failure
        }
    }

    public boolean deposit(int accountNumber, int bankId, double amount) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumberAndBankId(accountNumber, bankId);
            if (account != null) {
                account.updateBalance(amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdraw(int accountNumber, int bankId, double amount) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumberAndBankId(accountNumber, bankId);
            if (account != null && account.getBalance() >= amount) {
                account.updateBalance(-amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getBalance(int accountNumber, int bankId) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumberAndBankId(accountNumber, bankId);
            if (account != null) {
                return account.getBalance();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1.0;
    }

    public boolean removeAccount(int accountNumber, int bankId) {
        try {
            AccountEntity account = AccountEntity.findByAccountNumberAndBankId(accountNumber, bankId);
            if (account != null && account.getBalance() == 0.0) {
                account.delete();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int generateUniqueAccountNumber() {
        // Generate a random 8-digit unique account number.
        return (int) (Math.random() * 90000000) + 10000000;
    }
}
