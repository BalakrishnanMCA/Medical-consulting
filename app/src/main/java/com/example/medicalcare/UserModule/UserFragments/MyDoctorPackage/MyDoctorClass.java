package com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage;

public class MyDoctorClass {
    String Doctorname,Doctormobile,Specialization,profileimage,dateandtime;

    public MyDoctorClass() {

    }

    public MyDoctorClass(String doctorname, String doctormobile, String specialization, String profileimage, String dateandtime) {
        Doctorname = doctorname;
        Doctormobile = doctormobile;
        Specialization = specialization;
        this.profileimage = profileimage;
        this.dateandtime = dateandtime;
    }

    public String getDoctorname() {
        return Doctorname;
    }

    public void setDoctorname(String doctorname) {
        Doctorname = doctorname;
    }

    public String getDoctormobile() {
        return Doctormobile;
    }

    public void setDoctormobile(String doctormobile) {
        Doctormobile = doctormobile;
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
}
