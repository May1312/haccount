package com.fnjz.front.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by yhang on 2018/6/12.
 */
public class CommonUtils {

    /**
     * 单笔订单号生成规则   年月日+时间戳+5位随机数
     * @return
     */
    public static String getAccountOrder() {

        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();
        // 获取5位随机数
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
        // 当前时间+时间戳+随机数
        return str+System.currentTimeMillis()+rannum;
    }

    /**
     * session_key   时间戳+5位随机数
     * @return
     */
    public static String getSessionKeyPrefix() {

        Random random = new Random();
        // 获取5位随机数
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
        // 时间戳+随机数
        return System.currentTimeMillis()+""+rannum;
    }

    public static void main(String[] args){
        System.out.println(getAccountOrder());
    }
}
