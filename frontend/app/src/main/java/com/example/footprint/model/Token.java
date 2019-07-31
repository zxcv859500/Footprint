package com.example.footprint.model;

public class Token {


    private String TokenKey;
    private User user;

    private Token(){
        TokenKey = null;
    }

    private static class TokenHolder{
        public static final Token INSTANCE = new Token();
    }

    public static Token getTokenObject(){
        return TokenHolder.INSTANCE;
    }

    public void setTokenKey(String tokenKey) {
        TokenKey = tokenKey;
    }

    public String getTokenKey() {
        return TokenKey;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
