import java.util.Arrays;

/**
 * A non-interactive client that demonstrates transactions being sent to a running server.
 */
public class NonInteractiveClient {
    public static void main(String[] args) throws Exception {
        String clientId = "DEMO-CLIENT";
        TransactionClient client = new TransactionClient(clientId, "localhost", 7777);
        client.connect();
        
        System.out.println("\n========== [CLIENT] STARTING TRANSACTIONS ==========\n");
        
        // Transaction 1: Write two accounts
        System.out.println("[CLIENT] >>> Sending Transaction 1: WRITE account:101=5000, WRITE account:102=3000");
        Transaction tx1 = client.sendTransaction(Arrays.asList(
            new Operation(Operation.Type.WRITE, "account:101", "5000"),
            new Operation(Operation.Type.WRITE, "account:102", "3000")
        ));
        System.out.println("[CLIENT] <<< Result: " + tx1.getStatus() + " | ID: " + tx1.getTxId().substring(0,8));
        System.out.println();
        
        // Transaction 2: Read + Write
        System.out.println("[CLIENT] >>> Sending Transaction 2: READ account:101, WRITE account:103=10000");
        Transaction tx2 = client.sendTransaction(Arrays.asList(
            new Operation(Operation.Type.READ, "account:101", null),
            new Operation(Operation.Type.WRITE, "account:103", "10000")
        ));
        System.out.println("[CLIENT] <<< Result: " + tx2.getStatus() + " | ID: " + tx2.getTxId().substring(0,8));
        System.out.println();
        
        // Transaction 3: Delete + Write
        System.out.println("[CLIENT] >>> Sending Transaction 3: DELETE temp:data, WRITE log:audit=complete");
        Transaction tx3 = client.sendTransaction(Arrays.asList(
            new Operation(Operation.Type.DELETE, "temp:data", null),
            new Operation(Operation.Type.WRITE, "log:audit", "complete")
        ));
        System.out.println("[CLIENT] <<< Result: " + tx3.getStatus() + " | ID: " + tx3.getTxId().substring(0,8));
        System.out.println();
        
        client.disconnect();
        System.out.println("[CLIENT] Disconnected. All transactions complete!");
    }
}

