import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Arithmetic extends Remote {
    double add(double a, double b) throws RemoteException;
    double subtract(double a, double b) throws RemoteException;
    double multiply(double a, double b) throws RemoteException;
    double divide(double a, double b) throws RemoteException;
    // new method: server executes operation based on op string
    double compute(String op, double a, double b) throws RemoteException;
}

