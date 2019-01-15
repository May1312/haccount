package com.fnjz.front.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * Created by yhang on 2018/6/8.
 */
public class ValidateUtils {
    /**
     * 手机号11位校验
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles){
        boolean flag = false;
        if(mobiles.length()==11){
            try{
                Pattern p = Pattern.compile("0?(12|13|14|15|16|17|18|19)[0-9]{9}");
                Matcher m = p.matcher(mobiles);
                flag = m.matches();
            }catch(Exception e){
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 小数整数部分8位，小数点后两位校验
     * @param decimal
     * @return
     */
    public static boolean checkDecimal(String decimal){
        boolean flag = false;
        String str = "^-?(0|([1-9]\\d{0,7}))(\\.\\d{0,2})?$";
        if(decimal.matches(str)){
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }

    public static void main(String[] args){
        System.out.println(isMobile("15501233770"));
        //System.out.println(checkDecimal("1.00"));
    }
}
