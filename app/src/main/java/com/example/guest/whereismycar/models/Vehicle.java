package com.example.guest.whereismycar.models;

/**
 * Created by Guest on 12/20/16.
 */
public class Vehicle {
    private String vehicleName;
    private String vehicleDescription;
    private String vehicleImage;
    private String coordinates;
    private String pushId;

    public Vehicle(String vehicleName, String vehicleDescription, String vehicleImage, String coordinates) {
        this.vehicleName = vehicleName;
        this.vehicleDescription = vehicleDescription;
        this.vehicleImage = "not_specified";
        this.coordinates = "not_specified";
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
}
