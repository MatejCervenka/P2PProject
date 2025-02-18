package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

/**
 * Command for depositing money into a bank account.
 * Handles both local and remote deposits by forwarding requests to the appropriate bank node if necessary.
 */
public class ADCommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs an ADCommand with the specified account service.
     * @param accountService The service responsible for handling account transactions.
     */
    public ADCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "AD" (Account Deposit) command.
     * Deposits the specified amount into the given account. If the account belongs to a remote bank,
     * the command is forwarded using the network client.
     *
     * @param parameters An array containing:
     *                   - The account number and bank code in the format "accountNumber/bankCode".
     *                   - The amount to deposit.
     * @return A success response `"AD"` if the deposit is successful, or an error message in case of failure.
     * @throws IOException If an I/O error occurs during network communication.
     */
    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length < 2) {
            return "ER Invalid format. Expected: AD <accountNumber>/<bankCode> <amount>";
        }

        String[] accountParts = parameters[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        long depositAmount = Long.parseLong(parameters[1]);

        if (!P2PServer.isValidBankCode(bankCode)) {
            return NetworkClient.sendCommand(bankCode, "AD " + accountNumber + "/" + bankCode + " " + depositAmount);
        }

        return accountService.deposit(accountNumber, depositAmount)
                ? "AD" : "ER Failed to deposit money.";
    }
}