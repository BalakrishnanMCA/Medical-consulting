package com.example.medicalcare.MessagePackage;

public class MessageRetriverClass {
    String receiverNumber,message,profileImage,location;

    public MessageRetriverClass() {

    }

    public MessageRetriverClass(String receiverNumber, String message, String profileImage,String location) {
        this.receiverNumber = receiverNumber;
        this.message = message;
        this.profileImage = profileImage;
        this.location=location;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
