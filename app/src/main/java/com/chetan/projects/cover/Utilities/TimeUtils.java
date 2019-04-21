package com.chetan.projects.cover.Utilities;


import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static int HourToMin(int hour){
        return (int) TimeUnit.HOURS.toMinutes(hour);
    }

    public static int HourToSec(int hour){
        return (int) TimeUnit.HOURS.toSeconds(hour);
    }

    public static int HourMinToSec(int hour , int min){
        return (int) (TimeUnit.HOURS.toSeconds(hour) +
                TimeUnit.MINUTES.toSeconds(min));
    }
}
