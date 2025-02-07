package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

import java.util.HashMap;
import java.util.Map;

/**
 * The CommandFactory class is responsible for managing and providing instances of command objects
 * based on their respective command names.
 */
public class CommandFactory {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Constructs a CommandFactory and initializes available commands with their respective implementations.
     *
     * @param accountService The AccountService instance used for command execution.
     */
    public CommandFactory(AccountService accountService) {
        commands.put("BC", new BCCommand());
        commands.put("AC", new ACCommand(accountService));
        commands.put("AD", new ADCommand(accountService));
        commands.put("AW", new AWCommand(accountService));
        commands.put("AB", new ABCommand(accountService));
        commands.put("AR", new ARCommand(accountService));
        commands.put("BA", new BACommand(accountService));
        commands.put("BN", new BNCommand(accountService));
        commands.put("AS", new ASCommand(accountService));
    }

    /**
     * Retrieves a command instance based on the command name.
     *
     * @param commandName The name of the command to retrieve.
     * @return The Command instance associated with the given command name, or null if no command exists.
     */
    public Command getCommand(String commandName) {
        return commands.getOrDefault(commandName, null);
    }
}
