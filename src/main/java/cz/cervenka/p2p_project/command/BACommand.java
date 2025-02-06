package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

public class BACommand implements Command {
    private final AccountService accountService;

    public BACommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String execute(String[] parameters) {
        try {
            if (parameters.length > 0) {
                return "ER Invalid format.";
            }
            long totalFunds = accountService.getTotalFunds();
            return "BA " + totalFunds;
        } catch (Exception e) {
            return "ER Unable to retrieve total funds in bank.";
        }
    }
}
