package com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage;

import java.io.Serializable;

public class MedicineList implements Serializable {
    String name;
    boolean morning,afternoon,night;
    String days;
    public MedicineList() {
    }

    public MedicineList(String name, boolean morning, boolean afternoon, boolean night, String days) {
        this.name = name;
        this.morning = morning;
        this.afternoon = afternoon;
        this.night = night;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isAfternoon() {
        return afternoon;
    }

    public void setAfternoon(boolean afternoon) {
        this.afternoon = afternoon;
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
