package com.kuro.chatroom;

import java.util.Date;

public class Message {
    private String pseudo;
    private String content;
    private Date sentAt = new Date();

    public Message() {
    }

    public Message(String pseudo, String content) {
        this.pseudo = pseudo;
        this.content = content;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}