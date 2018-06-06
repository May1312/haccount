package com.fnjz.constants;


public class RedisPrefix {

    //用户登录验证码前缀
    public static final String PREFIX_USER_VERIFYCODE_LOGIN = "user_verifycode_login:";
    //用户注册验证码前缀
    public static final String PREFIX_USER_VERIFYCODE_REGISTER = "user_verifycode_register:";
    //用户找回密码验证码前缀
    public static final String PREFIX_USER_VERIFYCODE_RESETPWD = "user_verifycode_resetpwd:";
    //用户绑定手机号验证码前缀
    public static final String PREFIX_USER_VERIFYCODE_BIND_MOBILE = "user_verifycode_bindmobile:";
    //验证码缓存有效时间 默认三分钟
    public static final long VERIFYCODE_VALID_TIME = 3L;
    //用户信息缓存有效时间 默认三十天
    public static final long USER_VALID_TIME = 30L;
    //用户信息缓存有效时间 默认三十天  单位秒
    public static final String USER_EXPIRE_TIME = "2592000";
}
