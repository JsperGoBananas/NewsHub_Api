package com.jl.newshubapi.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    //get current UTC time
    public static LocalDateTime getCurrentUTCTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    //convert timestamp in milliseconds to UTC time in LocalDateTime
    public static LocalDateTime convertTimestampToUTC(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);
    }

    //convert LocalDateTime to Date
    public static java.util.Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return java.util.Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    //convert Date to LocalDateTime
    public static LocalDateTime convertDateToLocalDateTime(java.util.Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    private  String getCurrentTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
