package com.demos.buddylocator.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Demos on 11/3/2017.
 */

public class Users implements Serializable {

    String email;
    String name;
    Double latitude,longitude;
    boolean hidden;
    HashMap<String,String> friends;
    HashMap<String,String > addfriends;
    HashMap<String,String > friendsrequest;


    public Users() {
        this.name = "null";
        latitude=0.0;
        longitude=0.0;
        this.email = "null";
        hidden = false;
        friends = new HashMap<>();
        friendsrequest = new HashMap<>();
        addfriends = new HashMap<>();

    }

    public Users(String email, String name, double lat, double lng) {
        this.email = email;
        this.name = name;
        this.latitude = lat;
        this.longitude = lng;
        this.hidden=false;
        friends = new HashMap<>();
        friendsrequest = new HashMap<>();
        addfriends = new HashMap<>();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double lat) {
        this.latitude = lat;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double lng) {
        this.longitude = lng;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
    }


    public HashMap<String, String> getAddfriends() {
        return addfriends;
    }

    public void setAddfriends(HashMap<String, String> addfriends) {
        this.addfriends = addfriends;
    }

    public HashMap<String, String> getFriendsrequest() {
        return friendsrequest;
    }

    public void setFriendsrequest(HashMap<String, String> friendsrequest) {
        this.friendsrequest = friendsrequest;
    }

    public void updateFriendsRequest(String k,String s){
        this.friendsrequest.put(k,s);
    }

    public  void updateFriends(String k ,String s){
        this.friends.put(k,s);
    }
    public void updateAddFriends(String k ,String s){
        this.addfriends.put(k,s);
    }

}

