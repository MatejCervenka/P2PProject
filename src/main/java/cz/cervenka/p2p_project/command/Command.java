package cz.cervenka.p2p_project.command;

import java.io.IOException;

/**
 * Represents a command that can be executed within the P2P banking system.
 * All commands must implement this interface and provide an execution logic.
 */
public interface Command {

    /**
     * Executes the command with the given parameters.
     *
     * @param parameters The arguments for the command. The expected format depends on the specific command implementation.
     * @return The result of the command execution, usually in the form of a response message.
     * @throws IOException If an I/O error occurs during execution.
     */
    String execute(String[] parameters) throws IOException;
}
