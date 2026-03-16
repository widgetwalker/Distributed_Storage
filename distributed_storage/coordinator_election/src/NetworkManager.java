import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class NetworkManager {
    private final String myId;
    private final int myPort;
    private Consumer<String> onMessageReceived;
    private boolean running = true;

    public NetworkManager(String myId, Consumer<String> onMessageReceived) {
        this.myId = myId;
        this.myPort = ConfigLoader.getPort(myId);
        this.onMessageReceived = onMessageReceived;
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(myPort)) {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        if (running) System.err.println("Accept error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Server start failed: " + e.getMessage());
            }
        }).start();
    }

    private void handleClient(Socket socket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    onMessageReceived.accept(line);
                }
            } catch (IOException e) { }
        }).start();
    }

    public void broadcast(String message) {
        String[] nodes = { "A", "B", "C", "D" };
        for (String node : nodes) {
            if (!node.equals(myId)) {
                sendToNode(node, message);
            }
        }
    }

    public void sendToNode(String targetId, String message) {
        String ip = ConfigLoader.getIp(targetId);
        int port = ConfigLoader.getPort(targetId);
        if (ip == null || port == -1) return;

        new Thread(() -> {
            try (Socket socket = new Socket(ip, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
            } catch (IOException e) { }
        }).start();
    }

    public void stop() { running = false; }
}
