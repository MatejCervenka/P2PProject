package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.database.entities.BankEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class BankService {
    private final String bankCode;
    private final int bankId;

    public BankService() throws UnknownHostException, SQLException {
        this.bankCode = InetAddress.getLocalHost().getHostAddress();
        this.bankId = bankCode.hashCode();

        // Ensure the bank exists in the database
        BankEntity bank = BankEntity.findByCode(bankCode);
        if (bank == null) {
            bank = new BankEntity();
            bank.setIpAddress(bankCode);
            bank.setId(bankId);
            bank.save();
        }
    }

    /**
     * Retrieves the current bank's unique identifier.
     * @return the bank ID.
     */
    public int getBankId() {
        return bankId;
    }

    /**
     * Retrieves the unique bank ID for a given bank code.
     * @param bankCode the bank's code (IP address).
     * @return the bank ID or -1 if the code does not match this bank.
     */
    public int getBankIdByCode(String bankCode) {
        if (this.bankCode.equals(bankCode)) {
            return this.bankId;
        }
        return -1; // Return -1 if the bank code doesn't match this bank.
    }

    /**
     * Retrieves the current bank's code (IP address).
     * @return the bank code.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Handles the BA (Bank Total Amount) command.
     *
     * @return The total funds across all accounts in the bank.
     */
    public double getTotalFunds() {
        try {
            BankEntity bank = BankEntity.findByCode(bankCode);
            if (bank != null) {
                return bank.getTotalFunds();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1.0; // Return an error code or negative value in case of failure.
    }

    /**
     * Handles the BN (Bank Number of Clients) command.
     *
     * @return The number of accounts (clients) in the bank.
     */
    public int getClientCount() {
        try {
            BankEntity bank = BankEntity.findByCode(bankCode);
            if (bank != null) {
                return bank.getAccountCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return an error code in case of failure.
    }
}
