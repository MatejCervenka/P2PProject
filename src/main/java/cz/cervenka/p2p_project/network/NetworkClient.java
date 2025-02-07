package cz.cervenka.p2p_project.network;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods to send commands to a remote bank over the network.
 * Tries connecting to multiple ports on the bank's IP address until a connection is established.
 */
public class NetworkClient {

    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);
    private static final int START_PORT = 65525;
    private static final int END_PORT = 65535;
    private static final int CONNECTION_TIMEOUT_MS = 20000;

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    /**
     * Sends a command to a bank at the specified IP address.
     * Attempts to connect to multiple ports until a connection is successful.
     *
     * @param bankIp The IP address of the bank.
     * @param command The command to send to the bank.
     * @return The response from the bank, or an error message if the connection fails.
     */
    public static String sendCommand(String bankIp, String command) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> results = new ArrayList<>();

        for (int port = START_PORT; port <= END_PORT; port++) {
            int finalPort = port;
            results.add(executor.submit(() -> tryConnect(bankIp, finalPort, command)));
        }

        try {
            for (Future<String> result : results) {
                try {
                    String response = result.get();
                    if (!response.startsWith("ER")) {
                        executor.shutdownNow();
                        return response;
                    }
                } catch (Exception e) {
                    logger.warn("{}Failed to connect on port: {}{}", YELLOW, e.getMessage(), RESET);
                }
            }
        } finally {
            executor.shutdown();
        }

        return "ER Unable to connect to any port for bank " + bankIp;
    }

    /**
     * Tries to connect to a bank's server on the specified port and send the command.
     *
     * @param bankIp The IP address of the bank.
     * @param port The port to connect to.
     * @param command The command to send to the bank.
     * @return The response from the bank.
     */
    private static String tryConnect(String bankIp, int port, String command) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(bankIp, port), CONNECTION_TIMEOUT_MS);
            return sendCommandToBank(socket, command);
        } catch (IOException e) {
            logger.error("{}Error connecting to bank at {}:{} - {}{}", RED, bankIp, port, e.getMessage(), RESET);
            return "ER Unable to reach bank at " + bankIp + ":" + port;
        }
    }

    /**
     * Sends the command to the bank through the given socket.
     *
     * @param socket The socket to communicate with the bank.
     * @param command The command to send.
     * @return The response from the bank.
     */
    private static String sendCommandToBank(Socket socket, String command) {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println(command);
            return reader.readLine();
        } catch (IOException e) {
            logger.error("{}Error while communicating with bank: {}{}", RED, e.getMessage(), RESET);
            return "ER Error while communicating with bank.";
        }
    }
}
