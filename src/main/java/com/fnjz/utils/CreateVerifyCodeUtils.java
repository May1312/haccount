package com.fnjz.utils;

/**
 * y验证码随机生成工具类
 * Created by yhang on 2018/5/31.
 */
public class CreateVerifyCodeUtils {
    public static String createRandom(int length) {
        String retStr = "";
        String strTable = "1234567890";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }
    @org.junit.Test
    public void run(){
        System.out.println(createRandom(6));
    }
}
