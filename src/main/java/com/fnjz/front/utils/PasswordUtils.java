package com.fnjz.front.utils;

import org.jeecgframework.core.util.PasswordUtil;

/**
 * 密码加密工具类 调用jeecg内部方法
 * Created by yhang on 2018/7/18.
 */
public class PasswordUtils {

    /**
     * 秘钥
     */
    private static final String encrypt = "hbird";
    /**
     * 加密密码
     * @param password
     * @return
     */
    public static String getEncryptpwd(String password){
        byte[] salt = PasswordUtil.getStaticSalt();
        String ciphertext = PasswordUtil.encrypt(password, encrypt, salt);
        return ciphertext;
    }

    /**
     * 解密密码
     * @param password
     * @return
     */
    public static String getDecryptpwd(String password){
        byte[] salt = PasswordUtil.getStaticSalt();
        String ciphertext = PasswordUtil.decrypt(password, encrypt, salt);
        return ciphertext;
    }

    public static void main(String[] args) {
        String str = "123456";
        try {
            org.jeecgframework.core.util.LogUtil.info("密文:" + getEncryptpwd(str));
            org.jeecgframework.core.util.LogUtil.info("明文:" + getDecryptpwd(getEncryptpwd(str)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
