import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DNSInterface extends Remote {
    String lookup(String domainName) throws RemoteException;
}
