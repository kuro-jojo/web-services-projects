import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoom{

    private Map<String, ChatUser> users = new HashMap<>();
    static  final String joined = "joined the chat room.\n";
    static  final String left = "left the chat room.\n";

    public ChatRoomImpl() throws RemoteException{

    }

    @Override
    public void subscribe(ChatUser user, String pseudo) throws RemoteException {
        users.put(pseudo, user);
        this.postMessage(pseudo, joined);
    }

    @Override
    public void unsubscribe(String pseudo) throws RemoteException {
        this.postMessage(pseudo, left);
        users.remove(pseudo);
    }

    @Override
    public void postMessage(String pseudo, String message) throws RemoteException {
        if(users.containsKey(pseudo))
            for (var entry : users.entrySet()) {
                if(!entry.getKey().equals(pseudo)){
                    entry.getValue().displayMessage(pseudo + ": " + message);
                }
            }
    }

    public static void main(String[] args){

        String host = "localhost";
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ChatRoom chatRoom = new ChatRoomImpl();
            registry.bind("distChatRoom", chatRoom);
            System.out.println("Chat room started successfully.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
