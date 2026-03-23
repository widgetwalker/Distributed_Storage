import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;

public class DNSServerImpl extends UnicastRemoteObject implements DNSInterface {

    public DNSServerImpl() throws RemoteException {
        super();
    }

    @Override
    public String lookup(String domainName) throws RemoteException {
        System.out.println("Processing lookup request for: " + domainName);
        try {
            InetAddress address = InetAddress.getByName(domainName);
            return address.getHostAddress();
        } catch (Exception e) {
            return "Domain not found";
        }
    }
}
