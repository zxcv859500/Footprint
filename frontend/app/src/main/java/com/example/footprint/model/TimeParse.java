package com.example.footprint.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeParse {


    public static String getTime(String strTime){
        TimeZone timeZone;
        String time;
        DateFormat dateFormat_UTX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat dateFormat_Local = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        timeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat_UTX.setTimeZone(timeZone);
        try {
            date = dateFormat_UTX.parse(strTime);
            time = dateFormat_Local.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
            time = dateFormat_Local.format(date);
        }


        return time;
    }


}
