import java.io.IOException;

public class TransactionClientLauncher {
    public static void main(String[] args) {
        String clientId = "CLIENT-" + System.currentTimeMillis() % 1000;
        String host = "localhost";
        int port = 7777;
        
        try {
            TransactionClient client = new TransactionClient(clientId, host, port);
            client.connect();
            client.startInteractiveSession();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }
}
