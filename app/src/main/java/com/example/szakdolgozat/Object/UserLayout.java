package com.example.szakdolgozat.Object;

public class UserLayout {
    private String email;
    private String password;
    private String username;

    public UserLayout(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
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
}