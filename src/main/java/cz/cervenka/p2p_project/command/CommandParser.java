package cz.cervenka.p2p_project.command;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    public static class ParsedCommand {
        private final String command;
        private final String[] parameters;

        public ParsedCommand(String command, String[] parameters) {
            this.command = command;
            this.parameters = parameters;
        }

        public String getCommand() {
            return command;
        }

        public String[] getParameters() {
            return parameters;
        }
    }

    public static ParsedCommand parse(String rawInput) {
        rawInput = rawInput.trim();
        if (rawInput.isEmpty()) {
            return null;
        }

        String[] parts = rawInput.split(" ");
        String command = parts[0].toUpperCase();
        String[] parameters = parts.length > 1 ? parts[1].split("/") : new String[0];

        return new ParsedCommand(command, parameters);
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{5}");
    }

    public static boolean isValidAmount(String amount) {
        return amount.matches("\\d+(\\.\\d{1,2})?");
    }

    public static boolean isValidBankCode(String bankCode) {
        return bankCode.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
    }
}
