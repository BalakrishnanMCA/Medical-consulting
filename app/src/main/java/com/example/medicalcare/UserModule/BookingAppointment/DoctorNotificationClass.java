package com.example.medicalcare.UserModule.BookingAppointment;

public class DoctorNotificationClass {
    String specification,patientName,sentDate,appointmentDate;
    public DoctorNotificationClass() {
    }

    public DoctorNotificationClass(String specification, String patientName, String sentDate,String appointmentDate) {
        this.specification = specification;
        this.patientName = patientName;
        this.sentDate = sentDate;
        this.appointmentDate=appointmentDate;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
