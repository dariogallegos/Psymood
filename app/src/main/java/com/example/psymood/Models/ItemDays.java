package com.example.psymood.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ItemDays {

    private String day1, day2, day3, day4, day5, day6, day7;

    public ItemDays() {

        Calendar calendar = Calendar.getInstance();

        Date day1 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date day2 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Date day3 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date day4 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date day5 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date day6 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date day7 = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("EE");
        this.day1 = sdf.format(day1);
        this.day2 = sdf.format(day2);
        this.day3 = sdf.format(day3);
        this.day4 = sdf.format(day4);
        this.day5 = sdf.format(day5);
        this.day6 = sdf.format(day6);
        this.day7 = sdf.format(day7);
    }

    public String getDay1() {
        return day1;
    }

    public String getDay2() {
        return day2;
    }

    public String getDay3() {
        return day3;
    }

    public String getDay4() {
        return day4;
    }

    public String getDay5() {
        return day5;
    }

    public String getDay6() {
        return day6;
    }

    public String getDay7() {
        return day7;
    }
}
