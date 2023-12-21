package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Survey implements Serializable {
    private String id;
    private String parentID;
    private String title;
    private String summary;
    private String type;
    private long timestamp;
    private ArrayList<String> questions;
    private ArrayList<String> participants;
    private ArrayList<String> tags;

    public Survey(){ }

    public Survey(String id, String parentID, String title, String summary, String type, long timestamp, ArrayList<String> questions, ArrayList<String> participants, ArrayList<String> tags) {
        this.id = id;
        this.parentID = parentID;
        this.title = title;
        this.summary = summary;
        this.type = type;
        this.timestamp = timestamp;
        this.questions = questions;
        this.participants = participants;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Survey survey = (Survey) obj;
        return id.matches(survey.getId());
    }
}
