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
     * 用户无场景获取验证码
     */
    public static final String PREFIX_USER_VERIFYCODE = "user_verifycode:";

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
     * 用户兑换红包类商品手机号验证码前缀
     */
    public static final String PREFIX_USER_VERIFYCODE_CASH_MOBILE = "user_verifycode_cashmobile:";
    /**
     * 小程序session_key缓存前缀
     */
    public static final String PREFIX_WXAPPLET_SESSION_KEY = "user_wxapplet_sessionkey:";
    /**
     * 小程序access token缓存前缀
     */
    public static final String PREFIX_WXAPPLET_ACCESS_TOKEN = "wxapplet_access_token";
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

    public static final long VALID_TIME_28 = 28L;

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
     * 缓存类目收入前缀v2 多账本  前缀:用户id:账本id :string 类型
     */
    public static final String USER_LABEL = "user_label:";

    /**
     * 缓存系统类目收入前缀
     */
    public static final String SYS_INCOME_LABEL_TYPE = "sys_label_type:income";

    /**
     * 缓存系统类目收入前缀 id:账本id :string 类型
     */
    public static final String SYS_LABEL = "sys_label:";

    /**
     * 缓存系统类目支出前缀
     */
    public static final String SYS_SPEND_LABEL_TYPE = "sys_label_type:spend";
    /**
     * 缓存系统连续签到领取积分
     */
    public static final String SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE = "sys_integral:sign_in_cycle_aware";
    //public static final String SYS_INTEGRAL_SIGN_IN_CYCLE_AWARD = "sys_integral:sign_in_cycle_award";
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
    public static final String USER_HOME_WINDOW_READ = "home_window_read:user_read:";

    /**
     * 缓存系统首页公告读取次数
     */
    public static final String SYS_HOME_WINDOW_READ = "home_window_read:sys_read:";

    /**
     * 缓存系统轮播图读取次数
     */
    public static final String SYS_SLIDESHOW_READ = "sys_slideshow_read:";

    /**
     * 缓存用户邀请好友数
     */
    public static final String USER_INVITE_COUNT = "user_invite_count:";

    /**
     * 用户注册成功奖励积分数
     */
    public static final String USER_REGISTER_INTEGRAL = "user_register_integral:";

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

    /**
     * 小程序服务推送  formid前缀
     */
    public static final String PREFIX_WXAPPLET_PUSH = "wxapplet_openid_formid:";

    /**
     * 小程序端服务推送 userinfoid  openid
     */
    public static final String PREFIX_WXAPPLET_USERINFOID_OPENID = "wxapplet_userinfoid_openid:";

    /**
     * 头部轮播缓存
     */
    public static final String PREFIX_HEAD_REPORT = "head_report:";

    /**
     * 缓存活动成功读取次数
     */
    public static final String PREFIX_INTEGRALS_ACTIVITY = "user_integrals_activity_success:";

    /**
     * 新账本创建 缓存标识
     */
    public static final String PREFIX_SYS_NEW_ACCOUNT_BOOK = "new_account_book:sys";

    /**
     * 用户查看新账本 标识
     */
    public static final String PREFIX_USER_NEW_ACCOUNT_BOOK = "new_account_book:user:";

    /**
     * 解锁新徽章前缀
     */
    public static final String PREFIX_USER_NEW_UNLOCK_BADGE = "user_new_unlock_badge:";
}
