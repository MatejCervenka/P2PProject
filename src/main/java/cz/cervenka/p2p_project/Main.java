package cz.cervenka.p2p_project;

import cz.cervenka.p2p_project.config.DatabaseConfig;
import cz.cervenka.p2p_project.server.P2PServer;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            DatabaseConfig.closeDataSource();
        }));

        P2PServer server = new P2PServer();
        server.start();
    }
}