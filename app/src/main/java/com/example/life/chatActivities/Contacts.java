package com.example.life.chatActivities;

public class Contacts {

    public String username;
    public String status;
    public String profileimage;



    public Contacts(){

    }

    public Contacts(String username, String status, String profileimage) {
        this.username = username;
        this.status = status;
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

}
