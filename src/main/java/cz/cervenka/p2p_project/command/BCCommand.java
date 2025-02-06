package cz.cervenka.p2p_project.command;

import cz.cervenka.p2p_project.server.P2PServer;

import java.io.IOException;

public class BCCommand implements Command {
    @Override
    public String execute(String[] parameters) throws IOException {
        if (parameters.length > 0) {
            return "ER Invalid format.";
        }
        return "BC " + P2PServer.getBankCode();
    }
}
