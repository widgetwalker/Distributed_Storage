import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <NodeId>");
            return;
        }

        String nodeId = args[0].toUpperCase();
        try {
            ConfigLoader.load();
            ClusterManager cluster = new ClusterManager(nodeId);
            runCli(cluster, nodeId);
        } catch (IOException e) {
            System.err.println("Init failed: " + e.getMessage());
        }
    }

    private static void runCli(ClusterManager cluster, String nodeId) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printStatus(cluster, nodeId);
            System.out.println("\n[1] Create File  [2] Read State  [3] Exit");
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                System.out.println("\nInput stream closed. Exiting...");
                break;
            }
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Filename: ");
                if (!scanner.hasNextLine())
                    break;
                String name = scanner.nextLine().trim();
                if (!cluster.storeFile(name)) {
                    System.out.println("Local node FULL. Asking coordinator for help...");

                    String leader = cluster.getCoordinatorId();
                    if (leader == null) {
                        System.out.println("Error: No coordinator elected yet. Try again later.");
                    } else if (leader.equals(nodeId)) {
                        String target = cluster.findBestNodeForOverflow();
                        if (target != null) {
                            System.out.println("Coordinator (ME) directs overflow to: " + target);
                            cluster.requestRemoteStore(target, name);
                        } else {
                            System.out.println("CRITICAL: All nodes are full!");
                        }
                    } else {
                        System.out
                                .println("Follower: Leader " + leader + " will handle it (SIMULATION: Automated soon)");
                        // In a real system, we'd forward the request to the leader here.
                        // For this lab, we'll suggest the user use the leader's logic or wait for sync.
                        String target = cluster.findBestNodeForOverflow();
                        if (target != null) {
                            System.out.println("Based on synchronized state, recommending node: " + target);
                            cluster.requestRemoteStore(target, name);
                        }
                    }
                }
            } else if (choice.equals("3")) {
                System.exit(0);
            }
        }
    }

    private static void printStatus(ClusterManager cluster, String nodeId) {
        System.out.println("\n--- Cluster Status (" + nodeId + ") ---");
        System.out.println(
                "Coordinator: " + (cluster.getCoordinatorId() != null ? cluster.getCoordinatorId() : "ELECTING..."));

        Map<String, List<String>> state = cluster.getGlobalState();
        for (String id : new String[] { "A", "B", "C", "D" }) {
            List<String> files = state.get(id);
            System.out.println("Node " + id + ": " + files.size() + "/5 " + (id.equals(nodeId) ? "*" : ""));
            if (!files.isEmpty())
                System.out.println("   Files: " + files);
        }
    }
}
