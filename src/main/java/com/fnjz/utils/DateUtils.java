package com.fnjz.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * redis缓存时间计算
 * Created by yhang on 2018/5/31.
 */
public class DateUtils {
    public static long addNDay(int day) {
        Date date=new Date(); //取时间
        System.out.println(date.getTime());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,day); //把日期往后增加一天,整数  往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        return date.getTime();
    }
    public static void main(String[] args) {
        System.out.println(addNDay(1));
    }
}
