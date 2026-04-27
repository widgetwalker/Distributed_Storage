import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Thread-safe key-value store representing a data partition in the distributed system.
 */
public class KeyValueStore {
    private final String storeId;
    private final Map<String, String> data = new ConcurrentHashMap<>();
    private final Map<String, String> preparedData = new ConcurrentHashMap<>();

    public KeyValueStore(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreId() { return storeId; }

    public synchronized String read(String key) {
        return data.get(key);
    }

    public synchronized void write(String key, String value) {
        data.put(key, value);
    }

    public synchronized void delete(String key) {
        data.remove(key);
    }

    // Two-phase commit: prepare phase
    public synchronized boolean prepare(String key, String value) {
        preparedData.put(key, value);
        System.out.println("[" + storeId + "] PREPARED: key=" + key + " value=" + value);
        return true;
    }

    // Two-phase commit: commit phase
    public synchronized void commit(String key) {
        String value = preparedData.remove(key);
        if (value != null) {
            data.put(key, value);
            System.out.println("[" + storeId + "] COMMITTED: key=" + key);
        }
    }

    // Two-phase commit: rollback phase
    public synchronized void rollback(String key) {
        preparedData.remove(key);
        System.out.println("[" + storeId + "] ROLLED BACK: key=" + key);
    }

    public synchronized Map<String, String> snapshot() {
        return new ConcurrentHashMap<>(data);
    }
}

