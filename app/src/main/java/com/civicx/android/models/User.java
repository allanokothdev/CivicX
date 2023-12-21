package com.civicx.android.models;

import java.io.Serializable;

public class User implements Serializable {

    private String id;  //ID
    private String pic; //PIC
    private String name; //NAME
    private String email; //EMAIL
    private String age;
    private boolean disability;
    private String ward; //EMAIL
    private String subCounty; //EMAIL
    private String constituency;
    private String county; //COUNTY
    private String gender; //GENDER
    private String country; //country
    private String token;
    private int stars;
    private boolean verification;
    private boolean reported;

    public User() {
    }

    public User(String id, String pic, String name, String email, String age, boolean disability, String ward, String subCounty, String constituency, String county, String gender, String country, String token, int stars, boolean verification, boolean reported) {
        this.id = id;
        this.pic = pic;
        this.name = name;
        this.email = email;
        this.age = age;
        this.disability = disability;
        this.ward = ward;
        this.subCounty = subCounty;
        this.constituency = constituency;
        this.county = county;
        this.gender = gender;
        this.country = country;
        this.token = token;
        this.stars = stars;
        this.verification = verification;
        this.reported = reported;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        User user = (User) obj;
        return id.matches(user.getId());
    }
}
