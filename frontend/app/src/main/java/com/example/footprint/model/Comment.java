package com.example.footprint.model;

public class Comment {
    private String content;
    private String nickname;
    private String commentId;
    private String date;
    private String like;
    private String likeFlag;

    public Comment(){
    }

    public Comment(String content, String nickname,String date, String love){
        this.content = content;
        this.nickname = nickname;
        this.date = date;
        this.like = love;
    }

    public String getMaintext() {
        return content;
    }

    public void setMaintext(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(String likeFlag) {
        this.likeFlag = likeFlag;
    }



}
