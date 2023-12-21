package com.civicx.android.models;

import java.io.Serializable;

public class Notification implements Serializable {

    private String id;
    private String image;
    private String title;
    private String message;
    private String objectID;
    private String objectType;
    private String token;

    public Notification(){ }

    public Notification(String id, String image, String title, String message, String objectID, String objectType, String token) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.message = message;
        this.objectID = objectID;
        this.objectType = objectType;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Notification notification = (Notification)obj;
        return id.matches(notification.getId());
    }
}
