import java.util.*;

class Node {
    int id;
    long time;

    Node(int id, long time) {
        this.id = id;
        this.time = time;
    }

    void updateTime(long newTime) {
        this.time = newTime;
    }
}

public class ClockSync {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Step 1: Ask user for number of nodes
        System.out.print("Enter number of nodes: ");
        int n = sc.nextInt();

        List<Node> nodes = new ArrayList<>();

        // Step 2: Input clock times for each node
        for (int i = 1; i <= n; i++) {
            System.out.print("Enter clock time for Node " + i + ": ");
            long time = sc.nextLong();
            nodes.add(new Node(i, time));
        }

        // Step 3: Calculate average time
        long sum = 0;
        for (Node node : nodes) {
            sum += node.time;
        }
        long avgTime = sum / nodes.size();

        // Step 4: Synchronize all clocks to average
        for (Node node : nodes) {
            node.updateTime(avgTime);
        }

        // Step 5: Display synchronized times
        System.out.println("\nSynchronized Clock Times:");
        for (Node node : nodes) {
            System.out.println("Node " + node.id + ": " + node.time);
        }

        sc.close();
    }
}
