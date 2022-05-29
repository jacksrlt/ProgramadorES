package com.ja.programadores.Constructors;

import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey;
    private String title;
    private String content;
    private String picture;
    private String userUid;
    private Object timeStamp;

    public Post(String title, String content, String picture, String userUid) {
        this.title = title;
        this.content = content;
        this.picture = picture;
        this.userUid = userUid;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post() {
    }


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

    public String getPicture() {
        return picture;
    }

    public String getUserUid() {
        return userUid;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}