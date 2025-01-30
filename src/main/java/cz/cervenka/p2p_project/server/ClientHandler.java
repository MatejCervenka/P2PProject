package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;
import cz.cervenka.p2p_project.config.ConfigTimeout;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final CommandProcessor commandProcessor;
    private final ExecutorService executor;

    public ClientHandler(Socket clientSocket, CommandProcessor commandProcessor) {
        this.clientSocket = clientSocket;
        this.commandProcessor = commandProcessor;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        System.out.println("Handling client: " + clientSocket.getInetAddress());

        try {
            clientSocket.setSoTimeout(30000);

            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)
            ) {
                String clientMessage;

                while ((clientMessage = readWithTimeout(reader)) != null) {
                    System.out.println("Received: " + clientMessage);

                    String response = processWithTimeout(clientMessage);

                    writer.println(response);
                    System.out.println("Sent: " + response);
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Client connection timed out: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
            executor.shutdown();
        }
    }

    /**
     * Reads input with a timeout to prevent a client from keeping the connection open indefinitely.
     */
    private String readWithTimeout(BufferedReader reader) throws IOException {
        Future<String> future = executor.submit(reader::readLine);

        try {
            return future.get(ConfigTimeout.getUserTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.err.println("Read operation timed out.");
            return null;
        } catch (Exception e) {
            throw new IOException("Error reading from client: " + e.getMessage());
        }
    }

    /**
     * Processes a command with a timeout to prevent long-running operations.
     */
    private String processWithTimeout(String command) {
        Future<String> future = executor.submit(() -> commandProcessor.processCommand(command));

        try {
            return future.get(ConfigTimeout.getCommandTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return "ER Command processing timeout exceeded.";
        } catch (Exception e) {
            return "ER An error occurred while processing command.";
        }
    }
}