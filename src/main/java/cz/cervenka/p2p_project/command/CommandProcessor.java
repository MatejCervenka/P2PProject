package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommandProcessor {

    private BankService bankService;
    private AccountService accountService;

    public CommandProcessor() {
    }

    public CommandProcessor(BankService bankService, AccountService accountService) {
        this.bankService = bankService;
        this.accountService = accountService;
    }

    public String processCommand(String rawInput) {
        CommandParser.ParsedCommand parsedCommand = CommandParser.parse(rawInput);

        if (parsedCommand == null) {
            return "ER Invalid command format.";
        }

        String command = parsedCommand.getCommand();
        String[] params = parsedCommand.getParameters();

        try {
            switch (command) {
                case "BC":
                    return processBC();
                case "AC":
                    return processAC();
                case "AD":
                    return processAD(params);
                case "AW":
                    return processAW(params);
                case "AB":
                    return processAB(params);
                case "AR":
                    return processAR(params);
                case "BA":
                    return processBA();
                case "BN":
                    return processBN();
                default:
                    return "ER Unknown command.";
            }
        } catch (Exception e) {
            return "ER An error occurred: " + e.getMessage();
        }
    }

    private String processBC() throws UnknownHostException {
        String bankCode = InetAddress.getLocalHost().getHostAddress();
        return "BC " + bankCode;
    }

    private String processAC() {
        int accountNumber = accountService.createAccount(bankService.getBankId());
        if (accountNumber != -1) {
            return "AC " + accountNumber + "/" + bankService.getBankCode();
        }
        return "ER Unable to create a new account.";
    }

    private String processAD(String[] params) {
        if (params.length < 2) {
            return "ER Invalid format. Expected: AD <accountNumber>/<bankCode> <amount>";
        }

        String[] accountParts = params[0].split("/");
        if (accountParts.length != 2 || !CommandParser.isValidAccountNumber(accountParts[0]) || !CommandParser.isValidBankCode(accountParts[1])) {
            return "ER Invalid account format.";
        }

        String amount = params[1];
        if (!CommandParser.isValidAmount(amount)) {
            return "ER Invalid amount format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        double depositAmount = Double.parseDouble(amount);

        if (accountService.deposit(accountNumber, bankService.getBankIdByCode(bankCode), depositAmount)) {
            return "AD";
        }
        return "ER Failed to deposit money.";
    }

    private String processAW(String[] params) {
        if (params.length < 2) {
            return "ER Invalid format. Expected: AW <accountNumber>/<bankCode> <amount>";
        }

        String[] accountParts = params[0].split("/");
        if (accountParts.length != 2 || !CommandParser.isValidAccountNumber(accountParts[0]) || !CommandParser.isValidBankCode(accountParts[1])) {
            return "ER Invalid account format.";
        }

        String amount = params[1];
        if (!CommandParser.isValidAmount(amount)) {
            return "ER Invalid amount format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        double withdrawalAmount = Double.parseDouble(amount);

        if (accountService.withdraw(accountNumber, bankService.getBankIdByCode(bankCode), withdrawalAmount)) {
            return "AW";
        }
        return "ER Insufficient funds or failed to withdraw.";
    }

    private String processAB(String[] params) {
        if (params.length != 1) {
            return "ER Invalid format. Expected: AB <accountNumber>/<bankCode>";
        }

        String[] accountParts = params[0].split("/");
        if (accountParts.length != 2 || !CommandParser.isValidAccountNumber(accountParts[0]) || !CommandParser.isValidBankCode(accountParts[1])) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        double balance = accountService.getBalance(accountNumber, bankService.getBankIdByCode(bankCode));

        if (balance >= 0) {
            return "AB " + balance;
        }
        return "ER Failed to retrieve balance.";
    }

    private String processAR(String[] params) {
        if (params.length != 1) {
            return "ER Invalid format. Expected: AR <accountNumber>/<bankCode>";
        }

        String[] accountParts = params[0].split("/");
        if (accountParts.length != 2 || !CommandParser.isValidAccountNumber(accountParts[0]) || !CommandParser.isValidBankCode(accountParts[1])) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];

        if (accountService.removeAccount(accountNumber, bankService.getBankIdByCode(bankCode))) {
            return "AR";
        }
        return "ER Cannot delete account with non-zero balance.";
    }

    private String processBA() {
        double totalAmount = bankService.getTotalFunds();
        return "BA " + totalAmount;
    }

    private String processBN() {
        int clientCount = bankService.getClientCount();
        return "BN " + clientCount;
    }
}
