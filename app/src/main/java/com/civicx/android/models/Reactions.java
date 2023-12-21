package com.civicx.android.models;

import java.io.Serializable;

public class Reactions implements Serializable {

    private String id;
    private int shares;
    private int upvotes;
    private int downvotes;
    private int likes;
    private int comments;
    private int downloads;
    private int views;

    public Reactions() {
    }

    public Reactions(String id, int shares, int upvotes, int downvotes, int likes, int comments, int downloads, int views) {
        this.id = id;
        this.shares = shares;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.likes = likes;
        this.comments = comments;
        this.downloads = downloads;
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
