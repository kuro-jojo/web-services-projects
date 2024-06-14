package com.server.bean;

import java.util.ArrayList;

public class Utilisateur {

    private String pseudo;
    private ArrayList<Message> listMsg;

    public Utilisateur() {
        this.pseudo = "";
        this.listMsg = new ArrayList<Message>();
    }

    public Utilisateur(String pseudo) {
        this.pseudo = pseudo;
        this.listMsg = new ArrayList<Message>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public ArrayList<Message> getListMsg() {
        return listMsg;
    }

    public void setListMsg(ArrayList<Message> listMsg) {
        this.listMsg = listMsg;
    }

}
