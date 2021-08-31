package com.example.buzzchat3;

public class Comment {
    private String comment, publisher, commentid, commentNotificationid;

    public String getCommentNotificationid() {
        return commentNotificationid;
    }

    public void setCommentNotificationid(String commentNotificationid) {
        this.commentNotificationid = commentNotificationid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Comment() {
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public Comment(String comment, String publisher, String commentid, String commentNotificationid) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
        this.commentNotificationid = commentNotificationid;
    }
}
