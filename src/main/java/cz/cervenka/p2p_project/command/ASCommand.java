package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;
import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.util.List;

public class ASCommand implements Command {
    private final AccountService accountService;
    private final BankService bankService;

    public ASCommand(AccountService accountService, BankService bankService) {
        this.accountService = accountService;
        this.bankService = bankService;
    }

    @Override
    public String execute(String[] parameters) {
        try {
            List<AccountEntity> accounts = accountService.getAccounts();

            if (accounts.isEmpty()) {
                return "ER No accounts available.";
            }

            StringBuilder result = new StringBuilder();

            for (AccountEntity account : accounts) {
                result.append(account.getAccountNumber())
                        .append("/").append(bankService.getBankCode())
                        .append(" || ");
            }

            return "AS " + result.toString().trim();
        } catch (Exception e) {
            return "ER An error occurred while processing accounts: " + e.getMessage();
        }
    }
}
