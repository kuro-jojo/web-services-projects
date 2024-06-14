package com.server;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.server.bean.Message;
import com.server.bean.Utilisateur;

@WebService(endpointInterface = "com.server.RoomService")
public class RoomServiceImpl implements RoomService {

    private final ArrayList<Utilisateur> userList;

    public RoomServiceImpl() {
        this.userList = new ArrayList<Utilisateur>();
    }

    @Override
    public boolean subscribe(String pseudo) {
        Utilisateur u = this.getUser(pseudo);
        synchronized (userList) {
            if (u != null) {
                return false;
            }
            userList.add(new Utilisateur(pseudo));
            System.out.println(pseudo);
            return true;
        }
    }

    @Override
    public boolean unsubscribe(String pseudo) {
        Utilisateur u = this.getUser(pseudo);
        synchronized (userList) {
            if (u == null) {
                return false;
            }
            userList.remove(u);
            return true;
        }
    }

    @Override
    public String getMessageUser(String pseudo) {
        Utilisateur u = this.getUser(pseudo);
        synchronized (userList) {
            if (u == null) {
                return "pas de message";
            }
            String m = u.getListMsg().toString();
            u.getListMsg().clear();
            return m; //return true;
        }
        //return null;
    }

    @Override
    public void postMsg(String pseudo, String Message) {
        //Utilisateur u = this.getUser(pseudo);
        Message m = new Message(pseudo, Message);
        synchronized (userList) {
            for (Utilisateur u : userList) {
                if (!u.getPseudo().equals(pseudo)) {
                    u.getListMsg().add(m);
                }
            }
            //
        }

    }

    private Utilisateur getUser(String pseudo) {
        for (Utilisateur u : userList) {
            if (u.getPseudo().equals(pseudo)) {
                return u;
            }
        }
        return null;
    }

}
