import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;

public class RMIDNSServer {
    public static void main(String[] args) {
        try {
            // Get the local IP address to set the hostname for remote clients
            String hostIp = InetAddress.getLocalHost().getHostAddress();
            System.setProperty("java.rmi.server.hostname", hostIp);
            
            DNSInterface dnsService = new DNSServerImpl();
            
            // Create or get the RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Bind the remote object's stub in the registry
            registry.rebind("DNSService", dnsService);
            
            System.out.println("DNS Server is running on: " + hostIp);
            System.out.println("RMI Registry started on port 1099");
            System.out.println("DNS Service bound in registry as 'DNSService'");
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
