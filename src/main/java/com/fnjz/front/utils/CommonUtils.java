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

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

        return str+System.currentTimeMillis()+rannum;// 当前时间+时间戳+随机数
    }

    public static void main(String[] args){
        System.out.println(getAccountOrder());
    }
}
