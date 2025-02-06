package cz.cervenka.p2p_project.network;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NetworkClient {

    private static final int START_PORT = 65525;
    private static final int END_PORT = 65535;
    private static final int CONNECTION_TIMEOUT_MS = 20000000;

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
                    // Ignore failed attempts and continue checking other ports
                }
            }
        } finally {
            executor.shutdown();
        }

        return "ER Unable to connect to any port for bank " + bankIp;
    }

    private static String tryConnect(String bankIp, int port, String command) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(bankIp, port), CONNECTION_TIMEOUT_MS);
            return sendCommandToBank(socket, command);
        } catch (IOException e) {
            return "ER Unable to reach bank at " + bankIp + ":" + port;
        }
    }

    private static String sendCommandToBank(Socket socket, String command) {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println(command);
            return reader.readLine();
        } catch (IOException e) {
            return "ER Error while communicating with bank.";
        }
    }
}