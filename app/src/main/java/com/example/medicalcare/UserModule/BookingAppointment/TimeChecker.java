package com.example.medicalcare.UserModule.BookingAppointment;

import java.util.Calendar;

public class TimeChecker {

    static boolean isCorrectTime;
    static boolean isTimeGreaterThanCurrent;



    public static boolean isCorrectTime(String time) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR); // 12 hour format
        int currentAmPm = calendar.get(Calendar.AM_PM);

        // Extracting hour and AM/PM from the given time
        int givenHour = Integer.parseInt(time.substring(0, time.length() - 2));
        int givenAmPm = (time.endsWith("AM")) ? Calendar.AM : Calendar.PM;

        // Comparing AM/PM
        if (currentAmPm != givenAmPm) {
            return false;
        }

        return givenHour == currentHour;
    }


    public static boolean isTimeGreaterThanCurrent(String time) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 24 hour format
        int currentAmPm = calendar.get(Calendar.AM_PM);

        // Extracting hour and AM/PM from the given time
        int givenHour = Integer.parseInt(time.substring(0, time.length() - 2)); // Extracting hour part
        int givenAmPm = (time.endsWith("AM")) ? Calendar.AM : Calendar.PM;

        // Adjusting given hour for PM time (as it's represented differently in the 12-hour format)
        if (givenAmPm == Calendar.PM && givenHour != 12) {
            givenHour += 12;
        }

        // Comparing AM/PM
        if (currentAmPm != givenAmPm) {
            return (givenAmPm == Calendar.PM); // If AM, it's always less than PM
        }

        // Comparing hours
        return givenHour > currentHour;
    }

}
