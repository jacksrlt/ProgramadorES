package com.ja.programadores.POJO;

import com.google.firebase.database.ServerValue;

public class Direct{

    private String subject;
    private String message;
    private String image;
    private String name;
    private String avatar;
    private String directkey;
    private String senderuid;
    private String recuid;
    private Object timestamp;

    public Direct(String subject, String message, String image, String name, String avatar) {
        this.subject = subject;
        this.message = message;
        this.image = image;
        this.name = name;
        this.avatar = avatar;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Direct() {
    }

    public String getRecuid() {return recuid;}

    public void setRecuid(String recuid) {this.recuid = recuid;}

    public String getSenderuid() {return senderuid;}

    public void setSederuid(String senderuid) {this.senderuid = senderuid;}

    public String getDirectkey() {return directkey;}

    public void setDirectkey(String directkey) {this.directkey = directkey;}

    public String getAvatar() {return avatar;}

    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getSubject() {return subject;}

    public String getMessage() {
        return message;
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

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
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