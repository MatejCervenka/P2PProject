package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandFactory(BankService bankService, AccountService accountService) {
        commands.put("BC", new BCCommand());
        commands.put("AC", new ACCommand(accountService, bankService));
        commands.put("AD", new ADCommand(accountService, bankService));
        commands.put("AW", new AWCommand(accountService, bankService));
        commands.put("AB", new ABCommand(accountService, bankService));
        commands.put("AR", new ARCommand(accountService, bankService));
        commands.put("BA", new BACommand(bankService));
        commands.put("BN", new BNCommand(bankService));
        commands.put("AS", new ASCommand(accountService, bankService));
    }

    public Command getCommand(String commandName) {
        return commands.getOrDefault(commandName, null);
    }
}
