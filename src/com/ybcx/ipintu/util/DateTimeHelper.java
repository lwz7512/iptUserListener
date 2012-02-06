package com.ybcx.ipintu.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.ybcx.ipintu.R;


public class DateTimeHelper {
    private static final String TAG = "DateTimeHelper";


    public static final DateFormat AGO_FULL_DATE_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static String getRelativeDate(Date date, Context mctx) {
        Date now = new Date();
       
//        String prefix = mctx.getString(R.string.tweet_created_at_beautify_prefix);
        String prefix = "";
        String sec = mctx.getString(R.string.tweet_created_at_beautify_sec);
        String min = mctx.getString(R.string.tweet_created_at_beautify_min);
        String hour = mctx.getString(R.string.tweet_created_at_beautify_hour);
        String day = mctx.getString(R.string.tweet_created_at_beautify_day);
        String suffix = mctx.getString(R.string.tweet_created_at_beautify_suffix);
               
        // Seconds.
        long diff = (now.getTime() - date.getTime()) / 1000;

        if (diff < 0) {
            diff = 0;
        }

        if (diff < 60) {
            return diff + sec + suffix;
        }

        // Minutes.
        diff /= 60;

        if (diff < 60) {
            return  diff + min + suffix;
        }

        // Hours.
        diff /= 60;

        if (diff < 24) {
            return  diff + hour + suffix;
        }
        
        diff /=24;

        return diff+day+suffix;
    }
    
    //新加的整合格式方法，字符串到字符串
    //lwz7512 @ 2011/08/24
    public static  String getRelativeTimeByFormatDate(String publishTime, Context ctxt) throws ParseException{
    	Date pubDate = AGO_FULL_DATE_FORMATTER.parse(publishTime);
    	return getRelativeDate(pubDate, ctxt);
    }

    public static long getNowTime() {
        return Calendar.getInstance().getTime().getTime();
    }
    
    public static String longTimeToDate(long miliseconds){
    	Date dt = new Date(miliseconds);
    	return AGO_FULL_DATE_FORMATTER.format(dt);
    }
    
}
