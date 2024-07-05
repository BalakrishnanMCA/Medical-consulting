package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses;

public class UploadLocation {

    String mobile,latitude,longitude,name,address;
    public UploadLocation() {
    }

    public UploadLocation(String mobile, String latitude, String longitude,String name,String address) {
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name=name;
        this.address=address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
