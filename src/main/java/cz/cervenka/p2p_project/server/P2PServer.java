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

    public static String getBankCode() throws IOException {
        return String.valueOf(getLocalIpAddress().getHostAddress());
    }

    public static boolean isValidBankCode(String bankCode) throws IOException {
        return getBankCode().equals(bankCode);
    }
}
