package calculator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InterestCalculatorImpl extends UnicastRemoteObject implements InterestCalculator {

    public InterestCalculatorImpl() throws RemoteException {
        super();
    }

    @Override
    public double calculateSimpleInterest(double principal, double rate, double time) throws RemoteException {
        System.out.println(
                "[Server] Calculating Simple Interest: P=" + principal + ", R=" + rate + "%, T=" + time + " years");
        double si = (principal * rate * time) / 100.0;
        System.out.println("[Server] Simple Interest Result: " + si);
        return si;
    }

    @Override
    public double calculateCompoundInterest(double principal, double rate, double time) throws RemoteException {
        System.out.println(
                "[Server] Calculating Compound Interest: P=" + principal + ", R=" + rate + "%, T=" + time + " years");
        double amount = principal * Math.pow((1 + rate / 100.0), time);
        double ci = amount - principal;
        System.out.println("[Server] Compound Interest Result: " + ci);
        return ci;
    }
}
