package login;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class LoginServer {
    public static void main(String[] args) {
        try {
            LoginImpl service = new LoginImpl();
            // Start the registry on port 1099, ignore if already running
            try {
                LocateRegistry.createRegistry(1099);
            } catch (Exception e) {
                // Registry might already be running, this is fine
            }

            Naming.rebind("rmi://localhost/LoginService", service);
            System.out.println("LoginService is running on " + java.net.InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
