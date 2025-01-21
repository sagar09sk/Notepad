package com.example.notepad.Models;

import java.util.Calendar;

public class DateFormatModel {
    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        return month;
    }
    public int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }
    public String getMonthFormat() {
        int month = getMonth();
        if (month == 1) return "JANUARY ";
        if (month == 2) return "FEBRUARY ";
        if (month == 3) return "MARCH ";
        if (month == 4) return "APRIL ";
        if (month == 5) return "MAY ";
        if (month == 6) return "JUNE ";
        if (month == 7) return "JULY ";
        if (month == 8) return "AUGUST ";
        if (month == 9) return "SEPTEMBER ";
        if (month == 10) return "OCTOBER ";
        if (month == 11) return "NOVEMBER ";
        if (month == 12) return "DECEMBER ";
        //default should never happen
        return "Null";

    }


}
