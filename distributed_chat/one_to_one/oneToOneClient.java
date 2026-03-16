import java.io.*;
import java.net.*;

public class oneToOneClient {
    private static final String SERVER_ADDRESS = "localhost"; // or server IP
    private static final int SERVER_PORT = 12350;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Connected to server. Start chatting!");

            // Thread to listen for server messages
            new Thread(() -> {
                try {
                    String incoming;
                    while ((incoming = serverReader.readLine()) != null) {
                        System.out.println(incoming);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            // Sending messages to server
            String message;
            while ((message = consoleReader.readLine()) != null) {
                serverWriter.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}