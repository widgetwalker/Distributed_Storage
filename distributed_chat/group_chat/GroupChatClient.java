import java.io.*;
import java.net.*;

public class GroupChatClient {
    private static final int SERVER_PORT = 12351;

    public static void main(String[] args) {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            // Get server address from user
            System.out.print("Enter server IP address (or 'localhost' for local): ");
            String serverAddress = consoleReader.readLine();
            
            if (serverAddress == null || serverAddress.trim().isEmpty()) {
                serverAddress = "localhost";
            }

            // Connect to server
            Socket socket = new Socket(serverAddress, SERVER_PORT);
            System.out.println("Connected to server at " + serverAddress + ":" + SERVER_PORT);

            // Setup I/O streams
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);

            // Get username
            System.out.print("Enter your username: ");
            String username = consoleReader.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }
            
            // Send username to server
            serverWriter.println(username);
            System.out.println("\n=== Group Chat Started ===");
            System.out.println("Type your messages below:\n");

            // Thread to listen for messages from server
            new Thread(() -> {
                try {
                    String incoming;
                    while ((incoming = serverReader.readLine()) != null) {
                        System.out.println(incoming);
                    }
                } catch (IOException e) {
                    System.out.println("\n*** Disconnected from server ***");
                }
            }).start();

            // Main thread: send messages to server
            String message;
            while ((message = consoleReader.readLine()) != null) {
                serverWriter.println(message);
            }

            // Cleanup
            socket.close();
            serverReader.close();
            serverWriter.close();

        } catch (UnknownHostException e) {
            System.err.println("Error: Could not find server at the specified address.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Could not connect to server.");
            e.printStackTrace();
        } finally {
            try {
                consoleReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
