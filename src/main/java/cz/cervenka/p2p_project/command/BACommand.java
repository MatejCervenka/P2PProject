package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

public class BACommand implements Command {
    private final AccountService accountService;

    public BACommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String execute(String[] parameters) {
        long totalFunds = accountService.getTotalFunds();
        return "BA " + totalFunds;
    }
}
