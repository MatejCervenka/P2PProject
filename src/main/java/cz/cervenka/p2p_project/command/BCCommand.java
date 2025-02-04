package cz.cervenka.p2p_project.command;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BCCommand implements Command {
    @Override
    public String execute(String[] parameters) {
        try {
            return "BC " + InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "ER Unable to determine bank code.";
        }
    }
}
