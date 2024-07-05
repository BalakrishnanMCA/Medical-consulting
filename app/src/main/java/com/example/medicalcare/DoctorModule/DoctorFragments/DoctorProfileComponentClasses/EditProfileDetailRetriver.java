package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses;

public class EditProfileDetailRetriver {

    String name,experience,email,description;
    public EditProfileDetailRetriver() {
    }

    public EditProfileDetailRetriver(String name, String experience, String email, String description) {
        this.name = name;
        this.experience = experience;
        this.email = email;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
