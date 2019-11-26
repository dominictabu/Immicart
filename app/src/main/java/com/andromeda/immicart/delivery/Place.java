package com.andromeda.immicart.delivery;


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

    public Place(String placeID, String name, String address) {
        this.placeID = placeID;
        this.name = name;
        this.address = address;
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

