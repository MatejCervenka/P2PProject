package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;
import cz.cervenka.p2p_project.config.ApplicationConfig;

public class AWCommand implements Command {
    private final AccountService accountService;
    private final BankService bankService;
    private static final int PORT = ApplicationConfig.getInt("server.port");

    public AWCommand(AccountService accountService, BankService bankService) {
        this.accountService = accountService;
        this.bankService = bankService;
    }

    @Override
    public String execute(String[] parameters) {
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

        if (!bankService.isValidBankCode(bankCode)) {
            return NetworkClient.sendCommand(bankCode, PORT, "AW " + accountNumber + "/" + bankCode + " " + withdrawalAmount);
        }

        return accountService.withdraw(accountNumber, withdrawalAmount)
                ? "AW " + accountNumber + "/" + bankService.getBankCode() + " -" + withdrawalAmount
                : "ER Insufficient funds or invalid withdrawal.";
    }
}
