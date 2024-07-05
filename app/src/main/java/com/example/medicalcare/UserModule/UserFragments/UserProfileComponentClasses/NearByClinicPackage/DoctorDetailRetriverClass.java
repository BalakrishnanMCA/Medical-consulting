package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.NearByClinicPackage;

public class DoctorDetailRetriverClass {
    String name,doctorSpecialization,experience,latitude,longitude,profileImage;

    public DoctorDetailRetriverClass() {
    }

    public DoctorDetailRetriverClass(String name, String doctorSpecialization, String experience, String latitude, String longitude, String profileImage) {
        this.name = name;
        this.doctorSpecialization = doctorSpecialization;
        this.experience = experience;
        this.latitude = latitude;
        this.longitude = longitude;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
