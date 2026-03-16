import java.io.*;
import java.net.*;
import java.util.*;

public class CollaborativeEditingServer {

    private static final int DEFAULT_PORT = 5001;
    private static final List<CollaborativeEditingHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final List<String> document = Collections.synchronizedList(new ArrayList<>());
    private static final String LOG_FILE = "edit_log.txt";
    private static CollaborativeEditingHandler currentWriter = null;

    public static synchronized boolean requestWriteAccess(CollaborativeEditingHandler client) {
        if (currentWriter == null || currentWriter == client) {
            currentWriter = client;
            return true;
        }
        return false;
    }

    public static synchronized void releaseWriteAccess(CollaborativeEditingHandler client) {
        if (currentWriter == client) {
            currentWriter = null;
        }
    }

    public static synchronized String getWriterName() {
        return (currentWriter != null) ? currentWriter.getClientName() : "None";
    }
    private static String currentFilePath = "shared_document.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║    COLLABORATIVE FILE EDITING SERVER       ║");
        System.out.println("╚════════════════════════════════════════════╝");

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

        System.out.print("\nEnter path to .txt file to share (or press Enter for default): ");
        String filePath = scanner.nextLine().trim();
        if (!filePath.isEmpty()) {
            currentFilePath = filePath;
        }

        initializeDocument();
        startServer(port);
    }

    private static void initializeDocument() {
        File docFile = new File(currentFilePath);

        if (docFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(docFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    document.add(line);
                }
                System.out
                        .println("[SERVER] Loaded document from " + currentFilePath + " (" + document.size()
                                + " lines).");
            } catch (IOException e) {
                System.err.println("[ERROR] Could not read " + currentFilePath + ": " + e.getMessage());
                loadDefaultDocument();
            }
        } else {
            System.out.println("[SERVER] File not found. Creating new document: " + currentFilePath);
            loadDefaultDocument();
            saveDocumentToFile();
        }
    }

    private static void loadDefaultDocument() {
        document.add("Welcome to Collaborative Document Editing!");
        document.add("This is line 2.");
        document.add("You can edit this document together.");
        System.out.println("[SERVER] Document initialized with default content (" + document.size() + " lines).");
    }

    private static void saveDocumentToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFilePath))) {
            synchronized (document) {
                for (String line : document) {
                    writer.println(line);
                }
            }
            System.out.println("[SERVER] Document auto-saved to " + currentFilePath);
        } catch (IOException e) {
            System.err.println("[ERROR] Could not save to " + currentFilePath + ": " + e.getMessage());
        }
    }

    private static void startServer(int port) {
        System.out.println("\n[SERVER] Starting File Server on port " + port + "...");
        System.out.println("[SERVER] Waiting for clients to connect...\n");

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[SERVER] New connection from: " +
                            clientSocket.getInetAddress().getHostAddress());

                    CollaborativeEditingHandler handler = new CollaborativeEditingHandler(clientSocket, clients,
                            document);
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

    public static void broadcastUpdate(String message, CollaborativeEditingHandler sender) {
        synchronized (clients) {
            for (CollaborativeEditingHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public static void logEdit(String username, String action) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] " + username + ": " + action;
        System.out.println(logEntry);

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("[ERROR] Could not write to log file: " + e.getMessage());
        }
    }

    public static void removeClient(CollaborativeEditingHandler client) {
        clients.remove(client);
        System.out.println("[SERVER] Client removed. Active clients: " + clients.size());
    }

    public static void saveDocument() {
        saveDocumentToFile();
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
