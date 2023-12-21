package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Comments implements Serializable {

    //COMMENT DETAILS
    private String cd;  //comment id
    private String parentID;
    private Person person;
    private String publisherID;
    private boolean anonymous;
    private String option;
    private String topic;
    private String reason;
    private String recommendation;
    private String status;
    private long timestamp;  //comment time
    private int type;
    private boolean reported;
    private Demographic demographic;
    private ArrayList<String> tags;

    public Comments() {
    }

    public Comments(String cd, String parentID, Person person, String publisherID, boolean anonymous, String option, String topic, String reason, String recommendation, String status, long timestamp, int type, boolean reported, Demographic demographic, ArrayList<String> tags) {
        this.cd = cd;
        this.parentID = parentID;
        this.person = person;
        this.publisherID = publisherID;
        this.anonymous = anonymous;
        this.option = option;
        this.topic = topic;
        this.reason = reason;
        this.recommendation = recommendation;
        this.status = status;
        this.timestamp = timestamp;
        this.type = type;
        this.reported = reported;
        this.demographic = demographic;
        this.tags = tags;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public Demographic getDemographic() {
        return demographic;
    }

    public void setDemographic(Demographic demographic) {
        this.demographic = demographic;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Comments comment = (Comments)obj;
        return cd.matches(comment.getCd());
    }
}
