package cz.cervenka.p2p_project.network;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    public static String sendCommand(String bankIp, int port, String command) {
        try (Socket socket = new Socket(bankIp, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send command
            writer.println(command);

            // Read response
            return reader.readLine();

        } catch (IOException e) {
            return "ER Unable to reach bank node at " + bankIp;
        }
    }
}