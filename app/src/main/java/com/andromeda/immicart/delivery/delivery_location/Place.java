package com.andromeda.immicart.delivery.delivery_location;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "place")
public class Place {

    @NonNull
    @PrimaryKey
    private String placeID;
    private String name;
    private String address;
    private String placeFullText;

    public Place(String placeID, String name, String address, String placeFullText) {
        this.placeID = placeID;
        this.name = name;
        this.address = address;
        this.placeFullText = placeFullText;
    }

    public String getPlaceFullText() {
        return placeFullText;
    }

    public void setPlaceFullText(String placeFullText) {
        this.placeFullText = placeFullText;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

