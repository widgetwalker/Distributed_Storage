package calculator;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class InterestServer {
    public static void main(String[] args) {
        try {
            // Start the RMI registry on port 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("[Server] RMI Registry started on port 1099.");

            // Create and bind the remote object
            InterestCalculatorImpl obj = new InterestCalculatorImpl();
            Naming.rebind("rmi://localhost/InterestCalculator", obj);

            System.out.println("[Server] InterestCalculator service is ready and bound.");
            System.out.println("[Server] Waiting for client requests...");
        } catch (Exception e) {
            System.err.println("[Server] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
