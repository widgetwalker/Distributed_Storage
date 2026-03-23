import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RMIDNSClient {
    public static void main(String[] args) {
        String host = (args.length < 1) ? "localhost" : args[0];
        try {
            System.out.println("Connecting to DNS Server at: " + host);
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            DNSInterface stub = (DNSInterface) registry.lookup("DNSService");

            Scanner scanner = new Scanner(System.in);
            System.out.println("RMI DNS Client Started.");
            System.out.println("Type 'exit' to quit.");

            while (true) {
                System.out.print("Enter domain name to resolve: ");
                String domain = scanner.nextLine().trim();
                
                if (domain.equalsIgnoreCase("exit")) {
                    break;
                }

                if (domain.isEmpty()) {
                    continue;
                }

                String ip = stub.lookup(domain);
                System.out.println("IP Address for '" + domain + "': " + ip);
            }
            
            scanner.close();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
