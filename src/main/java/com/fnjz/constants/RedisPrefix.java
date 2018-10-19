package com.fnjz.constants;


public class RedisPrefix {

    public static final String BASE_URL = "/api/*/";

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
     * 签到前缀
     */
    public static final String PREFIX_SIGN_IN = "user_sign_in:";
    /**
     * 新手任务前缀
     */
    public static final String PREFIX_NEWBIE_TASK = "user_integral_task:newbie_task:";
    /**
     * 今日任务前缀
     */
    public static final String PREFIX_TODAY_TASK = "user_integral_task:today_task:";
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
     * 缓存系统类目收入前缀
     */
    public static final String SYS_INCOME_LABEL_TYPE = "sys_label_type:income";

    /**
     * 缓存系统类目支出前缀
     */
    public static final String SYS_SPEND_LABEL_TYPE = "sys_label_type:spend";
    /**
     * 缓存系统连续签到领取积分
     */
    public static final String SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE = "sys_integral:sign_in_cycle_aware";
    /**
     * 缓存系统新手任务领取积分
     */
    public static final String SYS_INTEGRAL_NEWBIE_TASK = "sys_integral:newbie_task_aware";
    /**
     * 缓存系统今日任务领取积分
     */
    public static final String SYS_INTEGRAL_TODAY_TASK = "sys_integral:today_task_aware";
    /**
     * 缓存用户连续签到领取积分数据
     */
    public static final String USER_INTEGRAL_SIGN_IN_CYCLE_AWARE = "user_sign_in_cycle_aware_status:";

    /**
     * 缓存用户首页公告读取次数
     */
    public static final String USER_HOME_WINDOW_READ = "user_home_window_read:";

    /**
     * 缓存用户邀请好友数
     */
    public static final String USER_INVITE_COUNT = "user_invite_count:";

    public static final String SPEND = "SPEND";

    public static final String INCOME = "INCOME";

    /**
     * admin 创建/删除测试用户验证码前缀
     */
    public static final String ADMIN_CERTAIN = "admin_certain:";

    /**
     * 结构消费比查询中饮食id
     */
    public static final String CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE = "2c91dbe363f72fec0163f818eea4001b";

    /**
     * 离线同步时间间隔设置  1天
     */
    public static final String SYN_INTERVAL = "86400000";

    /**
     * 小程序活动统计
     */
    public static final String PREFIX_WXAPPLET_ACTIVITY = "wxapplet_activity_statistics";
}
