package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses;

public class RetriveEditProfileDetails {
    String name,emailid;
    public RetriveEditProfileDetails() {
    }

    public RetriveEditProfileDetails(String name, String emailid) {
        this.name = name;
        this.emailid = emailid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }
}
