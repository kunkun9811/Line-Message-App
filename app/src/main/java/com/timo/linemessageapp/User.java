package com.timo.linemessageapp;

public class User {
    private String email;
    private String user_id;
    private String token;


    public User(String email, String user_id, String token) {
        this.email = email;
        this.user_id = user_id;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return user_id;
    }

    public String getToken() {
        return token;
    }
}
