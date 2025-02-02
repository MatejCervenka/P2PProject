package cz.cervenka.p2p_project.services;

import cz.cervenka.p2p_project.config.ApplicationConfig;
import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.net.UnknownHostException;
import java.util.List;

public class BankService {
    private final String bankCode;
    private final AccountService accountService;

    public BankService(AccountService accountService) throws UnknownHostException {
        this.bankCode = ApplicationConfig.getProperty("server.host.address");
        this.accountService = accountService;
    }

    /**
     * Retrieves the current bank's code (IP address).
     * @return the bank code.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Checks if the given bank code matches this bank.
     * @param bankCode the bank's code (IP address).
     * @return true if it matches, false otherwise.
     */
    public boolean isValidBankCode(String bankCode) {
        return this.bankCode.equals(bankCode);
    }

    /**
     * Retrieves the total funds across all accounts.
     * @return The total balance in the system.
     */
    public Long getTotalFunds() {
        return accountService.getTotalFunds();
    }

    /**
     * Retrieves the number of accounts (clients).
     * @return The number of accounts in the system.
     */
    public Long getClientCount() {
        return accountService.getTotalAccounts();
    }

    public List<AccountEntity> getAccounts() {
        return accountService.getAccounts();
    }
}
