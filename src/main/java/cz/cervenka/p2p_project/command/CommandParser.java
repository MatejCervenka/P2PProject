package cz.cervenka.p2p_project.command;


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
        String[] parameters;

        if (command.equals("AD") || command.equals("AW") || command.equals("AB") || command.equals("AR")) { // Handle AD, AW, AB and AR specially
            if (parts.length > 1) {
                parameters = new String[parts.length - 1];
                System.arraycopy(parts, 1, parameters, 0, parts.length - 1); // Keep the whole account/bank code string
            } else {
                parameters = new String[0];
            }
        } else {
            parameters = parts.length > 1 ? parts[1].split("/") : new String[0]; // Normal split for other commands
        }

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
