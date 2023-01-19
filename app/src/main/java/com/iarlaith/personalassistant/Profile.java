package com.iarlaith.personalassistant;

public class Profile {

    private String userID;
    private String email;
    private String name;

    public Profile(String userID, String email, String name) {
        this.userID = userID;
        this.email = email;
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
