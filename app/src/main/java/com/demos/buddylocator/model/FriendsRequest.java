package com.demos.buddylocator.model;

/**
 * Created by Demos on 11/27/2017.
 */

public class FriendsRequest {

    String Sender;
    String Reciever;

    public FriendsRequest() {
        this.Sender = "";
        this.Reciever = "";

    }


    public FriendsRequest(String Sender,String Reciever) {
        this.Sender = Sender;
        this.Reciever = Reciever;

    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReciever() {
        return Reciever;
    }

    public void setReciever(String reciever) {
        Reciever = reciever;
    }
}
