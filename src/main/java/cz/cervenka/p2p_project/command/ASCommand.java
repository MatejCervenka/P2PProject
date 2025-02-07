package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.database.entities.AccountEntity;

import java.util.List;

/**
 * Command for retrieving all account numbers managed by the bank.
 * The command returns a list of accounts in the format:
 * AS <accountNumber>/<bankCode> || <accountNumber>/<bankCode> || ...
 */
public class ASCommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs an ASCommand with the specified account service.
     *
     * @param accountService The service responsible for retrieving bank accounts.
     */
    public ASCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "AS" (Account Show) command.
     * Retrieves a list of all bank accounts stored in the system.
     *
     * @param parameters An empty array (since this command requires no parameters).
     * @return A formatted list of accounts if available, or an error message if none exist.
     */
    @Override
    public String execute(String[] parameters) {
        try {
            if (parameters.length > 0) {
                return "ER Invalid format.";
            }

            List<AccountEntity> accounts = accountService.getAccounts();

            if (accounts.isEmpty()) {
                return "ER No accounts available.";
            }

            StringBuilder result = new StringBuilder("AS ");

            for (AccountEntity account : accounts) {
                result.append(account.getAccountNumber())
                        .append("/").append(P2PServer.getBankCode())
                        .append(" || ");
            }

            return result.substring(0, result.length() - 4);
        } catch (Exception e) {
            return "ER An error occurred while processing accounts: " + e.getMessage();
        }
    }
}
