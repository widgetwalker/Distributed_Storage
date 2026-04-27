import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Server that accepts multiple client connections and processes distributed transactions.
 * Each client connection is handled in a separate thread.
 */
public class TransactionServer {
    private final int port;
    private final TransactionCoordinator coordinator;
    private ServerSocket serverSocket;
    private final ExecutorService clientExecutor;
    private volatile boolean running = false;

    public TransactionServer(int port, TransactionCoordinator coordinator) {
        this.port = port;
        this.coordinator = coordinator;
        this.clientExecutor = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("TransactionServer started on port " + port);
        System.out.println("Participants: " + coordinator.getAllParticipants().keySet());
        System.out.println("Waiting for client connections...\n");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                clientExecutor.submit(new ClientHandler(clientSocket, coordinator));
            } catch (SocketException e) {
                if (running) {
                    System.err.println("Server socket error: " + e.getMessage());
                }
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        clientExecutor.shutdown();
        coordinator.shutdown();
        System.out.println("Server stopped.");
    }

    /**
     * Handles a single client connection.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final TransactionCoordinator coordinator;

        public ClientHandler(Socket socket, TransactionCoordinator coordinator) {
            this.socket = socket;
            this.coordinator = coordinator;
        }

        @Override
        public void run() {
            try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
            ) {
                // Send welcome message
                out.writeObject("CONNECTED: Welcome to Distributed Transaction Server");
                out.flush();

                while (!socket.isClosed()) {
                    try {
                        Object received = in.readObject();
                        if (received instanceof Transaction) {
                            Transaction tx = (Transaction) received;
                            System.out.println("Received transaction from " + tx.getClientId() + ": " + tx.getTxId());
                            
                            Transaction result = coordinator.executeTransaction(tx);
                            out.writeObject(result);
                            out.flush();
                        } else if (received instanceof String && ((String) received).equalsIgnoreCase("EXIT")) {
                            System.out.println("Client disconnected: " + socket.getInetAddress());
                            break;
                        } else {
                            out.writeObject("ERROR: Expected Transaction object or EXIT");
                            out.flush();
                        }
                    } catch (EOFException e) {
                        System.out.println("Client closed connection: " + socket.getInetAddress());
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}

