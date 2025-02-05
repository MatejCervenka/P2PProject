package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.server.P2PServer;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.config.ApplicationConfig;

import java.io.IOException;

public class ADCommand implements Command {
    private final AccountService accountService;
    private static final int PORT = ApplicationConfig.getInt("server.port");

    public ADCommand(AccountService accountService) {
        this.accountService = accountService;
    }

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
                ? "AD " + accountNumber + "/" + P2PServer.getBankCode() + " +" + depositAmount
                : "ER Failed to deposit money.";
    }
}
