package com.fnjz.front.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yhang on 2018/6/14.
 */
public class DateUtils {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String convert2String(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date fetchBeginOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date fetchEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }
    //根据年/月获取对应的月份-天数
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
    //获取当前时间 - 年份
    public static int getCurrentYear(){
        Calendar a=Calendar.getInstance();
        return a.get(Calendar.YEAR);
    }
    //获取当前时间 - 月份
    public static String getCurrentMonth(){
        Calendar a=Calendar.getInstance();
        String month =  (a.get(Calendar.MONTH)+1)+"";
        // 查询天 mysql中保存的月份1-9 带0 ,校验month是否包含0
        if (!StringUtils.startsWithIgnoreCase(month, "0")
                && month.length() < 2) {
            month = "0" + month;
        }
        return month;
    }
    //获取当前时间 - 天数
    public static int getCurrentDay(){
        Calendar a=Calendar.getInstance();
        return a.get(Calendar.DATE);
    }
    //获取当前时间 年/月
    public static String getCurrentYearMonth(){
        Calendar a=Calendar.getInstance();
        String year = a.get(Calendar.YEAR)+"";
        String month = (a.get(Calendar.MONTH)+1)+"";
        // 查询天 mysql中保存的月份1-9 带0 ,校验month是否包含0
        if (!StringUtils.startsWithIgnoreCase(month, "0")
                && month.length() < 2) {
            month = "0" + month;
        }
        return year+"-"+month;
    }
    //获取当前时间 月/日
    public static String getCurrentMonthDay(){
        Calendar a=Calendar.getInstance();
        return (a.get(Calendar.MONTH)+1)+"-"+(a.get(Calendar.DATE));
    }

    public static void main(String[] args){
        System.out.println(getCurrentYearMonth());
    }
}
