package com.civicx.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {


    private String cd;
    private int level;
    private String cp;
    private String ct;
    private ArrayList<String> country;

    public Category(){ }

    public Category(String cd, int level, String cp, String ct, ArrayList<String> country) {
        this.cd = cd;
        this.level = level;
        this.cp = cp;
        this.ct = ct;
        this.country = country;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public ArrayList<String> getCountry() {
        return country;
    }

    public void setCountry(ArrayList<String> country) {
        this.country = country;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Category category = (Category) obj;
        return cd.matches(category.getCd());
    }
}
