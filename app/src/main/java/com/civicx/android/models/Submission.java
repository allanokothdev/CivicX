package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Submission implements Serializable {

    private String id;
    private boolean anonymous;
    private Person person;
    private String question;
    private String answer;
    private long timestamp;
    private Demographic demographic;
    private ArrayList<String> tags;

    public Submission() {
    }

    public Submission(String id, boolean anonymous, Person person, String question, String answer, long timestamp, Demographic demographic, ArrayList<String> tags) {
        this.id = id;
        this.anonymous = anonymous;
        this.person = person;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
        this.demographic = demographic;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
        Submission submission = (Submission) obj;
        return id.matches(submission.getId());
    }
}
