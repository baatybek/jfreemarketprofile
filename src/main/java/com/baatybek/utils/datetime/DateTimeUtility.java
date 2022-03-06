package com.baatybek.utils.datetime;

import org.jfree.data.time.FixedMillisecond;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeUtility {
    public static final SimpleDateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    public static Date generateDateFromStr(String dateTime) throws ParseException {
        return DEFAULT_DATE_TIME_FORMAT.parse(dateTime);
    }

    public static FixedMillisecond generateFixMilliSec(String dateTime) throws ParseException {
        Date date = generateDateFromStr(dateTime);
        return new FixedMillisecond(date);
    }
}
