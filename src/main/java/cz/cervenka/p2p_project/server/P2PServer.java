package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;
import cz.cervenka.p2p_project.command.CommandFactory;
import cz.cervenka.p2p_project.config.ApplicationConfig;
import cz.cervenka.p2p_project.config.DatabaseConfig;
import cz.cervenka.p2p_project.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * A server for the P2P Banking system that listens for incoming client connections.
 * It uses a thread pool to handle each client connection in a separate thread.
 */
public class P2PServer {

    private static final Logger logger = LoggerFactory.getLogger(P2PServer.class);
    private static final int PORT = ApplicationConfig.getInt("server.port");
    private static final int THREAD_POOL_SIZE = ApplicationConfig.getInt("thread_pool.size");
    private static final int MAX_QUEUE_SIZE = ApplicationConfig.getInt("thread_pool.maxQueueSize");

    private final ExecutorService threadPool;

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    /**
     * Initializes the P2PServer, setting up the thread pool and shutdown hook.
     */
    public P2PServer() {
        this.threadPool = new ThreadPoolExecutor(
                THREAD_POOL_SIZE, THREAD_POOL_SIZE,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE),
                new ThreadPoolExecutor.AbortPolicy()
        );

        // Register a shutdown hook to close the database when the server stops
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * Starts the P2P Banking server, listening for client connections.
     * Each client connection is handled in a separate thread.
     */
    public void start() throws IOException {
        logger.info("P2P Banking Server is starting...");
        InetAddress localAddress = getLocalIpAddress();

        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, localAddress)) {
            logger.info("Server is listening on port {}", PORT);

            AccountService accountService = new AccountService();
            logger.info("IP: {}", getBankCode());

            CommandFactory commandFactory = new CommandFactory(accountService);
            CommandProcessor commandProcessor = new CommandProcessor(commandFactory);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("New client connected: {}", clientSocket.getInetAddress());

                    threadPool.execute(new ClientHandler(clientSocket, commandProcessor));
                } catch (IOException e) {
                    logger.error("{}Error accepting client connection: {}{}", RED, e.getMessage(), RESET);
                }
            }
        } catch (IOException e) {
            logger.error("{}Error in server: {}{}", RED, e.getMessage(), RESET);
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down the server and releases resources.
     */
    private void shutdown() {
        logger.info("Shutting down server...");

        // Shutdown thread pool
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }

        // Close database connection
        DatabaseConfig.closeDataSource();
        logger.info("Server shut down.");
    }

    /**
     * Retrieves the local IP address for the server.
     *
     * @return The local IP address.
     * @throws IOException If an error occurs while retrieving the IP address.
     */
    private static InetAddress getLocalIpAddress() throws IOException {
        InetAddress localAddress = null;

        for (InetAddress address : InetAddress.getAllByName(InetAddress.getLocalHost().getHostName())) {
            if (address.isSiteLocalAddress()) {
                localAddress = address;
                ApplicationConfig.setIP(localAddress.getHostAddress());
                break;
            }
        }
        return localAddress;
    }

    /**
     * Retrieves the bank code based on the local IP address.
     *
     * @return The bank's code (IP address).
     * @throws IOException If an error occurs while retrieving the IP address.
     */
    public static String getBankCode() throws IOException {
        return String.valueOf(getLocalIpAddress().getHostAddress());
    }

    /**
     * Validates if the given bank code matches the local bank code.
     *
     * @param bankCode The bank code to validate.
     * @return true if the bank code is valid, false otherwise.
     * @throws IOException If an error occurs while retrieving the bank code.
     */
    public static boolean isValidBankCode(String bankCode) throws IOException {
        return getBankCode().equals(bankCode);
    }
}
