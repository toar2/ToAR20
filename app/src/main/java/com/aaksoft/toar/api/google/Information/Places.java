package com.aaksoft.toar.api.google.Information;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
    Used to store places information to be passed between fragments
 */

public class Places implements Serializable{

    private double lat;
    private double lng;
    private String placeId;
    private String iconUrl;
    private String addr;
    private String phoneNumber;
    private String name;
    private double rating;
    private String vicinity;
    private String reference;
    private boolean isOpen;
    private boolean isPriceDefined;
    private double price;
    private String hours;
    private List<String> type= new ArrayList<>();

    public Places() {

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isPriceDefined() {
        return isPriceDefined;
    }

    public void setPriceDefined(boolean priceDefined) {
        isPriceDefined = priceDefined;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceTypeString(){
        int size = type.size();
        String typeString="";
        for (int i=0; i< size; i++){
            typeString = typeString + " | " + this.type.get(i);
        }
        return typeString;
    }
}
