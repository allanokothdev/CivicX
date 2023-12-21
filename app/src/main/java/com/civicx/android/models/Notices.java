package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Notices implements Serializable {

    private String id;
    private Brand brand;
    private String publisher;
    private String pic;
    private String title;
    private String summary;
    private String link;
    private long date;
    private ArrayList<String> county;
    private ArrayList<String> tags;

    public Notices() {
    }

    public Notices(String id, Brand brand, String publisher, String pic, String title, String summary, String link, long date, ArrayList<String> county, ArrayList<String> tags) {
        this.id = id;
        this.brand = brand;
        this.publisher = publisher;
        this.pic = pic;
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.date = date;
        this.county = county;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getCounty() {
        return county;
    }

    public void setCounty(ArrayList<String> county) {
        this.county = county;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Notices notices = (Notices)obj;
        return id.matches(notices.getId());
    }
}
