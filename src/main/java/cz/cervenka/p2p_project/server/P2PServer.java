package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServer {

    private final int port;

    public P2PServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("P2P Banking Server is starting...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // Initialize services
            BankService bankService = new BankService();
            AccountService accountService = new AccountService();
            CommandProcessor commandProcessor = new CommandProcessor(bankService, accountService);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept an incoming connection
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Pass the CommandProcessor to ClientHandler
                new Thread(new ClientHandler(clientSocket, commandProcessor)).start();
            }

        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
        }
    }
}