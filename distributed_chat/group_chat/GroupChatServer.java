import java.io.*;
import java.net.*;
import java.util.*;

public class GroupChatServer {
    private static final int PORT = 12351;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static int clientCounter = 0;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCounter++;
                System.out.println("Client-" + clientCounter + " connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientCounter);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients
    static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
        // Display on server console
        System.out.println(message);
    }

    // Remove disconnected client
    static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client-" + client.clientId + " disconnected. Active clients: " + clients.size());
    }

    // Inner class to handle each client connection
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int clientId;
        private String username;

        public ClientHandler(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Get username from client
                username = in.readLine();
                if (username == null || username.trim().isEmpty()) {
                    username = "Client-" + clientId;
                }

                String joinMessage = "*** " + username + " has joined the chat ***";
                broadcast(joinMessage, this);

                // Listen for messages from this client
                String message;
                while ((message = in.readLine()) != null) {
                    String formattedMessage = username + ": " + message;
                    broadcast(formattedMessage, this);
                }

            } catch (IOException e) {
                System.out.println("Error with Client-" + clientId + ": " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        void cleanup() {
            try {
                String leaveMessage = "*** " + username + " has left the chat ***";
                broadcast(leaveMessage, this);
                
                removeClient(this);
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
