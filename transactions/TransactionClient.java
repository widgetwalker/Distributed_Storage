import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Client that connects to the TransactionServer and allows user input to create transactions.
 * Supports multiple concurrent clients.
 */
public class TransactionClient {
    private final String clientId;
    private final String serverHost;
    private final int serverPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Scanner scanner;

    public TransactionClient(String clientId, String serverHost, int serverPort) {
        this.clientId = clientId;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.scanner = new Scanner(System.in);
    }

    public void connect() throws IOException, ClassNotFoundException {
        socket = new Socket(serverHost, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        
        Object response = in.readObject();
        System.out.println("[" + clientId + "] " + response);
    }

    public void startInteractiveSession() {
        System.out.println("\n=== Distributed Transaction Client [" + clientId + "] ===");
        System.out.println("Commands:");
        System.out.println("  WRITE <key> <value>  - Add write operation to transaction");
        System.out.println("  READ <key>           - Add read operation to transaction");
        System.out.println("  DELETE <key>         - Add delete operation to transaction");
        System.out.println("  COMMIT               - Send transaction to server");
        System.out.println("  STATUS               - Show current transaction operations");
        System.out.println("  CLEAR                - Clear current transaction operations");
        System.out.println("  EXIT                 - Disconnect from server");
        System.out.println();

        List<Operation> currentOps = new ArrayList<>();

        while (true) {
            System.out.print("[" + clientId + "] > ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 3);
            String cmd = parts[0].toUpperCase();

            try {
                switch (cmd) {
                    case "WRITE":
                        if (parts.length < 3) {
                            System.out.println("Usage: WRITE <key> <value>");
                            break;
                        }
                        currentOps.add(new Operation(Operation.Type.WRITE, parts[1], parts[2]));
                        System.out.println("Added: WRITE " + parts[1] + " = " + parts[2]);
                        break;

                    case "READ":
                        if (parts.length < 2) {
                            System.out.println("Usage: READ <key>");
                            break;
                        }
                        currentOps.add(new Operation(Operation.Type.READ, parts[1], null));
                        System.out.println("Added: READ " + parts[1]);
                        break;

                    case "DELETE":
                        if (parts.length < 2) {
                            System.out.println("Usage: DELETE <key>");
                            break;
                        }
                        currentOps.add(new Operation(Operation.Type.DELETE, parts[1], null));
                        System.out.println("Added: DELETE " + parts[1]);
                        break;

                    case "COMMIT":
                        if (currentOps.isEmpty()) {
                            System.out.println("No operations to commit. Add some first.");
                            break;
                        }
                        Transaction tx = new Transaction(clientId, currentOps);
                        System.out.println("Sending transaction " + tx.getTxId() + " with " + currentOps.size() + " operation(s)...");
                        
                        out.writeObject(tx);
                        out.flush();
                        
                        Object result = in.readObject();
                        if (result instanceof Transaction) {
                            Transaction resultTx = (Transaction) result;
                            System.out.println("Result: " + resultTx.getStatus());
                            System.out.println("Transaction ID: " + resultTx.getTxId());
                        } else {
                            System.out.println("Server response: " + result);
                        }
                        currentOps.clear();
                        break;

                    case "STATUS":
                        System.out.println("Current operations (" + currentOps.size() + "):");
                        for (int i = 0; i < currentOps.size(); i++) {
                            System.out.println("  " + (i + 1) + ". " + currentOps.get(i));
                        }
                        break;

                    case "CLEAR":
                        currentOps.clear();
                        System.out.println("Cleared all operations.");
                        break;

                    case "EXIT":
                        out.writeObject("EXIT");
                        out.flush();
                        System.out.println("Disconnecting...");
                        disconnect();
                        return;

                    default:
                        System.out.println("Unknown command: " + cmd);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Quick method to send a pre-built transaction (for programmatic use).
     */
    public Transaction sendTransaction(List<Operation> operations) throws IOException, ClassNotFoundException {
        Transaction tx = new Transaction(clientId, operations);
        out.writeObject(tx);
        out.flush();
        return (Transaction) in.readObject();
    }
}

