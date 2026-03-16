import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static ClusterManager clusterManager;
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <NodeId> (e.g., A, B, C, D)");
            return;
        }

        String nodeId = args[0].toUpperCase();
        System.out.println("Starting Distributed Storage System [" + nodeId + "]...");

        try {
            ConfigLoader.load();
            clusterManager = new ClusterManager(nodeId);
        } catch (IOException e) {
            System.err.println("Error initializing system: " + e.getMessage());
            return;
        }

        // Give network a moment to sync
        try {
            Thread.sleep(500);
        } catch (Exception ignored) {
        }

        runCli(nodeId);
    }

    private static void runCli(String nodeId) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printStatus(nodeId);
            System.out.println("\nOptions: [1] Create File  [2] Delete File  [3] Read Global State  [4] Exit");
            System.out.print(GREEN + "CMD > " + RESET);

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleCreate(scanner, nodeId);
                    break;
                case "2":
                    System.out.print("Enter filename to delete: ");
                    String toDelete = scanner.nextLine().trim();
                    clusterManager.deleteFile(toDelete);
                    break;
                case "3":
                    System.out.println("Refreshing state...");
                    break;
                case "4":
                    System.out.println("Exiting...");
                    clusterManager.getLocalNode();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private static void handleCreate(Scanner scanner, String nodeId) {
        System.out.print("Enter filename to create: ");
        String fileName = scanner.nextLine().trim();

        if (clusterManager.storeFile(fileName)) {
            System.out.println("File stored successfully in " + nodeId);
        } else {
            System.out.println(RED + "System " + nodeId + " is FULL (5/5)!" + RESET);
            System.out.println("Where would you like to store this file?");
            System.out.println("Options:");

            Map<String, List<String>> globalState = clusterManager.getGlobalState();
            boolean optionsAvailable = false;

            for (String id : globalState.keySet()) {
                if (!id.equals(nodeId)) {
                    int count = globalState.get(id).size();
                    if (count < 5) {
                        System.out.println("- Type " + GREEN + "'" + id + "'" + RESET + " for System " + id + " ("
                                + count + "/5 free)");
                        optionsAvailable = true;
                    }
                }
            }
            System.out.println("- Type " + YELLOW + "'delete'" + RESET + " to remove local files");
            System.out.println("- Type 'cancel' to abort");

            System.out.print("Choice: ");
            String target = scanner.nextLine().trim().toUpperCase();

            if (target.equals("DELETE")) {
                System.out.print("Enter filename to delete from LOCAL " + nodeId + ": ");
                String toDelete = scanner.nextLine().trim();
                if (clusterManager.deleteFile(toDelete)) {
                    // Retry creating the original file
                    if (clusterManager.storeFile(fileName)) {
                        System.out.println("Space cleared! File " + fileName + " stored successfully.");
                    }
                } else {
                    System.out.println(RED + "File not found." + RESET);
                }
            } else if (globalState.containsKey(target)) {
                clusterManager.requestRemoteStore(target, fileName);
                System.out.println("Request sent to System " + target + " (Check terminal for success/failure).");
            } else if (!target.equals("CANCEL")) {
                System.out.println("Invalid selection.");
            }
        }
    }

    private static void printStatus(String nodeId) {
        System.out.println("\n=================================");
        System.out.println("    Active Workspace: System " + nodeId);
        System.out.println("=================================");

        StorageNode local = clusterManager.getLocalNode();
        Map<String, List<String>> global = clusterManager.getGlobalState();

        int totalFiles = 0;

        for (String id : global.keySet()) {
            List<String> files = global.get(id);
            int count = files.size();
            totalFiles += count;

            String status = id.equals(nodeId) ? YELLOW + "(YOU)" + RESET : "";
            System.out.printf("System %s: %d/5 files %s\n", id, count, status);
            if (!files.isEmpty()) {
                System.out.println("   Files: " + files);
            }
        }
        System.out.println("---------------------------------");
        System.out.println("Total Usage: " + totalFiles + "/20 files");
    }
}
