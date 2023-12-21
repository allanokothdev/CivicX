package com.civicx.android.models;

import java.io.Serializable;

public class Reply implements Serializable {

    private String cd; //ID
    private String postID; //BILL
    private String commentID; //COMMENTS
    private String publisher; //
    private long date;   //date

    public Reply() {
    }

    public Reply(String cd, String postID, String commentID, String publisher, long date) {
        this.cd = cd;
        this.postID = postID;
        this.commentID = commentID;
        this.publisher = publisher;
        this.date = date;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Reply reply = (Reply) obj;
        return cd.matches(reply.getCd());
    }
}
