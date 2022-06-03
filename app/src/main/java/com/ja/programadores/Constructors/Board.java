package com.ja.programadores.Constructors;

import com.google.firebase.database.ServerValue;

public class Board {

    private String postKey;
    private String title;
    private String content;
    private String name;
    private String avatar;
    private String location;
    private Object timestamp;

    public Board(String title, String content, String location, String name, String avatar) {
        this.title = title;
        this.content = content;
        this.name = name;
        this.location = location;
        this.avatar = avatar;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Board() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatar() {return avatar;}

    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}