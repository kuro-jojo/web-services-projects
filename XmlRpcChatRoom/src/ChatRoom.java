import org.apache.xmlrpc.XmlRpcClient;

import java.rmi.RemoteException;
import java.util.*;

public class ChatRoom{

    private List<String> users = new ArrayList<>();
    private XmlRpcClient chatUserServer;
    static  final String joined = "joined the chat room.\n";
    static  final String left = "left the chat room.\n";

    public ChatRoom(XmlRpcClient chatUserServer) throws RemoteException{
        this.chatUserServer = chatUserServer;
    }

    public Boolean subscribe(String pseudo) throws RemoteException {
        if(!users.contains(pseudo)) {
            users.add(pseudo);
            this.postMessage(pseudo, joined);
            return true;
        }
        return false;
    }

    public Boolean unsubscribe(String pseudo) throws RemoteException {
        this.postMessage(pseudo, left);
        users.remove(pseudo);
        return true;
    }

    public Boolean postMessage(String pseudo, String message) throws RemoteException {
        if(users.contains(pseudo)) {
            for (var user : users) {
                if (!user.equals(pseudo)) {
                    Vector<Object> params = new Vector<>();
                    params.add(pseudo + ": " + message);
                    try {
                        chatUserServer.execute(user + "." + "displayMessage", params);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
