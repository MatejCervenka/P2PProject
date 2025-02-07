package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

/**
 * Command for removing a bank account.
 * An account can only be removed if it exists and has a zero balance.
 */
public class ARCommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs an ARCommand with the specified account service.
     *
     * @param accountService The service responsible for managing bank accounts.
     */
    public ARCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "AR" (Account Remove) command.
     * Removes the specified account if it exists and has a zero balance.
     *
     * @param parameters An array containing:
     *                   - The account number and bank code in the format "accountNumber/bankCode".
     * @return A success response `"AR"` if the account is removed, or an error message if removal fails.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length < 1) {
            return "ER Invalid format. Expected: AR <accountNumber>/<bankCode>";
        }

        String[] accountParts = parameters[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];

        if (!P2PServer.isValidBankCode(bankCode)) {
            return "ER Invalid bank code.";
        }

        return accountService.removeAccount(accountNumber)
                ? "AR" : "ER Cannot remove account (not found or nonzero balance).";

        /*return accountService.removeAccount(accountNumber)
                ? "AR " + accountNumber + "/" + P2PServer.getBankCode() + " Removed"
                : "ER Cannot remove account (not found or nonzero balance).";*/
    }
}
