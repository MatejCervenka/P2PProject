package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.config.ConfigTimeout;

import java.io.IOException;
import java.util.concurrent.*;

public class CommandProcessor {
    private final ExecutorService executor;
    private final CommandFactory commandFactory;

    public CommandProcessor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
        this.executor = Executors.newCachedThreadPool();
    }

    public String processCommand(String rawInput) {
        rawInput = rawInput.trim();

        if (rawInput.isEmpty()) {
            return "";
        }

        String finalRawInput = rawInput;
        Callable<String> task = () -> executeCommand(finalRawInput);
        Future<String> future = executor.submit(task);

        try {
            return future.get(ConfigTimeout.getCommandTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return "ER Command timeout exceeded (" + ConfigTimeout.getCommandTimeout() + "ms).";
        } catch (Exception e) {
            return "ER An error occurred: " + e.getMessage();
        }
    }

    private String executeCommand(String rawInput) throws IOException {
        CommandParser.ParsedCommand parsedCommand = CommandParser.parse(rawInput);

        if (parsedCommand == null) {
            return "ER Invalid command format.";
        }

        Command command = commandFactory.getCommand(parsedCommand.getCommand());

        if (command == null) {
            return "ER Unknown command.";
        }

        return command.execute(parsedCommand.getParameters());
    }
}
