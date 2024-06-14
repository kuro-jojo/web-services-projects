import java.rmi.*;
public interface ChatUser extends Remote {
    public Boolean displayMessage(String message) throws
            RemoteException;
}