package ru.at_consulting.itsm.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeConversion {
    DateFormat dfm = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");  

    long unixtime;
    public long timeConversion(String time)
    {
        dfm.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));
    try
    {
        unixtime = dfm.parse(time).getTime();  
        unixtime=unixtime/1000;
    } 
    catch (ParseException e) 
    {
        e.printStackTrace();
    }
    return unixtime;
    }

}
