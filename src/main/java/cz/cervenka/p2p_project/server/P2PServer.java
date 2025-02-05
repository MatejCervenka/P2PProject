package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;
import cz.cervenka.p2p_project.command.CommandFactory;
import cz.cervenka.p2p_project.config.ApplicationConfig;
import cz.cervenka.p2p_project.services.AccountService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class P2PServer {

    private static final int PORT = ApplicationConfig.getInt("server.port");
    private static final int THREAD_POOL_SIZE = ApplicationConfig.getInt("threadpool.size");
    private static final int MAX_QUEUE_SIZE = ApplicationConfig.getInt("threadpool.maxQueueSize");

    private final ExecutorService threadPool;

    public P2PServer() {
        this.threadPool = new ThreadPoolExecutor(
                THREAD_POOL_SIZE, THREAD_POOL_SIZE,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public void start() throws IOException {
        System.out.println("P2P Banking Server is starting...");
        InetAddress localAddress = getLocalIpAddress();

        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, localAddress)) {
            System.out.println("Server is listening on port " + PORT);

            // Initialize services
            AccountService accountService = new AccountService();
            System.out.println("IP: " + getBankCode());

            // CommandFactory should be passed to CommandProcessor
            CommandFactory commandFactory = new CommandFactory(accountService);
            CommandProcessor commandProcessor = new CommandProcessor(commandFactory);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    // Execute ClientHandler tasks with thread pool
                    threadPool.execute(new ClientHandler(clientSocket, commandProcessor));
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        System.out.println("Shutting down thread pool...");
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        System.out.println("Server shut down.");
    }

    /**
     * Gets the local LAN IP address of the machine.
     *
     * @return The LAN IP address or null if it cannot be determined.
     * @throws IOException If there's an error retrieving the LAN IP address.
     */
    private static InetAddress getLocalIpAddress() throws IOException {
        InetAddress localAddress = null;

        // Attempt to get the LAN IP address (skipping localhost)
        for (InetAddress address : InetAddress.getAllByName(InetAddress.getLocalHost().getHostName())) {
            if (address.isSiteLocalAddress()) {
                localAddress = address;
                ApplicationConfig.setIP(String.valueOf(localAddress.getHostAddress()));
                break;
            }
        }
        return localAddress;
    }

    /**
     * Retrieves the current bank's code (IP address).
     *
     * @return the bank code.
     */
    public static String getBankCode() throws IOException {
        return String.valueOf(getLocalIpAddress().getHostAddress());
    }

    /**
     * Checks if the given bank code matches this bank.
     *
     * @param bankCode the bank's code (IP address).
     * @return true if it matches, false otherwise.
     */
    public static boolean isValidBankCode(String bankCode) throws IOException {
        return getBankCode().equals(bankCode);
    }
}
