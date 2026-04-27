import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable transaction envelope sent between client and server.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String txId;
    private final String clientId;
    private final List<Operation> operations;
    private volatile TransactionStatus status;
    private final long createdAt;
    private final Map<String, String> readResults = new HashMap<>();

    public Transaction(String clientId, List<Operation> operations) {
        this.txId = UUID.randomUUID().toString();
        this.clientId = clientId;
        // Defensive copy to preserve immutability contract
        this.operations = Collections.unmodifiableList(new ArrayList<>(operations));
        this.status = TransactionStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
    }

    public synchronized TransactionStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getTxId() { return txId; }
    public String getClientId() { return clientId; }
    public List<Operation> getOperations() { return operations; }
    public long getCreatedAt() { return createdAt; }
    public synchronized Map<String, String> getReadResults() { return new HashMap<>(readResults); }
    public synchronized void addReadResult(String key, String value) { readResults.put(key, value); }

    @Override
    public String toString() {
        return "Transaction{" +
                "txId='" + txId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", ops=" + operations.size() +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}

