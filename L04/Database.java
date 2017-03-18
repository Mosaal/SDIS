import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Database extends Remote {
    String makeRequest(String request) throws RemoteException;
}