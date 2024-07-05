package com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage;

import java.io.Serializable;
import java.util.ArrayList;

public class UploadPriscriptionData implements Serializable {
    String doctorName,specialization,Date,patientName,patientAge,patientGender;
    ArrayList<MedicineList> Medicine;

    public UploadPriscriptionData() {
    }

    public UploadPriscriptionData(String doctorName, String specialization, String date, String patientName, String patientAge, String patientGender, ArrayList<MedicineList> medicine) {
        this.doctorName = doctorName;
        this.specialization = specialization;
        Date = date;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        Medicine = medicine;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public ArrayList<MedicineList> getMedicine() {
        return Medicine;
    }

    public void setMedicine(ArrayList<MedicineList> medicine) {
        Medicine = medicine;
    }
}

