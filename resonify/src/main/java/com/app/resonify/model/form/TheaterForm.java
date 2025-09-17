package com.app.resonify.model.form;

import java.util.UUID;

public class TheaterForm {

    private String name;
    private String address;
    private Double lat;
    private Double lng;

    private UUID cityId;

    // Photo handling
    private String photoAction;
    private String photoUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public UUID getCityId() {
        return cityId;
    }

    public void setCityId(UUID cityId) {
        this.cityId = cityId;
    }

    public String getPhotoAction() {
        return photoAction;
    }

    public void setPhotoAction(String photoAction) {
        this.photoAction = photoAction;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
