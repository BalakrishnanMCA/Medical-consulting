package com.example.medicalcare.UserModule.BookingAppointment;

public class DoctorPaymentReceivedClass {
    String patientName,receivedDate,specification,paymentAmount;
    public DoctorPaymentReceivedClass() {
    }

    public DoctorPaymentReceivedClass(String patientName, String receivedDate, String specification, String paymentAmount) {
        this.patientName = patientName;
        this.receivedDate = receivedDate;
        this.specification = specification;
        this.paymentAmount = paymentAmount;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
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
