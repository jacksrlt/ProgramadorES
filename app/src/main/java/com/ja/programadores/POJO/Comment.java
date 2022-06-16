package com.ja.programadores.POJO;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content, useruid, avatar, name, postkey;
    private Object timestamp;


    public Comment() {
    }

    public Comment(String content, String useruid, String avatar, String name, String postkey) {
        this.content = content;
        this.useruid = useruid;
        this.avatar = avatar;
        this.name = name;
        this.postkey = postkey;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public Comment(String content, String useruid, String avatar, String name, Object timestamp) {
        this.content = content;
        this.useruid = useruid;
        this.avatar = avatar;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}