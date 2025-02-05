/*
package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.config.ApplicationConfig;
import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class BankService {
    private final String bankCode;
    private final AccountService accountService;

    public BankService(AccountService accountService) throws IOException {
        this.bankCode = String.valueOf(getLocalIpAddress().getHostAddress());
        this.accountService = accountService;
    }

    */
/**
     * Retrieves the current bank's code (IP address).
     *
     * @return the bank code.
     *//*

    public String getBankCode() {
        return bankCode;
    }

    */
/**
     * Checks if the given bank code matches this bank.
     *
     * @param bankCode the bank's code (IP address).
     * @return true if it matches, false otherwise.
     *//*

    public boolean isValidBankCode(String bankCode) {
        return this.bankCode.equals(bankCode);
    }
}
*/
