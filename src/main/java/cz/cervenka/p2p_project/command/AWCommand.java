package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

/**
 * Command for withdrawing money from a bank account.
 * Format: AW <accountNumber>/<bankCode> <amount>
 */
public class AWCommand implements Command {
    private final AccountService accountService;

    /**
     * Constructs an AWCommand with the specified account service.
     *
     * @param accountService The service responsible for account operations.
     */
    public AWCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Executes the "AW" (Account Withdraw) command.
     *
     * @param parameters The command arguments, expected format: [accountNumber/bankCode, amount].
     * @return A success message or an error response.
     * @throws IOException If an issue occurs while processing.
     */
    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length < 2) {
            return "ER Invalid format. Expected: AW <accountNumber>/<bankCode> <amount>";
        }

        String[] accountParts = parameters[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format. Expected: <accountNumber>/<bankCode>";
        }

        try {
            int accountNumber = Integer.parseInt(accountParts[0]);
            String bankCode = accountParts[1];
            long withdrawalAmount = Long.parseLong(parameters[1]);

            if (withdrawalAmount <= 0) {
                return "ER Invalid withdrawal amount. Must be greater than zero.";
            }

            if (!P2PServer.isValidBankCode(bankCode)) {
                return NetworkClient.sendCommand(bankCode, "AW " + accountNumber + "/" + bankCode + " " + withdrawalAmount);
            }

            return accountService.withdraw(accountNumber, withdrawalAmount)
                    ? "AW" : "ER Insufficient funds or invalid withdrawal.";

        } catch (NumberFormatException e) {
            return "ER Invalid number format. Account number and amount must be numeric.";
        }

        /*return accountService.withdraw(accountNumber, withdrawalAmount)
                ? "AW " + accountNumber + "/" + P2PServer.getBankCode() + " -" + withdrawalAmount
                : "ER Insufficient funds or invalid withdrawal.";*/
    }
}
