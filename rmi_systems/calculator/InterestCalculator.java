package calculator;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterestCalculator extends Remote {

    /**
     * Calculate Simple Interest
     * Formula: SI = (P * R * T) / 100
     */
    double calculateSimpleInterest(double principal, double rate, double time) throws RemoteException;

    /**
     * Calculate Compound Interest
     * Formula: CI = P * (1 + R/100)^T - P
     */
    double calculateCompoundInterest(double principal, double rate, double time) throws RemoteException;
}
