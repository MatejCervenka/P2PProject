package cz.cervenka.p2p_project.command;

import java.io.IOException;

public interface Command {
    String execute(String[] parameters) throws IOException;
}
