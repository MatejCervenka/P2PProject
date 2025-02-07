package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

/**
 * Command for creating a new bank account.
 * Generates a unique account number and associates it with the local bank code.
 */
public class ACCommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs an ACCommand with the specified account service.
     * @param accountService The service responsible for managing accounts.
     */
    public ACCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "AC" (Account Create) command.
     * A new account is created with a unique account number, and the local bank code is assigned.
     *
     * @param parameters This command does not require any parameters.
     * @return A success response with the format "AC accountNumber/bankCode",
     *         or an error message if the account creation fails.
     * @throws IOException If an I/O error occurs (not expected in this method).
     */
    @Override
    public String execute(String[] parameters) throws IOException {
        int accountNumber = accountService.createAccount();
        return (accountNumber != -1)
                ? "AC " + accountNumber + "/" + P2PServer.getBankCode()
                : "ER Unable to create a new account.";
    }
}
