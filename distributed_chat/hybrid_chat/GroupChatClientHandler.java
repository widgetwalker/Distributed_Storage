import java.io.*;
import java.net.*;
import java.util.*;

public class GroupChatClientHandler extends Thread {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final List<GroupChatClientHandler> clients;
    private String clientName = "Unknown";
    private volatile boolean running = true;

    public GroupChatClientHandler(Socket socket, List<GroupChatClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("[HANDLER] Error setting up streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String message;

            while (running && (message = in.readLine()) != null) {
                if (message.contains("has joined the chat")) {
                    int start = message.indexOf('[');
                    int end = message.indexOf(" has joined");
                    if (start >= 0 && end > start) {
                        clientName = message.substring(start + 1, end);
                    }
                }

                System.out.println(message);
                OneToOneGroupChatServer.broadcastMessage(message, this);
            }

        } catch (SocketException e) {
            System.out.println("[HANDLER] Client " + clientName + " disconnected abruptly.");
        } catch (IOException e) {
            System.err.println("[HANDLER] Error reading from client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        if (out != null && !socket.isClosed()) {
            out.println(message);
        }
    }

    private void disconnect() {
        running = false;

        String leaveMessage = "[" + clientName + " has left the chat]";
        System.out.println(leaveMessage);
        OneToOneGroupChatServer.broadcastMessage(leaveMessage, this);

        OneToOneGroupChatServer.removeClient(this);

        closeQuietly(in);
        closeQuietly(out);
        closeQuietly(socket);
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
            }
        }
    }

    private void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public String getClientName() {
        return clientName;
    }
}
