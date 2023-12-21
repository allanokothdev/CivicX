package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Location implements Serializable {

    private String id;
    private String address;
    private double latitude;
    private double longitude;
    private String ward;
    private String subCounty;
    private String county;
    private String country;
    private ArrayList<String> tags;

    public Location() {
    }

    public Location(String id, String address, double latitude, double longitude, String ward, String subCounty, String county, String country, ArrayList<String> tags) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ward = ward;
        this.subCounty = subCounty;
        this.county = county;
        this.country = country;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(String subCounty) {
        this.subCounty = subCounty;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
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
        Location location = (Location) obj;
        return id.matches(location.getId());
    }
}
