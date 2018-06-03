package com.fnjz.front.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 把字符串加密后返回
     * @param str
     * @return str
     */
    public static String getMD5(String str){
        if(str==null) return null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(md==null){
            return null;
        }
        md.update(str.getBytes());  //使用指定的 byte 数组更新摘要
        byte[] b = md.digest(); 	//通过执行诸如填充之类的最终操作完成哈希计算

        str = byte2hex(b);     		//把字节数组转变为16进制的字符串
        str=str.toUpperCase();
        return str;
    }
    //把字节数组转变为16进制的字符串
    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0Xff));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
