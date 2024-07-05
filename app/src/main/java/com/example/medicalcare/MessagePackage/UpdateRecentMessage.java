package com.example.medicalcare.MessagePackage;

public class UpdateRecentMessage {
    String chatlistname,profileImage,message,mobNumber;

    public UpdateRecentMessage() {
    }

    public UpdateRecentMessage(String name, String profileImage, String message,String mobNumber) {
        this.chatlistname = name;
        this.profileImage = profileImage;
        this.message = message;
        this.mobNumber=mobNumber;
    }

    public String getChatlistname() {
        return chatlistname;
    }

    public void setChatlistname(String chatlistname) {
        this.chatlistname = chatlistname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobNumber() {
        return mobNumber;
    }

    public void setMobNumber(String mobNumber) {
        this.mobNumber = mobNumber;
    }
}
