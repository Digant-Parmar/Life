package com.example.life.Model;

public class User {

    private String uid;
    private String username;
    private String status;
            //imageURL;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User(String uid, String username, String status){
        this.uid = uid;
        this.username = username;
        this.status = status;
        //this.imageURL = imageURL;
    }
    public User(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//
//    public String getImageURL() {
//        return imageURL;
//    }
//
//    public void setImageURL(String imageURL) {
//        this.imageURL = imageURL;
//    }


}
