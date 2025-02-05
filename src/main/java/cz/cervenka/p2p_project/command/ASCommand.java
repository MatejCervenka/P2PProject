package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.util.List;

public class ASCommand implements Command {
    private final AccountService accountService;

    public ASCommand(AccountService accountService) {
        this.accountService = accountService;
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
                        .append("/").append(P2PServer.getBankCode())
                        .append(" || ");
            }

            return "AS " + result.toString().trim();
        } catch (Exception e) {
            return "ER An error occurred while processing accounts: " + e.getMessage();
        }
    }
}
