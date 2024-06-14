import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;

public class ChatRoomServeur {
    public static void main (String [] args){
        try { System.out.println("Attempting to start XML-RPC Server...");
            WebServer server = new WebServer(80);
            XmlRpcClient chatUserServer = new XmlRpcClient("http://localhost:8000/RPC2");
            server.addHandler("chatRoom", new ChatRoom(chatUserServer));
            server.start();
            System.out.println("Started successfully.");
            System.out.println("ChatRoomServer : Accepting requests. (Halt program to stop.)");
        }
        catch (Exception exception){
            System.err.println("JavaServer: " + exception);
        }
    }
}
