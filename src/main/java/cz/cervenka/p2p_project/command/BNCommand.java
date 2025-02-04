package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.BankService;

public class BNCommand implements Command {

    private final BankService bankService;

    public BNCommand(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public String execute(String[] parameters) {
        long clientCount = bankService.getClientCount();
        return "BN " + clientCount;
    }
}
