package com.example.medicalcare.MessagePackage;

public class PhotoLoaderClass {
    String receiverNumber,profileImage;

    public PhotoLoaderClass() {
    }

    public PhotoLoaderClass(String receiverNumber, String profileImage) {
        this.receiverNumber = receiverNumber;
        this.profileImage = profileImage;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
