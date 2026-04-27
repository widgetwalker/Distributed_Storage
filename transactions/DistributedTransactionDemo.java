import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point for the distributed transaction system.
 * 
 * Usage:
 *   java DistributedTransactionDemo server          - Starts the transaction server
 *   java DistributedTransactionDemo client            - Starts interactive client
 *   java DistributedTransactionDemo auto            - Runs automated multi-client demo
 */
public class DistributedTransactionDemo {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  java DistributedTransactionDemo server     - Start server");
            System.out.println("  java DistributedTransactionDemo client      - Start interactive client");
            System.out.println("  java DistributedTransactionDemo auto        - Run automated multi-client demo");
            System.exit(0);
        }

        String mode = args[0].toLowerCase();

        switch (mode) {
            case "server":
                startServer();
                break;
            case "client":
                startInteractiveClient();
                break;
            case "auto":
                runAutomatedDemo();
                break;
            default:
                System.err.println("Unknown mode: " + mode);
                System.exit(1);
        }
    }

    /**
     * Start the transaction server with 3 participants (distributed nodes).
     */
    private static void startServer() throws IOException {
        TransactionCoordinator coordinator = new TransactionCoordinator("COORD-ALPHA");

        // Add 3 participant nodes (simulating distributed data partitions)
        coordinator.addParticipant(new Participant("PARTITION-A"));
        coordinator.addParticipant(new Participant("PARTITION-B"));
        coordinator.addParticipant(new Participant("PARTITION-C"));

        TransactionServer server = new TransactionServer(SERVER_PORT, coordinator);
        
        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown signal received. Stopping server...");
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        server.start();
    }

    /**
     * Start an interactive client session with user input.
     */
    private static void startInteractiveClient() throws IOException, ClassNotFoundException {
        String clientId = "CLIENT-" + System.currentTimeMillis();
        TransactionClient client = new TransactionClient(clientId, SERVER_HOST, SERVER_PORT);
        client.connect();
        client.startInteractiveSession();
    }

    /**
     * Run an automated demo with multiple concurrent clients sending transactions.
     */
    private static void runAutomatedDemo() throws Exception {
        // Start server in background thread
        Thread serverThread = new Thread(() -> {
            try {
                startServer();
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Give server time to start
        Thread.sleep(1000);

        System.out.println("\n========== AUTOMATED MULTI-CLIENT DEMO ==========\n");

        // Simulate Client 1: Simple write transaction
        Thread client1 = new Thread(() -> simulateClient("AUTO-1", Arrays.asList(
            new Operation(Operation.Type.WRITE, "user:001", "Alice"),
            new Operation(Operation.Type.WRITE, "user:002", "Bob")
        )));

        // Simulate Client 2: Read + Write mixed transaction
        Thread client2 = new Thread(() -> simulateClient("AUTO-2", Arrays.asList(
            new Operation(Operation.Type.READ, "user:001", null),
            new Operation(Operation.Type.WRITE, "user:003", "Charlie"),
            new Operation(Operation.Type.WRITE, "product:100", "Laptop")
        )));

        // Simulate Client 3: Multiple writes across different keys (will hit different partitions)
        Thread client3 = new Thread(() -> simulateClient("AUTO-3", Arrays.asList(
            new Operation(Operation.Type.WRITE, "order:999", "{\"item\":\"Laptop\",\"qty\":2}"),
            new Operation(Operation.Type.WRITE, "inventory:100", "48"),
            new Operation(Operation.Type.DELETE, "temp:cache", null)
        )));

        // Simulate Client 4: Transaction that might conflict or hit same keys
        Thread client4 = new Thread(() -> simulateClient("AUTO-4", Arrays.asList(
            new Operation(Operation.Type.WRITE, "user:001", "Alice-Updated"),
            new Operation(Operation.Type.WRITE, "user:002", "Bob-Updated")
        )));

        client1.start();
        client2.start();
        client3.start();
        client4.start();

        client1.join();
        client2.join();
        client3.join();
        client4.join();

        System.out.println("\n========== DEMO COMPLETE ==========");

        // Print final state of partitions
        System.out.println("\nFinal partition states would be logged above.");
        System.out.println("In a real system, you'd query the stores for final values.");
    }

    private static void simulateClient(String clientId, List<Operation> ops) {
        try {
            TransactionClient client = new TransactionClient(clientId, SERVER_HOST, SERVER_PORT);
            client.connect();
            System.out.println("[" + clientId + "] Connected. Sending " + ops.size() + " operation(s)...");
            
            Transaction result = client.sendTransaction(ops);
            System.out.println("[" + clientId + "] Transaction " + result.getTxId() + " => " + result.getStatus());
            
            client.disconnect();
        } catch (Exception e) {
            System.err.println("[" + clientId + "] Error: " + e.getMessage());
        }
    }
}

