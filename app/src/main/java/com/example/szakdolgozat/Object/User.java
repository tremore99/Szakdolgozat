package com.example.szakdolgozat.Object;

public class User {
    private String email;
    private String password;
    private String username;
    private String userUID;

    public User(String email, String password, String username, String userUID) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userUID = userUID;
    }

    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getUsername() {return username;}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
