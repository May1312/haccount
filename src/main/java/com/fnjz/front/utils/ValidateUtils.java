package com.fnjz.front.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号校验工具类
 * Created by yhang on 2018/6/8.
 */
public class ValidateUtils {
    public static boolean isMobile(String mobiles){
        boolean flag = false;
        try{
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    public static void main(String[] args){
        System.out.println(!isMobile("15501233770"));
    }
}
