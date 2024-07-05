package com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage;

public class ReviewClass {
    String date,name,review,rating;


    public ReviewClass(String name, String date, String review, String rating) {
        this.date = date;
        this.name = name;
        this.rating = rating;
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
