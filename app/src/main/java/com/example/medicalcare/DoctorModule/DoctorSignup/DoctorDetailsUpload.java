package com.example.medicalcare.DoctorModule.DoctorSignup;

public class DoctorDetailsUpload {
    String Name,Email,MobileNumber,DoctorLicense,DoctorSpecialization,Password,Experience,profileImage;


    public DoctorDetailsUpload() {
    }

    public DoctorDetailsUpload(String name, String email, String mobileNumber, String doctorLicense, String doctorSpecialization, String password, String experience,String profileImage) {
        Name = name;
        Email = email;
        MobileNumber = mobileNumber;
        DoctorLicense = doctorLicense;
        DoctorSpecialization = doctorSpecialization;
        Password = password;
        Experience=experience;
        profileImage=profileImage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getDoctorLicense() {
        return DoctorLicense;
    }

    public void setDoctorLicense(String doctorLicense) {
        DoctorLicense = doctorLicense;
    }

    public String getDoctorSpecialization() {
        return DoctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        DoctorSpecialization = doctorSpecialization;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
