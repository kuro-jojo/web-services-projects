import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcClient;

public class ChatUserServeur {
    public static void main (String [] args) {
        try {
            XmlRpcClient chatRoomServer = new XmlRpcClient("http://localhost/RPC2");
            System.out.println("Attempting to start XML-RPC Server...");
            WebServer server = new WebServer(8000);
            new ChatUserImpl(chatRoomServer, server);
            new ChatUserImpl(chatRoomServer, server);
        } catch (Exception exception) {
            System.err.println("JavaServer: " + exception);
            exception.printStackTrace();
        }
    }
}
