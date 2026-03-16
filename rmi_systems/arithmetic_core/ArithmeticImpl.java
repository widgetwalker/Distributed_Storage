import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.time.LocalDateTime;

public class ArithmeticImpl extends UnicastRemoteObject implements Arithmetic {
    protected ArithmeticImpl() throws RemoteException {
        super();
    }

    public double add(double a, double b) throws RemoteException {
        double res = a + b;
        log(String.format("add(%.4f, %.4f) -> %.4f", a, b, res));
        return res;
    }

    public double subtract(double a, double b) throws RemoteException {
        double res = a - b;
        log(String.format("subtract(%.4f, %.4f) -> %.4f", a, b, res));
        return res;
    }

    public double multiply(double a, double b) throws RemoteException {
        double res = a * b;
        log(String.format("multiply(%.4f, %.4f) -> %.4f", a, b, res));
        return res;
    }

    public double divide(double a, double b) throws RemoteException {
        if (b == 0) {
            log(String.format("divide(%.4f, %.4f) -> DIVISION_BY_ZERO", a, b));
            throw new RemoteException("Division by zero");
        }
        double res = a / b;
        log(String.format("divide(%.4f, %.4f) -> %.4f", a, b, res));
        return res;
    }

    private void log(String message) {
        String client = "unknown";
        try {
            client = RemoteServer.getClientHost();
        } catch (ServerNotActiveException ignored) {
        }
        System.out.println("[" + LocalDateTime.now() + "] [client=" + client + "] " + message);
    }

    public double compute(String op, double a, double b) throws RemoteException {
        log(String.format("compute(%s, %.4f, %.4f) called", op, a, b));
        switch (op.toLowerCase()) {
            case "add":
            case "+":
                return add(a, b);
            case "subtract":
            case "sub":
            case "-":
                return subtract(a, b);
            case "multiply":
            case "mul":
            case "*":
                return multiply(a, b);
            case "divide":
            case "div":
            case "/":
                return divide(a, b);
            default:
                log("Unknown operation requested: " + op);
                throw new RemoteException("Unknown operation: " + op);
        }
    }
}

