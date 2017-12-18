package com.example.lkm.ms_termproject_001;

import android.graphics.Bitmap;

public class MyItem {

    private String icon;
    private String name;
    private String contents;
    private String distance;
    private String id;
    private String category="";
    private String longitude;
    private String latitude;
    private float rating;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCategory(){return category;}

    public void setCategory(String category){this.category=category;}

    public String getId(){return id;}

    public void setId(String id){this.id=id;}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getdistance() {
        return distance;
    }

    public void setdistance(String distance) {
        this.distance = distance;
    }

}