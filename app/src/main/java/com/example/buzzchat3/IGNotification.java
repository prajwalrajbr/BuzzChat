package com.example.buzzchat3;

import java.time.LocalDateTime;

public class IGNotification {
    private String userid;
    private String text;
    private String postid;
    private String time;

    public IGNotification(String userid, String text, String postid) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.time = time;
    }

    public IGNotification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
