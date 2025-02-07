package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;

import java.io.IOException;

/**
 * Command for retrieving the bank code.
 * Format: BC (no parameters expected).
 */
public class BCCommand implements Command {

    /**
     * Executes the "BC" (Bank Code) command.
     *
     * @param parameters The command arguments (expected to be empty).
     * @return The bank code or an error message if the format is incorrect.
     * @throws IOException If an issue occurs while retrieving the bank code.
     */
    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length > 0) {
            return "ER Invalid format. Expected: BC (no parameters).";
        }
        return "BC " + P2PServer.getBankCode();
    }
}
