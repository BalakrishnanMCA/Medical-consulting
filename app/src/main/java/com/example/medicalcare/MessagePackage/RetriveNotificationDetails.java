package com.example.medicalcare.MessagePackage;

public class RetriveNotificationDetails {
    String name,mobilenumber,fcmToken;

    public RetriveNotificationDetails() {

    }

    public RetriveNotificationDetails(String name, String mobilenumber, String fcmToken) {
        this.name = name;
        this.mobilenumber = mobilenumber;
        this.fcmToken = fcmToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
