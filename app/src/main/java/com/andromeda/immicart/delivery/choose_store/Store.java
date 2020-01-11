package com.andromeda.immicart.delivery.choose_store;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "store")
public class Store implements Serializable {

    @PrimaryKey
    @NonNull
    private String key;
    private String name;
    private String logoUrl;
    private String county;
    private String address;
    private String latlng;
    private int number_of_reviews;
    private float avg_rating;
    private boolean isCurrent;
    @Ignore
    private  boolean selected = false;

    public Store(@NonNull String key, String name, String logoUrl, String county, String address, String latlng, int number_of_reviews, float avg_rating, boolean isCurrent) {
        this.key = key;
        this.name = name;
        this.logoUrl = logoUrl;
        this.county = county;
        this.address = address;
        this.latlng = latlng;
        this.number_of_reviews = number_of_reviews;
        this.avg_rating = avg_rating;
        this.isCurrent = isCurrent;
    }

    @Ignore
    public Store( String name, String logoUrl, String county, String address, String latlng, int number_of_reviews, float avg_rating) {
        this.key = key;
        this.name = name;
        this.logoUrl = logoUrl;
        this.county = county;
        this.address = address;
        this.latlng = latlng;
        this.number_of_reviews = number_of_reviews;
        this.avg_rating = avg_rating;
        this.isCurrent = isCurrent;
    }

    @Ignore
    public boolean isSelected() {
        return selected;
    }
    @Ignore
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public int getNumber_of_reviews() {
        return number_of_reviews;
    }

    public void setNumber_of_reviews(int number_of_reviews) {
        this.number_of_reviews = number_of_reviews;
    }

    public float getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(float avg_rating) {
        this.avg_rating = avg_rating;
    }

    @Ignore
    public Store() {}

}

