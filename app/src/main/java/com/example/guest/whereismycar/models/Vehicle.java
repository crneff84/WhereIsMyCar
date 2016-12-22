package com.example.guest.whereismycar.models;

import org.parceler.Parcel;


@Parcel
public class Vehicle {
    String vehicleName;
    String vehicleDescription;
    String vehicleImage;
    String coordinates;
    String latitude;
    String longitude;
    String index;
    private String pushId;

    public Vehicle(String vehicleName, String vehicleDescription, String vehicleImage, String coordinates, String latitude, String longitude) {
        this.vehicleName = vehicleName;
        this.vehicleDescription = vehicleDescription;
        this.vehicleImage = vehicleImage;
        this.coordinates = coordinates;
        this.latitude = latitude;
        this.longitude = longitude;
        this.index = "not_specified";
    }

    public Vehicle() {}

    public String getVehicleName() {
        return vehicleName;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
