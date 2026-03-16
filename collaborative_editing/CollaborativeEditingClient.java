import java.io.*;
import java.net.*;
import java.util.*;

public class CollaborativeEditingClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5001;
    private static volatile boolean running = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║    COLLABORATIVE FILE EDITING CLIENT       ║");
        System.out.println("╚════════════════════════════════════════════╝");

        System.out.print("Enter server address (default: " + DEFAULT_HOST + "): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) {
            host = DEFAULT_HOST;
        }

        System.out.print("Enter port number (default: " + DEFAULT_PORT + "): ");
        String portInput = scanner.nextLine().trim();
        int port = DEFAULT_PORT;
        if (!portInput.isEmpty()) {
            try {
                port = Integer.parseInt(portInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default: " + DEFAULT_PORT);
            }
        }

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = "User" + new Random().nextInt(1000);
            System.out.println("No username provided. Using: " + username);
        }

        connectToServer(host, port, username);
    }

    private static void connectToServer(String host, int port, String username) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedReader consoleReader = null;

        try {
            System.out.println("\n[CLIENT] Connecting to " + host + ":" + port + "...");

            socket = new Socket(host, port);
            System.out.println("[CLIENT] Connected successfully!");
            System.out.println("\nAvailable commands:");
            System.out.println("  READ                    - View current document");
            System.out.println("  APPEND <text>           - Add new line");
            System.out.println("  REPLACE <lineNo> <text> - Replace a line");
            System.out.println("  DELETE <lineNo>         - Delete a line");
            System.out.println("  EXIT                    - Quit\n");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            consoleReader = new BufferedReader(new InputStreamReader(System.in));

            out.println("USERNAME:" + username);

            final BufferedReader finalIn = in;
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while (running && (message = finalIn.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("\n[CLIENT] Disconnected from server.");
                    }
                }
                running = false;
            });
            receiveThread.setDaemon(true);
            receiveThread.start();

            final PrintWriter finalOut = out;
            String command;
            while (running) {
                try {
                    System.out.print("> ");
                    command = consoleReader.readLine();

                    if (command == null || command.trim().equalsIgnoreCase("EXIT")) {
                        running = false;
                        break;
                    }

                    if (!command.trim().isEmpty()) {
                        finalOut.println(command.trim());
                    }

                } catch (IOException e) {
                    System.out.println("[CLIENT] Error reading input.");
                    break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("[ERROR] Unknown host: " + host);
        } catch (ConnectException e) {
            System.err.println("[ERROR] Could not connect to server. Is it running?");
        } catch (IOException e) {
            System.err.println("[ERROR] Connection error: " + e.getMessage());
        } finally {
            running = false;
            closeQuietly(consoleReader);
            closeQuietly(in);
            closeQuietly(out);
            closeQuietly(socket);
            System.out.println("[CLIENT] Disconnected. Goodbye!");
        }
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
}
