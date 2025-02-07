package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

/**
 * Command for retrieving the balance of a specified account.
 * The account balance can be retrieved either locally or from a remote bank.
 */
public class ABCommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs an ABCommand with the specified account service.
     * @param accountService The service handling account operations.
     */
    public ABCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "AB" (Account Balance) command.
     * If the provided bank code matches the local bank, the balance is retrieved locally.
     * Otherwise, the request is forwarded to the appropriate bank.
     *
     * @param parameters Array containing the account number and bank code in the format: "accountNumber/bankCode".
     * @return The balance of the specified account, or an error message if the operation fails.
     * @throws IOException If a network error occurs while forwarding the request.
     */
    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length < 1) {
            return "ER Invalid format. Expected: AB <accountNumber>/<bankCode>";
        }

        String[] accountParts = parameters[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];

        if (!P2PServer.isValidBankCode(bankCode)) {
            return NetworkClient.sendCommand(bankCode, "AB " + accountNumber + "/" + bankCode);
        }

        long balance = accountService.getBalance(accountNumber);
        return (balance != -1)
                ? "AB " + balance
                : "ER Unable to retrieve account balance.";

        /* Alternative response format:
        return (balance != -1)
                ? "AB " + accountNumber + "/" + P2PServer.getBankCode() + " " + balance
                : "ER Unable to retrieve account balance.";
        */
    }
}
