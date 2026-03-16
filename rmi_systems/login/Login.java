package login;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Login extends Remote {
    String register(String username, String password) throws RemoteException;

    boolean authenticate(String username, String password) throws RemoteException;
}
