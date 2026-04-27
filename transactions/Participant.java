import java.util.List;

/**
 * Represents a participant (node/resource manager) in the distributed transaction.
 * Each participant manages a partition of the key-value store.
 */
public class Participant {
    private final String participantId;
    private final KeyValueStore store;
    private volatile boolean vote;

    public Participant(String participantId) {
        this.participantId = participantId;
        this.store = new KeyValueStore(participantId);
        this.vote = true;
    }

    public String getParticipantId() { return participantId; }
    public KeyValueStore getStore() { return store; }

    /**
     * Phase 1: Prepare - participant votes on whether it can commit.
     */
    public boolean prepare(Transaction transaction, Operation operation) {
        System.out.println("[" + participantId + "] Preparing operation: " + operation);
        // Simulate some validation logic
        if (operation.getKey() == null || operation.getKey().isEmpty()) {
            vote = false;
            System.out.println("[" + participantId + "] VOTE: NO (invalid key)");
            return false;
        }
        
        if (operation.getType() == Operation.Type.WRITE) {
            store.prepare(operation.getKey(), operation.getValue());
        } else if (operation.getType() == Operation.Type.READ) {
            String value = store.read(operation.getKey());
            transaction.addReadResult(operation.getKey(), value);
            System.out.println("[" + participantId + "] READ: key=" + operation.getKey() + " value=" + value);
        }
        
        vote = true;
        System.out.println("[" + participantId + "] VOTE: YES");
        return true;
    }

    /**
     * Phase 2a: Commit the prepared operation.
     */
    public void commit(Operation operation) {
        System.out.println("[" + participantId + "] Committing: " + operation);
        switch (operation.getType()) {
            case WRITE:
                store.commit(operation.getKey());
                break;
            case DELETE:
                store.delete(operation.getKey());
                break;
            case READ:
                // Read is non-mutating, nothing to commit
                break;
        }
    }

    /**
     * Phase 2b: Rollback the prepared operation.
     */
    public void rollback(Operation operation) {
        System.out.println("[" + participantId + "] Rolling back: " + operation);
        if (operation.getType() == Operation.Type.WRITE) {
            store.rollback(operation.getKey());
        }
    }

    public boolean getVote() { return vote; }
}

