import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.*;

public class ChatUserImpl extends UnicastRemoteObject implements ChatUser {
    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;

    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Send");
    private JButton btnJoin = new JButton("Join chat");
    private JButton btnQuit = new JButton("Quit chat");

    private ChatRoom chatRoom;

    public ChatUserImpl() throws RemoteException {
        this.createIHM();
        this.requestPseudo();
    }

    public ChatUserImpl(ChatRoom chatRoom) throws RemoteException {
        this.createIHM();
        this.requestPseudo();
        this.chatRoom = chatRoom;
    }

    public void displayMessage(String message) throws RemoteException {
        this.txtOutput.append("\t\t\t\t" + message);
    }

    public void createIHM() {
        // Assemblage des composants
        JPanel panel = (JPanel) this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());

        JPanel southCenterPanel = new JPanel(new BorderLayout());
        southCenterPanel.add(this.txtMessage, BorderLayout.CENTER);
        southCenterPanel.add(this.btnSend, BorderLayout.EAST);
        southPanel.add(southCenterPanel, BorderLayout.CENTER);

        JPanel southEastPanel = new JPanel(new BorderLayout());
        southEastPanel.add(this.btnJoin, BorderLayout.WEST);
        southEastPanel.add(this.btnQuit, BorderLayout.EAST);
        southPanel.add(southEastPanel, BorderLayout.EAST);

        panel.add(southPanel, BorderLayout.SOUTH);

        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                window_windowClosing(e);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
        btnJoin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnJoin_actionPerformed(e);
            }
        });
        btnQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnQuit_actionPerformed(e);
            }
        });
        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == '\n')
                    btnSend_actionPerformed(null);
            }
        });

        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220, 220, 220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500, 400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }

    public void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title, JOptionPane.OK_OPTION);
        if (this.pseudo == null)
            System.exit(0);
    }

    public void window_windowClosing(WindowEvent e) {
        System.exit(-1);
    }

    public void btnSend_actionPerformed(ActionEvent e) {
        if (this.txtMessage.getText() != null && !this.txtMessage.getText().isBlank()) {
            String message = this.txtMessage.getText() + "\n";
            try {
                chatRoom.postMessage(this.pseudo, message);
                this.txtOutput.append("You: " + message);
                this.txtMessage.setText("");
                this.txtMessage.requestFocus();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public void btnJoin_actionPerformed(ActionEvent e) {
        String message = "You joined the chat room." + "\n";
        System.out.println("Joining Before for " + this.pseudo);
        try {
            chatRoom.subscribe(this, this.pseudo);
            this.txtOutput.append(message);
            this.txtMessage.requestFocus();
            System.out.println("Joining After for " + this.pseudo);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void btnQuit_actionPerformed(ActionEvent e) {
        String message = "You left the chat room" + "\n";
        System.out.println("Quitting Before for " + this.pseudo);
        try {
            chatRoom.unsubscribe(this.pseudo);
            this.txtOutput.append(message);
            this.txtMessage.requestFocus();
            System.out.println("Quitting After for " + this.pseudo);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String host = "localhost";
            Registry registry = LocateRegistry.getRegistry(host);
            ChatRoom chatRoom = (ChatRoom) registry.lookup("distChatRoom");
            new ChatUserImpl(chatRoom);
            new ChatUserImpl(chatRoom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}