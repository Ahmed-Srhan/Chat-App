package com.example.chatapp.model;

public class UserModel {

    String userId, image, username, status;


    public UserModel() {
    }


    public UserModel(String userId, String image, String username, String status) {
        this.userId = userId;
        this.image = image;
        this.username = username;
        this.status = status;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
