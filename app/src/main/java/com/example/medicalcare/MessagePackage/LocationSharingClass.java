package com.example.medicalcare.MessagePackage;

public class LocationSharingClass {
    String location,receiverNumber;
    public LocationSharingClass() {
    }

    public LocationSharingClass(String location, String receiverNumber) {
        this.location = location;
        this.receiverNumber = receiverNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }
}
