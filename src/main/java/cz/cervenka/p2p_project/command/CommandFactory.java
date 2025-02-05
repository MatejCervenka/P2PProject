package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final Map<String, Command> commands = new HashMap<>();

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

    public Command getCommand(String commandName) {
        return commands.getOrDefault(commandName, null);
    }
}