package com.kuro.chatroom.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.kuro.chatroom.Message;
import com.kuro.chatroom.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatUser {
    private final int REFRESH_RATE = 1;
    private String title = "Logiciel de discussion en ligne";
    private User user = new User();
    private RestTemplate restTemplate = new RestTemplate();
    private String baseUrl = "http://localhost:8080/api";
    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Send");
    private JButton btnJoin = new JButton("Join chat");
    private JButton btnQuit = new JButton("Quit chat");

    private Instant lastUpdate = Instant.now().minusSeconds(24 * 60 * 60);

    public ChatUser() {
        this.createIHM();
        this.requestPseudo();
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
        // Gestion des événements
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
                if (event.getKeyChar() == '\n' && txtMessage.getText().length() > 0)
                    btnSend_actionPerformed(null);
                if (txtMessage.getText().length() == 0)
                    btnSend.setEnabled(false);
                else
                    btnSend.setEnabled(true);
            }
        });

        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220, 220, 220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500, 400);
        this.window.setVisible(true);
        this.txtMessage.setEnabled(false);
        btnSend.setEnabled(false);
    }

    public void btnJoin_actionPerformed(ActionEvent e) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + "/subscribe/" + user.getPseudo(),
                    String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                restTemplate.postForEntity(baseUrl + "/messages",
                        new Message("INFO", this.user.getPseudo() + " joined the chat room"),
                        Void.class);
                txtMessage.setEnabled(true);
                user.setJoinedAt(new Date());
            } else if (responseEntity.getStatusCode() == HttpStatus.CONFLICT) {
                JOptionPane.showMessageDialog(this.window, "Pseudo existe deja! Veuillez en choisir un autre");
                requestPseudo();
            }
        } catch (Exception ex) {
            System.out.println("Error while joining chat: " + ex.getMessage());
        }
        startPolling();
    }

    public void btnQuit_actionPerformed(ActionEvent e) {
        window_windowClosing(null);
    }

    public void requestPseudo() {
        this.user.setPseudo(JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title, JOptionPane.OK_OPTION));
        if (this.user.getPseudo() == null)
            System.exit(0);

        System.out.println(this.user.getPseudo() + " added successfully to the server.");
    }

    public void window_windowClosing(WindowEvent e) {
        try {
            unsubscribe();
        } catch (Exception ex) {
            System.out.println("Error while unsubscribing: " + ex.getMessage());
        }
        window.dispose();
    }

    private void unsubscribe() {
        restTemplate.getForEntity(baseUrl + "/unsubscribe/" + this.user.getPseudo(), String.class);
        restTemplate.postForEntity(baseUrl + "/messages",
                new Message("INFO", this.user.getPseudo() + " left the chat room"),
                Void.class);
    }

    public void startPolling() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            List<Message> messages = receiveMessages();
            if (!messages.isEmpty()) {
                for (Message message : messages) {
                    if (message.getPseudo().equals(user.getPseudo())) {
                        txtOutput.append("You : " + message.getContent() + "\n");
                    } else {
                        txtOutput.append(message.getPseudo() + " : " + message.getContent() + "\n");
                    }
                }
            }
        }, 0, REFRESH_RATE, TimeUnit.SECONDS);
    }

    public List<Message> receiveMessages() {
        ResponseEntity<List<Message>> responseEntity = restTemplate.exchange(
                baseUrl + "/messages?time=" + lastUpdate.toEpochMilli() + "&bef=" + user.getJoinedAt(), HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Message>>() {
                });
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            lastUpdate = Instant.now();
            return responseEntity.getBody();
        }
        return new ArrayList<>();
    }

    public void btnSend_actionPerformed(ActionEvent e) {
        String message = this.txtMessage.getText();
        restTemplate.postForEntity(baseUrl + "/messages", new Message(user.getPseudo(), message), Void.class);
        this.txtMessage.setText("");
        btnSend.setEnabled(false);
        this.txtMessage.requestFocus();
    }

    public static void main(String[] args) {
        new ChatUser();
        new ChatUser();
    }
}
