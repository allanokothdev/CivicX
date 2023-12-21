package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {

    private String id;
    private String conversationID;
    private String topic;
    private String title;
    private String summary;
    private String link;
    private String objectType;
    private String timestamp;
    private Long date;
    private Long publishedDate;
    private Brand brand;        // REMOVE
    private Person person;
    private String house;
    private String country;
    private String category;
    private int currentCost;
    private int targetCost;
    private ArrayList<String> photos;
    private ArrayList<String> sections;
    private ArrayList<String> tags;
    private ArrayList<String> bookmarks;

    public Post() {
    }

    public Post(String id, String conversationID, String topic, String title, String summary, String link, String objectType, String timestamp, Long date, Long publishedDate, Brand brand, Person person, String house, String country, String category, int currentCost, int targetCost, ArrayList<String> photos, ArrayList<String> sections, ArrayList<String> tags, ArrayList<String> bookmarks) {
        this.id = id;
        this.conversationID = conversationID;
        this.topic = topic;
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.objectType = objectType;
        this.timestamp = timestamp;
        this.date = date;
        this.publishedDate = publishedDate;
        this.brand = brand;
        this.person = person;
        this.house = house;
        this.country = country;
        this.category = category;
        this.currentCost = currentCost;
        this.targetCost = targetCost;
        this.photos = photos;
        this.sections = sections;
        this.tags = tags;
        this.bookmarks = bookmarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Long publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(int currentCost) {
        this.currentCost = currentCost;
    }

    public int getTargetCost() {
        return targetCost;
    }

    public void setTargetCost(int targetCost) {
        this.targetCost = targetCost;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getSections() {
        return sections;
    }

    public void setSections(ArrayList<String> sections) {
        this.sections = sections;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(ArrayList<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Post post = (Post) obj;
        return id.matches(post.getId());
    }
}
