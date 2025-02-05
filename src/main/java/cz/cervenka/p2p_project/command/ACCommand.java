package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

public class ACCommand implements Command {
    private final AccountService accountService;

    public ACCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String execute(String[] parameters) throws IOException {
        int accountNumber = accountService.createAccount();
        return (accountNumber != -1)
                ? "AC " + accountNumber + "/" + P2PServer.getBankCode()
                : "ER Unable to create a new account.";
    }
}
