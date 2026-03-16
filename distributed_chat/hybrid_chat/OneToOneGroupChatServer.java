import java.io.*;
import java.net.*;
import java.util.*;

public class OneToOneGroupChatServer {

    private static final int DEFAULT_PORT = 5000;
    private static final List<GroupChatClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║       DISTRIBUTED CHAT SERVER              ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.println("║  Select Mode:                              ║");
        System.out.println("║    1. One-to-One Chat (Personal)           ║");
        System.out.println("║    2. Group Chat (Multi-client)            ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.print("Enter choice (1 or 2): ");

        int mode = 1;
        try {
            mode = Integer.parseInt(scanner.nextLine().trim());
            if (mode != 1 && mode != 2) {
                System.out.println("Invalid choice. Defaulting to Mode 1 (One-to-One).");
                mode = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Defaulting to Mode 1 (One-to-One).");
        }

        System.out.print("Enter port number (default " + DEFAULT_PORT + "): ");
        String portInput = scanner.nextLine().trim();
        int port = DEFAULT_PORT;
        if (!portInput.isEmpty()) {
            try {
                port = Integer.parseInt(portInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default: " + DEFAULT_PORT);
            }
        }

        if (mode == 1) {
            startOneToOneChat(port);
        } else {
            startGroupChat(port);
        }
    }

    private static void startOneToOneChat(int port) {
        System.out.println("\n[SERVER] Starting One-to-One Chat Server on port " + port + "...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedReader consoleReader = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[SERVER] Waiting for a client to connect...");

            clientSocket = serverSocket.accept();
            System.out.println("[SERVER] Client connected from: " +
                    clientSocket.getInetAddress().getHostAddress());
            System.out.println("[SERVER] Type your messages below. Type 'exit' to quit.\n");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            consoleReader = new BufferedReader(new InputStreamReader(System.in));

            final BufferedReader finalIn = in;
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = finalIn.readLine()) != null) {
                        System.out.println("[CLIENT]: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("[SERVER] Client disconnected.");
                }
            });
            receiveThread.setDaemon(true);
            receiveThread.start();

            String message;
            while (true) {
                message = consoleReader.readLine();
                if (message == null || message.equalsIgnoreCase("exit")) {
                    out.println("[Server is closing the chat]");
                    break;
                }
                if (!message.trim().isEmpty()) {
                    out.println(message);
                }
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Server error: " + e.getMessage());
        } finally {
            closeQuietly(consoleReader);
            closeQuietly(in);
            closeQuietly(out);
            closeQuietly(clientSocket);
            closeQuietly(serverSocket);
            System.out.println("[SERVER] Server shut down.");
        }
    }

    private static void startGroupChat(int port) {
        System.out.println("\n[SERVER] Starting Group Chat Server on port " + port + "...");
        System.out.println("[SERVER] Waiting for clients to connect...\n");

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[SERVER] New connection from: " +
                            clientSocket.getInetAddress().getHostAddress());

                    GroupChatClientHandler handler = new GroupChatClientHandler(clientSocket, clients);
                    clients.add(handler);
                    handler.start();

                } catch (IOException e) {
                    System.err.println("[ERROR] Error accepting client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Could not start server: " + e.getMessage());
        } finally {
            closeQuietly(serverSocket);
        }
    }

    public static void broadcastMessage(String message, GroupChatClientHandler sender) {
        synchronized (clients) {
            for (GroupChatClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(GroupChatClientHandler client) {
        clients.remove(client);
        System.out.println("[SERVER] Client removed. Active clients: " + clients.size());
    }

    private static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
            }
        }
    }

    private static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private static void closeQuietly(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
