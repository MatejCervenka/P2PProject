package cz.cervenka.p2p_project.server;

import cz.cervenka.p2p_project.command.CommandProcessor;
import cz.cervenka.p2p_project.config.ApplicationConfig;
import cz.cervenka.p2p_project.services.AccountService;
import cz.cervenka.p2p_project.services.BankService;

import java.io.IOException;
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

    public void start() {
        System.out.println("P2P Banking Server is starting...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            AccountService accountService = new AccountService();
            BankService bankService = new BankService(accountService);
            System.out.println("IP: " + bankService.getBankCode());
            CommandProcessor commandProcessor = new CommandProcessor(bankService, accountService);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

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

    public static void main(String[] args) {
        new P2PServer().start();
    }
}
