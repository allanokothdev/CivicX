package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Brand implements Serializable {
    private String id;
    private String pic;
    private String title;
    private String summary;
    private String type;
    private String category;
    private String country;
    private String publisher;
    private String token;
    private ArrayList<String> tags;

    public Brand() {
    }

    public Brand(String id, String pic, String title, String summary, String type, String category, String country, String publisher, String token, ArrayList<String> tags) {
        this.id = id;
        this.pic = pic;
        this.title = title;
        this.summary = summary;
        this.type = type;
        this.category = category;
        this.country = country;
        this.publisher = publisher;
        this.token = token;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Brand brand = (Brand) obj;
        return id.matches(brand.getId());
    }
}
