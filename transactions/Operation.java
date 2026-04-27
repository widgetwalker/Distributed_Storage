import java.io.Serializable;

public class Operation implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Type { READ, WRITE, DELETE }

    private final String id;
    private final Type type;
    private final String key;
    private final String value; // nullable for READ/DELETE
    private final long timestamp;

    public Operation(Type type, String key, String value) {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.key = key;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getKey() { return key; }
    public String getValue() { return value; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Operation[%s %s key=%s val=%s]", id.substring(0,4), type, key, value);
    }
}

