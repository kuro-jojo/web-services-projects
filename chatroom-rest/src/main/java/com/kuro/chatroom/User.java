package com.kuro.chatroom;

import java.util.Date;

public class User {
    private String pseudo;
    private Date joinedAt;

    public User() {
    }

    public User(String pseudo) {
        this.pseudo = pseudo;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

}
