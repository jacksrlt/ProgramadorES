package com.ja.programadores.Constructors;

public class Post {

    String useruid, message, image;

    public Post() {
    }

    public Post(String useruid, String message, String image) {
        this.useruid = useruid;
        this.message = message;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}