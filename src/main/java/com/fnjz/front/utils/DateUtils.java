package com.fnjz.front.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yhang on 2018/6/14.
 */
public class DateUtils {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_FORMAT_NO_YEAR = new SimpleDateFormat("MM/dd");

    public static String convert2String(Date date) {
        return DATE_FORMAT.format(date);
    }
    public static String convert2StringNoYear(Date date) {
        return DATE_FORMAT_NO_YEAR.format(date);
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

    /**
     * 根据时间戳 获取对应的月份-天数
     */
    public static int getDaysByDate(Date date) {
        Calendar a = Calendar.getInstance();
        a.setTime(date);
        return a.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据年-月 获取对应的月份-天数
     */
    public static int getMonthDaysByYearMonth(String yearMonth) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date parse = sdf.parse(yearMonth);
            cal.setTime(parse);
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //异常
        return 30;
    }
    /**
     * 获取当前时间 - 年份
     */
    public static int getCurrentYear(){
        Calendar a=Calendar.getInstance();
        return a.get(Calendar.YEAR);
    }
    /**
     * 获取当前时间 - 月份
     */
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
    /**
     * 获取当前时间 - 天数
     */
    public static int getCurrentDay(){
        Calendar a=Calendar.getInstance();
        return a.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取当前时间 年/月
     */
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

    /**
     * 获取当前时间 月/日
     */
    public static String getCurrentMonthDay(){
        Calendar a=Calendar.getInstance();
        return (a.get(Calendar.MONTH)+1)+"-"+(a.get(Calendar.DATE));
    }

    public static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * 获取date 下一天日期时间戳
     */
    public static Date getNextDay(Date date){
        Calendar a=Calendar.getInstance();
        a.setTime(date);
        a.add(Calendar.DAY_OF_YEAR, +1);
        return a.getTime();
    }

    /**
     * 获取年中的星期数
     */
    public static int getWeeks(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置周一为一周的第一天
        cal.setTime(date);
        int num = cal.get(Calendar.WEEK_OF_YEAR);
        return num;
    }

    /**
     * 根据周数获取日期
     */
    public static Map<String,String> getDateByWeeks(int weeks){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, weeks); // 设置周数
        cal.set(Calendar.DAY_OF_WEEK, 2); // 1表示周日，2表示周一，7表示周六
        Date begin = cal.getTime();
        Map<String,String> map = new HashMap<>();
        map.put("beginTime",convert2String(begin));
        cal.add(Calendar.DATE,6);
        Date end = cal.getTime();
        map.put("endTime",convert2String(end));
        return map;
    }

    /**
     * 根据传入月份，往前递推range个单位
     * @param month
     * @param range
     * @return
     */
    public static String getRangeMonth(String month,int range){
        //范围需要减1
        if(range!=-1){
            //TODO  方法不能复用  获取消费结构比不要用-1
            range = range+1;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,Integer.valueOf(month)-1);
        cal.add(Calendar.MONTH, range);
        Date starDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(starDate);
    }

    /**
     * 根据传入月份，往前递推range年个单位
     * @param month
     * @param range
     * @return
     */
    public static String getRangeMonthforYear(String month,int range){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH,Integer.valueOf(month)-1);
        cal.add(Calendar.YEAR, range);
        Date starDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(starDate);
    }

    /**
     * 字符串中的月份追加0
     * @throws ParseException
     */
    public static String checkYearMonth(String yearMonth){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        try {
            Date parse = sdf.parse(yearMonth);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
            return formatter.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据日期 格式化为年-月
     */
    public static String getYearMonthByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(date);
    }
    public static void main(String[] args) throws ParseException {
        System.out.println(getRangeMonth("08",-3));
        System.out.println(getRangeMonthforYear("02",-1));
        System.out.println(Integer.valueOf("-"+"520"));
        System.out.println(checkYearMonth("2018-2"));
    }
}
