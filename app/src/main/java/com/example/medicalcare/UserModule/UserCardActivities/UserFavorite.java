package com.example.medicalcare.UserModule.UserCardActivities;

public class UserFavorite {

    String DocMobile,DocSpecification;

    public UserFavorite() {
    }

    public UserFavorite(String docMobile, String specialization) {
        DocMobile = docMobile;
        DocSpecification = specialization;
    }

    public String getDocMobile() {
        return DocMobile;
    }

    public void setDocMobile(String docMobile) {
        DocMobile = docMobile;
    }

    public String getDocSpecification() {
        return DocSpecification;
    }

    public void setDocSpecification(String docSpecification) {
        DocSpecification = docSpecification;
    }
}
