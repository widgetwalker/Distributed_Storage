import java.io.IOException;

public class TransactionServerLauncher {
    public static void main(String[] args) {
        int port = 7777;
        try {
            // Setup the coordinator and participants
            TransactionCoordinator coordinator = new TransactionCoordinator("COORD-ALPHA");
            coordinator.addParticipant(new Participant("PARTITION-A"));
            coordinator.addParticipant(new Participant("PARTITION-B"));
            coordinator.addParticipant(new Participant("PARTITION-C"));
            
            // Start the server
            TransactionServer server = new TransactionServer(port, coordinator);
            server.start();
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
