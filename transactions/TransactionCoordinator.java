import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Implements the Two-Phase Commit (2PC) protocol for distributed transactions.
 * Coordinates between multiple participants to ensure atomicity.
 */
public class TransactionCoordinator {
    private final String coordinatorId;
    private final Map<String, Participant> participants;
    private final ExecutorService executor;

    public TransactionCoordinator(String coordinatorId) {
        this.coordinatorId = coordinatorId;
        this.participants = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    public void addParticipant(Participant participant) {
        participants.put(participant.getParticipantId(), participant);
    }

    public Participant getParticipant(String id) {
        return participants.get(id);
    }

    public Map<String, Participant> getAllParticipants() {
        return participants;
    }

    /**
     * Execute a distributed transaction using Two-Phase Commit.
     */
    public Transaction executeTransaction(Transaction transaction) {
        System.out.println("\n=== COORDINATOR [" + coordinatorId + "] Starting 2PC for " + transaction.getTxId() + " ===");
        transaction.setStatus(TransactionStatus.PENDING);
        
        List<Operation> operations = transaction.getOperations();
        List<Participant> involvedParticipants = new ArrayList<>();
        
        // Assign operations to participants (simple hash-based partitioning)
        for (Operation op : operations) {
            Participant p = getParticipantForKey(op.getKey());
            if (p != null) {
                involvedParticipants.add(p);
            }
        }

        // ===== PHASE 1: PREPARE =====
        System.out.println("--- PHASE 1: PREPARE ---");
        boolean allYes = true;
        List<Future<Boolean>> prepareFutures = new ArrayList<>();
        
        for (int i = 0; i < operations.size(); i++) {
            final Operation op = operations.get(i);
            final Participant p = getParticipantForKey(op.getKey());
            if (p == null) continue;
            
            Future<Boolean> future = executor.submit(() -> p.prepare(transaction, op));
            prepareFutures.add(future);
        }
        
        // Collect votes
        for (Future<Boolean> future : prepareFutures) {
            try {
                boolean vote = future.get(5, TimeUnit.SECONDS);
                if (!vote) {
                    allYes = false;
                }
            } catch (Exception e) {
                System.err.println("Prepare phase error: " + e.getMessage());
                allYes = false;
            }
        }

        // ===== PHASE 2: COMMIT or ABORT =====
        if (allYes) {
            System.out.println("--- PHASE 2: COMMIT ---");
            transaction.setStatus(TransactionStatus.COMMITTED);
            for (Operation op : operations) {
                Participant p = getParticipantForKey(op.getKey());
                if (p != null) {
                    p.commit(op);
                }
            }
            System.out.println("=== TRANSACTION COMMITTED: " + transaction.getTxId() + " ===\n");
        } else {
            System.out.println("--- PHASE 2: ABORT ---");
            transaction.setStatus(TransactionStatus.ABORTED);
            for (Operation op : operations) {
                Participant p = getParticipantForKey(op.getKey());
                if (p != null) {
                    p.rollback(op);
                }
            }
            System.out.println("=== TRANSACTION ABORTED: " + transaction.getTxId() + " ===\n");
        }
        
        return transaction;
    }

    /**
     * Simple consistent hashing to determine which participant handles a key.
     */
    private Participant getParticipantForKey(String key) {
        if (participants.isEmpty()) return null;
        List<Participant> list = new ArrayList<>(participants.values());
        int index = Math.abs(key.hashCode()) % list.size();
        return list.get(index);
    }

    public void shutdown() {
        executor.shutdown();
    }
}

