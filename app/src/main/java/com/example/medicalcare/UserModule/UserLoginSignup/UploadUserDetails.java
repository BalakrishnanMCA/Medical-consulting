package com.example.medicalcare.UserModule.UserLoginSignup;

public class UploadUserDetails {
    String name,mobilenumber,emailid,userpassword;


    public UploadUserDetails(String name,String mobilenumber,String emailid,String userpassword) {
        this.name=name;
        this.mobilenumber=mobilenumber;
        this.emailid=emailid;
        this.userpassword=userpassword;

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

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }
}
