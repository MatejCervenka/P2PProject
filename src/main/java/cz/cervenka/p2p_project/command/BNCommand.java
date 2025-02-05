package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

public class BNCommand implements Command {

    private final AccountService accountService;

    public BNCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String execute(String[] parameters) {
        long clientCount = accountService.getTotalAccounts();
        return "BN " + clientCount;
    }
}
