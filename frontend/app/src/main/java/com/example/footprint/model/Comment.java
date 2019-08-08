package com.example.footprint.model;

public class Comment {
    private String maintext;
    private String nickname;
    private String date;
    private String love;

    public Comment(){
    }

    public Comment(String maintext, String nickname,String date, String love){
        this.maintext = maintext;
        this.nickname = nickname;
        this.date = date;
        this.love = love;
    }

    public String getMaintext() {
        return maintext;
    }

    public void setMaintext(String maintext) {
        this.maintext = maintext;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }



}
