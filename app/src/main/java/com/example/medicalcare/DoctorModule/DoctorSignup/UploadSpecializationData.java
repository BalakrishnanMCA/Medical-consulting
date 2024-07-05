package com.example.medicalcare.DoctorModule.DoctorSignup;

public class UploadSpecializationData {
    String name,specialization,rating,profileImage,experience,mobileNumber;
    String amount;

    public UploadSpecializationData() {
    }

    public UploadSpecializationData(String name, String specialization, String rating, String profileImage, String amount,String experience,String mobileNumber) {
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
        this.profileImage = profileImage;
        this.amount = amount;
        this.experience=experience;
        this.mobileNumber= mobileNumber;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
