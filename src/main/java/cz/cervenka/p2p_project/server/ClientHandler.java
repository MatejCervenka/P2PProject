package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final CommandProcessor commandProcessor;

    public ClientHandler(Socket clientSocket, CommandProcessor commandProcessor) {
        this.clientSocket = clientSocket;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        System.out.println("Handling client: " + clientSocket.getInetAddress());

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage;

            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received: " + clientMessage);

                // Process the command
                String response = commandProcessor.processCommand(clientMessage);

                // Send the response
                writer.println(response);
                System.out.println("Sent: " + response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}