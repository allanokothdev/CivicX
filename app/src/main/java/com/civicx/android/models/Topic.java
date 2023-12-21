package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Topic implements Serializable {

    private String id; //ID
    private String title;
    private String summary;
    private int posts;
    private ArrayList<String> tags;

    public Topic() {
    }

    public Topic(String id, String title, String summary, int posts, ArrayList<String> tags) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.posts = posts;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Topic topic = (Topic) obj;
        return id.matches(topic.getId());
    }
}
