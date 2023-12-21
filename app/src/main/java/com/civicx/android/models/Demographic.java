package com.civicx.android.models;

import java.io.Serializable;

public class Demographic implements Serializable {
    private String id;
    private String age;
    private String gender;
    private boolean disability;
    private String ward;
    private String subCounty;
    private String county;
    private String constituency;
    private String country;

    public Demographic() {
    }

    public Demographic(String id, String age, boolean disability, String ward, String subCounty, String constituency, String county, String gender, String country) {
        this.id = id;
        this.age = age;
        this.disability = disability;
        this.ward = ward;
        this.subCounty = subCounty;
        this.constituency = constituency;
        this.county = county;
        this.gender = gender;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public boolean isDisability() {
        return disability;
    }

    public void setDisability(boolean disability) {
        this.disability = disability;
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

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Demographic user = (Demographic) obj;
        return id.matches(user.getId());
    }
}
