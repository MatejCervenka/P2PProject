package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;
import cz.cervenka.p2p_project.config.ApplicationConfig;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles client connections, processes commands, and manages timeouts.
 * Each client connection is processed in a separate thread.
 */
public class ClientHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final CommandProcessor commandProcessor;
    private static final int CLIENT_TIMEOUT = ApplicationConfig.getInt("client.readTimeout");
    private static final int COMMAND_TIMEOUT = ApplicationConfig.getInt("client.commandTimeout");

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    /**
     * Initializes the ClientHandler with the client socket and command processor.
     *
     * @param clientSocket The client socket to communicate with.
     * @param commandProcessor The processor to handle client commands.
     */
    public ClientHandler(Socket clientSocket, CommandProcessor commandProcessor) {
        this.clientSocket = clientSocket;
        this.commandProcessor = commandProcessor;
    }

    /**
     * Handles communication with the client.
     * Reads client messages, processes them, and sends back responses.
     */
    @Override
    public void run() {
        logger.info("Handling client: " + clientSocket.getInetAddress());

        try {
            clientSocket.setSoTimeout(CLIENT_TIMEOUT);

            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String clientMessage;

                while ((clientMessage = readWithTimeout(reader)) != null) {
                    if (clientMessage.isEmpty()) {
                        continue;
                    }

                    logger.info("Received: {}", clientMessage);

                    String response = processWithTimeout(clientMessage);
                    writer.println(response);

                    logger.info("Sent: {}", response);
                }
            }
        } catch (SocketTimeoutException e) {
            logger.warn("{}Client connection timed out: {}{}", YELLOW, e.getMessage(), RESET);
        } catch (IOException e) {
            logger.error("{}Error handling client: {}{}", RED, e.getMessage(), RESET);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("{}Error closing client socket: {}{}", RED, e.getMessage(), RESET);
            }
        }
    }

    /**
     * Reads a line from the client with a timeout.
     *
     * @param reader The BufferedReader to read the client's message.
     * @return The message from the client, or null if a timeout occurs.
     * @throws IOException If an I/O error occurs while reading.
     */
    private String readWithTimeout(BufferedReader reader) throws IOException {
        Future<String> future = executor.submit(reader::readLine);

        try {
            return future.get(CLIENT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.warn("{}Read operation timed out.{}", YELLOW, RESET);
            return null;
        } catch (Exception e) {
            throw new IOException("Error reading from client: " + e.getMessage());
        }
    }

    /**
     * Processes a client command with a timeout.
     *
     * @param command The command to process.
     * @return The response to the command.
     */
    private String processWithTimeout(String command) {
        Future<String> future = executor.submit(() -> commandProcessor.processCommand(command));

        try {
            return future.get(COMMAND_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return "ER Command processing timeout exceeded.";
        } catch (Exception e) {
            return "ER An error occurred while processing the command.";
        }
    }
}
