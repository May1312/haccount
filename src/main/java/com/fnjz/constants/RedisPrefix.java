package com.fnjz.constants;


public class RedisPrefix {

    /**
     * 用户信息前缀
     */
    public static final String PREFIX_USER_LOGIN = "user_login:";
    /**
     * 用户登录验证码前缀
     */
    public static final String PREFIX_USER_VERIFYCODE_LOGIN = "user_verifycode_login:";
    /**
     * 用户注册验证码前缀
     */
    public static final String PREFIX_USER_VERIFYCODE_REGISTER = "user_verifycode_register:";
    /**
     * 用户找回密码验证码前缀
     */
    public static final String PREFIX_USER_VERIFYCODE_RESETPWD = "user_verifycode_resetpwd:";
    /**
     * 用户绑定手机号验证码前缀
     */
    public static final String PREFIX_USER_VERIFYCODE_BIND_MOBILE = "user_verifycode_bindmobile:";
    /**
     * 用户修改绑定手机号验证码前缀
     */
    public static final String PREFIX_USER_VERIFYCODE_CHANGE_MOBILE = "user_verifycode_changemobile:";
    /**
     * 小程序session_key缓存前缀
     */
    public static final String PREFIX_WXAPPLET_SESSION_KEY = "user_wxapplet_sessionkey:";
    /**
     * 用户-账本关联表前缀
     */
    public static final String PREFIX_USER_ACCOUNT_BOOK = "user_account_book:";
    /**
     * 我的统计(连续打卡+记账总笔数)前缀
     */
    public static final String PREFIX_MY_COUNT = "my_info_count:";
    /**
     * 验证码缓存有效时间 默认三分钟
     */
    public static final long VERIFYCODE_VALID_TIME = 3L;
    /**
     * 用户信息缓存有效时间 默认三十天
     */
    public static final long USER_VALID_TIME = 30L;

    /**
     * 用户信息缓存有效时间 默认三十天  单位秒
     */
    public static final String USER_EXPIRE_TIME = "2592000";
    /**
     * 小程序session key 信息缓存有效时间 默认1天
     */
    public static final long SESSION_KEY_TIME = 1L;
    /**
     * 缓存类目支出前缀
     */
    public static final String USER_SPEND_LABEL_TYPE = "user_spend_label_type:";
    /**
     * 缓存类目收入前缀
     */
    public static final String USER_INCOME_LABEL_TYPE = "user_income_label_type:";
    /**
     * admin 创建/删除测试用户验证码前缀
     */
    public static final String ADMIN_CERTAIN = "admin_certain:";
}
