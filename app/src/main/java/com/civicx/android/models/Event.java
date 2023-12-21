package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private String id;  //ID
    private String publisher;
    private Brand brand;
    private String pic;  //PIC
    private String title;  //TITLE
    private String summary;  //SUMMARY
    private String location;  //LOCATION
    private ArrayList<String> county;
    private String category;
    private String lick;  //LINK
    private long date;    //EVENT DATE
    private long time;    //EVENT TIME
    private ArrayList<String> country;
    private ArrayList<String> tags;

    public Event() {
    }

    public Event(String id, String publisher, Brand brand, String pic, String title, String summary, String location, ArrayList<String> county, String category, String lick, long date, long time, ArrayList<String> country, ArrayList<String> tags) {
        this.id = id;
        this.publisher = publisher;
        this.brand = brand;
        this.pic = pic;
        this.title = title;
        this.summary = summary;
        this.location = location;
        this.county = county;
        this.category = category;
        this.lick = lick;
        this.date = date;
        this.time = time;
        this.country = country;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getCounty() {
        return county;
    }

    public void setCounty(ArrayList<String> county) {
        this.county = county;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLick() {
        return lick;
    }

    public void setLick(String lick) {
        this.lick = lick;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<String> getCountry() {
        return country;
    }

    public void setCountry(ArrayList<String> country) {
        this.country = country;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Event event = (Event) obj;
        return id.matches(event.getId());
    }
}
