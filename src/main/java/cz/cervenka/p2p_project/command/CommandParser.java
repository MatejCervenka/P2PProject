package cz.cervenka.p2p_project.command;

/**
 * The CommandParser class is responsible for parsing command input strings into structured commands
 * with their corresponding parameters.
 */
public class CommandParser {

    /**
     * Encapsulates a parsed command with its name and parameters.
     */
    public static class ParsedCommand {
        private final String command;
        private final String[] parameters;

        /**
         * Constructs a ParsedCommand instance with the given command name and parameters.
         *
         * @param command The name of the command.
         * @param parameters The array of command parameters.
         */
        public ParsedCommand(String command, String[] parameters) {
            this.command = command;
            this.parameters = parameters;
        }

        /**
         * Retrieves the command name.
         *
         * @return The command name.
         */
        public String getCommand() {
            return command;
        }

        /**
         * Retrieves the command parameters.
         *
         * @return An array of command parameters.
         */
        public String[] getParameters() {
            return parameters;
        }
    }

    /**
     * Parses a raw input string into a ParsedCommand instance.
     *
     * @param rawInput The raw command string to be parsed.
     * @return A ParsedCommand instance containing the parsed command name and parameters, or null if the input is empty.
     */
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
