package com.example.footprint.model;

public class PostList {
    private String postId;
    private String title;
    private String author;
    private String pictureId;
    private String like;
    private String date;
    private String type;


    public PostList(){

    }
    public PostList(String postId,String title,String author,String pictureId,String like,String date,String type){
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.pictureId = pictureId;
        this.like = like;
        this.date = date;
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
