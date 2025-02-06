package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

public class BNCommand implements Command {

    private final AccountService accountService;

    public BNCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String execute(String[] parameters) {
        try {
            if (parameters.length > 0) {
                return "ER Invalid format.";
            }
            long clientCount = accountService.getTotalAccounts();
            return "BN " + clientCount;
        } catch (Exception e) {
            return "ER Unable to retrieve client count.";
        }
    }
}