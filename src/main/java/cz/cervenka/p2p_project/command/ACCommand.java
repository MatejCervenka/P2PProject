package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;

public class ACCommand implements Command {
    private final AccountService accountService;
    private final BankService bankService;

    public ACCommand(AccountService accountService, BankService bankService) {
        this.accountService = accountService;
        this.bankService = bankService;
    }

    @Override
    public String execute(String[] parameters) {
        int accountNumber = accountService.createAccount();
        return (accountNumber != -1)
                ? "AC " + accountNumber + "/" + bankService.getBankCode()
                : "ER Unable to create a new account.";
    }
}
