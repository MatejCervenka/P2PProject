package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

/**
 * Command for retrieving the total number of bank clients.
 * Format: BN (no parameters expected).
 */
public class BNCommand implements Command {

    private final AccountService accountService;

    /**
     * Constructs a BNCommand with the specified account service.
     *
     * @param accountService The service responsible for account operations.
     */
    public BNCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "BN" (Bank Number) command.
     *
     * @param parameters The command arguments (expected to be empty).
     * @return The total number of clients in the bank or an error message.
     */
    @Override
    public String execute(String[] parameters) {
        try {
            if (parameters.length > 0) {
                return "ER Invalid format. Expected: BN (no parameters).";
            }
            long clientCount = accountService.getTotalAccounts();
            return "BN " + clientCount;
        } catch (Exception e) {
            return "ER Unable to retrieve client count.";
        }
    }
}
