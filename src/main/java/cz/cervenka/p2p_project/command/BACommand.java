package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

/**
 * Command for retrieving the total funds in the bank.
 * Format: BA (no parameters expected).
 */
public class BACommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs a BACommand with the specified account service.
     *
     * @param accountService The service responsible for account operations.
     */
    public BACommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "BA" (Bank Amount) command.
     *
     * @param parameters The command arguments (expected to be empty).
     * @return The total funds in the bank or an error message.
     */
    @Override
    public String execute(String[] parameters) {
        if (parameters.length > 0) {
            return "ER Invalid format. Expected: BA (no parameters).";
        }

        try {
            long totalFunds = accountService.getTotalFunds();
            return "BA " + P2PServer.getBankCode() + " " + totalFunds;
        } catch (Exception e) {
            return "ER Unable to retrieve total funds: " + e.getMessage();
        }
    }
}
