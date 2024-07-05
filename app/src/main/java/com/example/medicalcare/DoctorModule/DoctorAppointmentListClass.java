package com.example.medicalcare.DoctorModule;

public class DoctorAppointmentListClass {
    String Username,UserMobile,Specialization,profileimage,dateandtime,description;

    public DoctorAppointmentListClass() {
    }

    public DoctorAppointmentListClass(String username, String userMobile, String specialization, String profileimage, String dateandtime, String description) {
        Username = username;
        UserMobile = userMobile;
        Specialization = specialization;
        this.profileimage = profileimage;
        this.dateandtime = dateandtime;
        this.description = description;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        Specialization = specialization;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getDateandtime() {
        return dateandtime;
    }

    public void setDateandtime(String dateandtime) {
        this.dateandtime = dateandtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
