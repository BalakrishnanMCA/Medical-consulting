package com.example.medicalcare.UserModule.BookingAppointment;

public class PatientPaymentHistoryClass {
    String doctorName,paymentDate,specification,paymentAmount;
    public PatientPaymentHistoryClass() {
    }

    public PatientPaymentHistoryClass(String doctorName, String paymentDate, String specification, String paymentAmount) {
        this.doctorName = doctorName;
        this.paymentDate = paymentDate;
        this.specification = specification;
        this.paymentAmount = paymentAmount;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
