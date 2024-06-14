package com.server;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.server.bean.Message;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface RoomService {

    @WebMethod
    public boolean subscribe(String pseudo);

    @WebMethod
    public boolean unsubscribe(String pseudo);

    @WebMethod
    public void postMsg(String pseudo, String message);

    @WebMethod
    public String getMessageUser(String pseudo);

}
