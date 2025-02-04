package cz.cervenka.p2p_project.command;

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

        String[] parts = rawInput.split(" ", 2);
        String command = parts[0].toUpperCase();
        String[] parameters = (parts.length > 1) ? parts[1].split(" ") : new String[0];

        return new ParsedCommand(command, parameters);
    }
}
