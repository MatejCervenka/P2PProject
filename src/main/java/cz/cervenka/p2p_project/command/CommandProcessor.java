package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.config.ApplicationConfig;
import cz.cervenka.p2p_project.config.ConfigTimeout;
import cz.cervenka.p2p_project.database.entities.AccountEntity;
import cz.cervenka.p2p_project.network.NetworkClient;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.*;

public class CommandProcessor {

    private final BankService bankService;
    private final AccountService accountService;
    private final ExecutorService executor;
    private static final int PORT = ApplicationConfig.getInt("server.port");


    public CommandProcessor(BankService bankService, AccountService accountService) {
        this.bankService = bankService;
        this.accountService = accountService;
        this.executor = Executors.newCachedThreadPool();
    }

    public String processCommand(String rawInput) {
        Callable<String> task = () -> executeCommand(rawInput);
        Future<String> future = executor.submit(task);

        try {
            return future.get(ConfigTimeout.getCommandTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return "ER Command timeout exceeded (" + ConfigTimeout.getCommandTimeout() + "ms).";
        } catch (Exception e) {
            return "ER An error occurred: " + e.getMessage();
        }
    }

    private String executeCommand(String rawInput) {
        CommandParser.ParsedCommand parsedCommand = CommandParser.parse(rawInput);

        if (parsedCommand == null) {
            return "ER Invalid command format.";
        }

        String command = parsedCommand.getCommand();
        String[] params = parsedCommand.getParameters();

        try {
            return switch (command) {
                case "BC" -> processBC();
                case "AC" -> processAC();
                case "AD" -> processAD(params);
                case "AW" -> processAW(params);
                case "AB" -> processAB(params);
                case "AR" -> processAR(params);
                case "BA" -> processBA();
                case "BN" -> processBN();
                case "AS" -> processAS();
                default -> "ER Unknown command.";
            };
        } catch (Exception e) {
            return "ER An error occurred: " + e.getMessage();
        }
    }

    private String processBC() throws UnknownHostException {
        return "BC " + InetAddress.getLocalHost().getHostAddress();
    }

    private String processAC() {
        int accountNumber = accountService.createAccount();
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
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        Long depositAmount = Long.parseLong(params[1]);

        if (!bankService.isValidBankCode(bankCode)) {
            // Forward to another bank node
            return NetworkClient.sendCommand(bankCode, PORT, "AD " + accountNumber + "/" + bankCode + " " + depositAmount);
        }

        return accountService.deposit(accountNumber, depositAmount) ?
                "AD " + accountNumber + "/" + bankService.getBankCode() + " +" + depositAmount :
                "ER Failed to deposit money.";
    }

    private String processAW(String[] params) {
        if (params.length < 2) {
            return "ER Invalid format. Expected: AW <accountNumber>/<bankCode> <amount>";
        }

        String[] accountParts = params[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];
        Long withdrawalAmount = Long.parseLong(params[1]);

        if (!bankService.isValidBankCode(bankCode)) {
            // Forward to another bank node
            return NetworkClient.sendCommand(bankCode, PORT, "AW " + accountNumber + "/" + bankCode + " " + withdrawalAmount);
        }

        return accountService.withdraw(accountNumber, withdrawalAmount) ?
                "AW " + accountNumber + "/" + bankService.getBankCode() + " -" + withdrawalAmount :
                "ER Insufficient funds or failed to withdraw.";
    }

    private String processAB(String[] params) {
        if (params.length != 1) {
            return "ER Invalid format. Expected: AB <accountNumber>/<bankCode>";
        }

        String[] accountParts = params[0].split("/");
        if (accountParts.length != 2) {
            return "ER Invalid account format.";
        }

        int accountNumber = Integer.parseInt(accountParts[0]);
        String bankCode = accountParts[1];

        if (!bankService.isValidBankCode(bankCode)) {
            // Forward to another bank node
            return NetworkClient.sendCommand(bankCode, PORT, "AB " + accountNumber + "/" + bankCode);
        }

        double balance = accountService.getBalance(accountNumber);
        return balance >= 0 ? "AB " + balance : "ER Failed to retrieve balance.";
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

        if (!bankService.isValidBankCode(bankCode)) {
            return "ER Invalid bank code.";
        }

        if (accountService.removeAccount(accountNumber)) {
            return "AR";
        }
        return "ER Cannot delete account with non-zero balance.";
    }

    private String processBA() {
        return "BA " + bankService.getTotalFunds();
    }

    private String processBN() {
        return "BN " + bankService.getClientCount();
    }

    private String processAS() {
        try {
            List<AccountEntity> accounts = accountService.getAccounts();

            if (accounts.isEmpty()) {
                return "ER No accounts available.";
            }

            StringBuilder result = new StringBuilder();

            for (AccountEntity account : accounts) {
                result.append(account.getAccountNumber())
                        .append("/").append(bankService.getBankCode())
                        .append("  ||  ");
            }

            return "AS " + result.toString().trim();
        } catch (Exception e) {
            return "ER An error occurred while processing accounts: " + e.getMessage();
        }
    }
}
