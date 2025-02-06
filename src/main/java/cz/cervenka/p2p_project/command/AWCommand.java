package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;

public class AWCommand implements Command {
    private final AccountService accountService;

    public AWCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length < 2) {
            return "ER Invalid format. Expected: AW <accountNumber>/<bankCode> <amount>";
        }

        String[] accountParts = parameters[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        long withdrawalAmount = Long.parseLong(parameters[1]);

        if (!P2PServer.isValidBankCode(bankCode)) {
            return NetworkClient.sendCommand(bankCode, "AW " + accountNumber + "/" + bankCode + " " + withdrawalAmount);
        }

        return accountService.withdraw(accountNumber, withdrawalAmount)
                ? "AW" : "ER Insufficient funds or invalid withdrawal.";

        /*return accountService.withdraw(accountNumber, withdrawalAmount)
                ? "AW " + accountNumber + "/" + P2PServer.getBankCode() + " -" + withdrawalAmount
                : "ER Insufficient funds or invalid withdrawal.";*/
    }
}
