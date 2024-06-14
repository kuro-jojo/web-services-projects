package com.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import static org.apache.taglibs.standard.functions.Functions.split;

public class Gui extends JFrame {

    private JPanel contentPane;
    private JTextField zoneTexte;
    private JTextArea zoneMsg;
    private String pseudo;
    private Client service;
    private Thread updateMsg;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Gui frame = new Gui();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Gui() {

        ihm();
        try {
            service = new Client();
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }

        requestPseudo();

        updateMsg = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {

                    }
                    updateZoneMessage();

                }
            }
        });
        updateMsg.start();
    }

    private void ihm() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        zoneMsg = new JTextArea();
        zoneMsg.setEditable(false);
        contentPane.add(zoneMsg, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);

        zoneTexte = new JTextField();
        panel.add(zoneTexte);
        zoneTexte.setColumns(28);

        JButton btnSend = new JButton("send");
        panel.add(btnSend);
        btnSend.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = zoneTexte.getText();
                if (msg.equals(""))
                    msg = "est connecté(e)";
                service.getRoom().postMsg("pseudo", pseudo+"---> "+msg);
                zoneTexte.setText("");

            }
        });
    }

    private void requestPseudo() {
        pseudo = JOptionPane.showInputDialog("entre le pseudo");
        if (pseudo == null) {
            System.exit(0);
        }
        System.out.println(pseudo + " est connecté");
        service.inscription(pseudo);
    }

    protected void updateZoneMessage() {
        synchronized (service) {
            String[] msg = split(service.getRoom().getMessageUser(pseudo), "[|]|,");
            if (msg.length!=0) {
                this.zoneMsg.append(msg[0]);
            }
        }

    }
}
