package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.config.ConfigTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * The CommandProcessor class is responsible for processing commands received from clients.
 * It uses an ExecutorService to handle commands asynchronously and ensures that commands
 * do not exceed the configured execution timeout.
 */
public class CommandProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
    private final ExecutorService executor;
    private final CommandFactory commandFactory;

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    /**
     * Constructs a CommandProcessor with the specified CommandFactory.
     *
     * @param commandFactory The factory used to retrieve and execute commands.
     */
    public CommandProcessor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Processes a command input by executing it asynchronously.
     *
     * @param rawInput The raw command string received from the client.
     * @return The response string from the executed command or an error message in case of failure.
     */
    public String processCommand(String rawInput) {
        rawInput = rawInput.trim();

        if (rawInput.isEmpty()) {
            return "";
        }

        logger.info("Processing command: {}", rawInput);

        String finalRawInput = rawInput;
        Callable<String> task = () -> executeCommand(finalRawInput);
        Future<String> future = executor.submit(task);

        try {
            return future.get(ConfigTimeout.getCommandTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.warn("{}Command timeout exceeded ({}ms).{}", YELLOW, ConfigTimeout.getCommandTimeout(), RESET);
            return "ER Command timeout exceeded (" + ConfigTimeout.getCommandTimeout() + "ms).";
        } catch (Exception e) {
            logger.error("{}Error processing command: {}{}", RED, e.getMessage(), RESET);
            return "ER An error occurred: " + e.getMessage();
        }
    }

    /**
     * Executes the parsed command by retrieving it from the CommandFactory and running it.
     *
     * @param rawInput The command string received from the client.
     * @return The response from the command execution.
     * @throws IOException If an I/O error occurs during command execution.
     */
    private String executeCommand(String rawInput) throws IOException {
        CommandParser.ParsedCommand parsedCommand = CommandParser.parse(rawInput);

        if (parsedCommand == null) {
            logger.warn("{}Invalid command format.{}", YELLOW, RESET);
            return "ER Invalid command format.";
        }

        Command command = commandFactory.getCommand(parsedCommand.getCommand());

        if (command == null) {
            logger.warn("{}Unknown command: {}{}", YELLOW, parsedCommand.getCommand(), RESET);
            return "ER Unknown command.";
        }

        return command.execute(parsedCommand.getParameters());
    }
}
