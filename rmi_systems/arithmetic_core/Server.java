import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            ArithmeticImpl service = new ArithmeticImpl();
            LocateRegistry.createRegistry(1099); // starts registry in-process
            Naming.rebind("rmi://localhost/ArithmeticService", service);
            Naming.rebind("rmi://localhost/ArithmeticService", service);
            System.out
                    .println("ArithmeticService is running on " + java.net.InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
