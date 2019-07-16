package com.example.footprint.model;

public class Token {
    private User user;



    private Token(){

    }

    private static class TokenHolder{
        public static final Token INSTANCE = new Token();
    }

    public static Token getTokenObject(){
        return TokenHolder.INSTANCE;
    }

    public User getUser(){
        return user;
    }

    public User setUser(){
        return user;
    }
}
