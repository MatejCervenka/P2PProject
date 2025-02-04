package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.BankService;

public class BACommand implements Command {
    private final BankService bankService;

    public BACommand(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public String execute(String[] parameters) {
        long totalFunds = bankService.getTotalFunds();
        return "BA " + totalFunds;
    }
}
