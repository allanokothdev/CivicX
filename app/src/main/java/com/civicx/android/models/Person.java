package com.civicx.android.models;

import java.io.Serializable;

public class Person implements Serializable {

    private String id;  //ID
    private String pic;
    private String title;
    private String subTitle;
    private String token;

    public Person() {
    }

    public Person(String id, String pic, String title, String subTitle, String token) {
        this.id = id;
        this.pic = pic;
        this.title = title;
        this.subTitle = subTitle;
        this.token = token;
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj){
        Person person = (Person) obj;
        return id.matches(person.getId());
    }
}
