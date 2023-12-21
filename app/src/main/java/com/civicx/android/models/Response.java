package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Response implements Serializable {

    //COMMENT DETAILS
    private String cd;
    private String publisherID;
    private String tokenID;
    private String summary;
    private long timestamp;
    private boolean reported;
    private ArrayList<String> tags;
    private ArrayList<String> comments;
    private ArrayList<String> upvotes;
    private ArrayList<String> downvotes;

    public Response() {
    }

    public Response(String cd, String publisherID, String tokenID, String summary, long timestamp, boolean reported, ArrayList<String> tags, ArrayList<String> comments, ArrayList<String> upvotes, ArrayList<String> downvotes) {
        this.cd = cd;
        this.publisherID = publisherID;
        this.tokenID = tokenID;
        this.summary = summary;
        this.timestamp = timestamp;
        this.reported = reported;
        this.tags = tags;
        this.comments = comments;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(ArrayList<String> upvotes) {
        this.upvotes = upvotes;
    }

    public ArrayList<String> getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(ArrayList<String> downvotes) {
        this.downvotes = downvotes;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Response response = (Response)obj;
        return cd.matches(response.getCd());
    }
}
