package com.aaksoft.toar.localdb;

/*
    Created By Aasharib
    on
    23 February, 2019
 */

/*
    This class holds individual image data for storage in local db
*/

public class ImageInformation {

    private int id;
    private String name;
    private double lat;
    private double lng;
    private String description;
    private String createdOn;

    // Image Status key:
    // 0: Not saved;
    // 1: Saved Locally & Not sync
    // 2: Saved Locally & synced
    // 3: downloaded self owned image
    // 4: downloaded not self owned image
    private int imageStatus;
    private String ownerName;
    private String owner_id;
    private String uniqueFileName;

    public ImageInformation() {

    }

    public ImageInformation(String name, float lat, float lng, String description, String createdOn, String ownerName, String ownerId, int imageStatus) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
        this.createdOn = createdOn;
        this.ownerName = ownerName;
        this.owner_id= ownerId;
        this.imageStatus = imageStatus;
    }
    public int getId(){return id;}

    public void setId(int id){this.id=id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwner_id(){ return owner_id;}

    public void setOwner_id(String ownerId){this.owner_id=ownerId;}

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getUniqueFileName() {
        return uniqueFileName;
    }

    public void setUniqueFileName(String uniqueFileName) {
        this.uniqueFileName = uniqueFileName;
    }
}
