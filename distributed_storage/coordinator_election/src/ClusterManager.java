import java.io.*;
import java.util.*;

public class ClusterManager {
    private final String myId;
    private final int myPriority;
    private final StorageNode localNode;
    private final NetworkManager networkManager;
    private final Map<String, List<String>> globalFiles;

    private String coordinatorId = null;
    private boolean isElectionInProgress = false;
    private long lastHeartbeatTime = 0;

    public ClusterManager(String myId) throws IOException {
        this.myId = myId;
        this.myPriority = ConfigLoader.getPriority(myId);
        this.globalFiles = new HashMap<>();
        for (String id : Arrays.asList("A", "B", "C", "D")) {
            globalFiles.put(id, new ArrayList<>());
        }

        this.localNode = new StorageNode(myId);
        this.networkManager = new NetworkManager(myId, this::onNetworkMessage);
        this.networkManager.startServer();

        startElection(); // Initial election
        startMonitoringThreads();
    }

    private void startMonitoringThreads() {
        // Heartbeat thread for Coordinator
        new Thread(() -> {
            while (true) {
                if (myId.equals(coordinatorId)) {
                    networkManager.broadcast("HEARTBEAT:" + myId);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        // Failure detection thread for Followers
        new Thread(() -> {
            while (true) {
                if (!myId.equals(coordinatorId) && coordinatorId != null) {
                    if (System.currentTimeMillis() - lastHeartbeatTime > 5000) {
                        System.out.println("Coordinator " + coordinatorId + " suspected dead. Starting election...");
                        coordinatorId = null;
                        startElection();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    private synchronized void startElection() {
        if (isElectionInProgress)
            return;
        isElectionInProgress = true;
        System.out.println("Election started by " + myId);

        boolean higherNodeFound = false;
        String[] nodes = { "A", "B", "C", "D" };
        for (String node : nodes) {
            if (ConfigLoader.getPriority(node) > myPriority) {
                networkManager.sendToNode(node, "ELECTION:" + myId);
                higherNodeFound = true;
            }
        }

        if (!higherNodeFound) {
            becomeCoordinator();
        } else {
            // Wait for OK or timeout
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                if (isElectionInProgress && coordinatorId == null) {
                    becomeCoordinator();
                }
            }).start();
        }
    }

    private synchronized void becomeCoordinator() {
        isElectionInProgress = false;
        coordinatorId = myId;
        System.out.println("--- I AM THE COORDINATOR (" + myId + ") ---");
        networkManager.broadcast("COORDINATOR:" + myId);
    }

    private void onNetworkMessage(String msg) {
        String[] parts = msg.split(":");
        String cmd = parts[0];
        String senderId = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "ELECTION":
                if (ConfigLoader.getPriority(myId) > ConfigLoader.getPriority(senderId)) {
                    networkManager.sendToNode(senderId, "OK:" + myId);
                    startElection();
                }
                break;
            case "OK":
                isElectionInProgress = false;
                break;
            case "COORDINATOR":
                isElectionInProgress = false;
                coordinatorId = senderId;
                lastHeartbeatTime = System.currentTimeMillis();
                System.out.println("New Coordinator: " + coordinatorId);
                break;
            case "HEARTBEAT":
                if (senderId.equals(coordinatorId)) {
                    lastHeartbeatTime = System.currentTimeMillis();
                }
                break;
            case "UPDATE":
                if (parts.length > 2) {
                    List<String> files = parts[2].isEmpty() ? new ArrayList<>() : Arrays.asList(parts[2].split(","));
                    globalFiles.put(senderId, files);
                }
                break;
            case "STORE_REQUEST":
                if (parts.length > 2) {
                    localNode.addFile(parts[2]);
                    broadcastUpdate();
                }
                break;
        }
    }

    public synchronized String findBestNodeForOverflow() {
        // Coordinator logic: Find node with most space
        String best = null;
        int minCount = 6;
        for (String id : globalFiles.keySet()) {
            int count = globalFiles.get(id).size();
            if (count < 5 && count < minCount) {
                minCount = count;
                best = id;
            }
        }
        return best;
    }

    public boolean storeFile(String fileName) {
        if (localNode.addFile(fileName)) {
            broadcastUpdate();
            return true;
        }
        return false;
    }

    private void broadcastUpdate() {
        String fileList = String.join(",", localNode.getFiles());
        networkManager.broadcast("UPDATE:" + myId + ":" + fileList);
    }

    public void requestRemoteStore(String targetId, String fileName) {
        networkManager.sendToNode(targetId, "STORE_REQUEST:" + myId + ":" + fileName);
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public StorageNode getLocalNode() {
        return localNode;
    }

    public Map<String, List<String>> getGlobalState() {
        return globalFiles;
    }
}
