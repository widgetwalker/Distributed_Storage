import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Unified demo that runs server and client in the same JVM.
 * Server output is prefixed with [SERVER].
 * Client output is prefixed with [CLIENT].
 */
public class UnifiedDemo {
    
    private static final int PORT = 7777;
    
    public static void main(String[] args) throws Exception {
        // Redirect server output to add [SERVER] prefix
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        
        // Start server in background thread
        Thread serverThread = new Thread(() -> {
            // Server prints to a custom stream with prefix
            PrintStream serverOut = new PrintStream(new OutputStream() {
                StringBuilder buffer = new StringBuilder();
                @Override
                public void write(int b) {
                    if (b == '\n') {
                        originalOut.println("[SERVER] " + buffer.toString());
                        buffer.setLength(0);
                    } else {
                        buffer.append((char) b);
                    }
                }
            });
            System.setOut(serverOut);
            System.setErr(serverOut);
            
            try {
                TransactionCoordinator coordinator = new TransactionCoordinator("COORD-ALPHA");
                coordinator.addParticipant(new Participant("PARTITION-A"));
                coordinator.addParticipant(new Participant("PARTITION-B"));
                coordinator.addParticipant(new Participant("PARTITION-C"));
                
                TransactionServer server = new TransactionServer(PORT, coordinator);
                server.start();
            } catch (IOException e) {
                serverOut.println("Server error: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Wait for server to start
        Thread.sleep(1500);
        
        // Restore original output for client
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Run client
        System.out.println("\n========== [CLIENT] CONNECTING TO SERVER ==========\n");
        
        TransactionClient client = new TransactionClient("DEMO-CLIENT", "localhost", PORT);
        client.connect();
        
        // Transaction 1
        System.out.println("[CLIENT] >>> Sending Transaction 1: WRITE account:101=5000, WRITE account:102=3000");
        Transaction tx1 = client.sendTransaction(Arrays.asList(
            new Operation(Operation.Type.WRITE, "account:101", "5000"),
            new Operation(Operation.Type.WRITE, "account:102", "3000")
        ));
        System.out.println("[CLIENT] <<< Result: " + tx1.getStatus() + " | ID: " + tx1.getTxId().substring(0,8));
        System.out.println();
        
        // Transaction 2
        System.out.println("[CLIENT] >>> Sending Transaction 2: READ account:101, WRITE account:103=10000");
        Transaction tx2 = client.sendTransaction(Arrays.asList(
            new Operation(Operation.Type.READ, "account:101", null),
            new Operation(Operation.Type.WRITE, "account:103", "10000")
        ));
        System.out.println("[CLIENT] <<< Result: " + tx2.getStatus() + " | ID: " + tx2.getTxId().substring(0,8));
        System.out.println();
        
        // Transaction 3
        System.out.println("[CLIENT] >>> Sending Transaction 3: DELETE temp:data, WRITE log:audit=complete");
        Transaction tx3 = client.sendTransaction(Arrays.asList(
            new Operation(Operation.Type.DELETE, "temp:data", null),
            new Operation(Operation.Type.WRITE, "log:audit", "complete")
        ));
        System.out.println("[CLIENT] <<< Result: " + tx3.getStatus() + " | ID: " + tx3.getTxId().substring(0,8));
        System.out.println();
        
        client.disconnect();
        System.out.println("[CLIENT] Disconnected.");
        System.out.println("\n========== ALL TRANSACTIONS COMPLETE ==========");
        
        // Give server time to finish logging
        Thread.sleep(500);
        System.exit(0);
    }
}

