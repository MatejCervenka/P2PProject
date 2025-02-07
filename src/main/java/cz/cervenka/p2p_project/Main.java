package cz.cervenka.p2p_project;

import cz.cervenka.p2p_project.server.P2PServer;

import java.io.IOException;

/**
 * The entry point for the P2P banking system application.
 * This class initializes and starts the P2P server.
 */
public class Main {

    /**
     * The main method that starts the P2P server.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an error occurs while starting the server.
     */
    public static void main(String[] args) throws IOException {
        P2PServer server = new P2PServer();
        server.start();
    }
}
