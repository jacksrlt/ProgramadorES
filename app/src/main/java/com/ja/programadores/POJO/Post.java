package com.ja.programadores.POJO;

import com.google.firebase.database.ServerValue;

public class Post {

    private String postkey;
    private String title;
    private String content;
    private String image;
    private String name;
    private String avatar;
    private String posteruid;
    private Object timestamp;
    private int likes;

    public Post(String title, String content, String image, String name, String avatar, int likes) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.name = name;
        this.avatar = avatar;
        this.likes = likes;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Post() {
    }

    public String getPosteruid() {return posteruid;}

    public void setPosteruid(String posteruid) {this.posteruid = posteruid;}

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getAvatar() {return avatar;}

    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getPostkey() {return postkey;}

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}