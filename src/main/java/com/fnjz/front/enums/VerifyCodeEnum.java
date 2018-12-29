package com.fnjz.front.enums;

/**
 * 验证码枚举类
 * Created by yhang on 2018/7/11.
 */
public enum VerifyCodeEnum {

    /**
     * 登录验证码
     */
    VERIFYCODE_LOGIN(1),

    /**
     * 注册验证码
     */
    VERIFYCODE_REGISTER(2),

    /**
     * 重置密码验证码
     */
    VERIFYCODE_RESETPWD(3),

    /**
     * 绑定手机号验证码
     */
    VERIFYCODE_BIND_MOBILE(4),

    /**
     * 修改手机号--->旧手机号获取验证码
     */
    VERIFYCODE_OLD_MOBILE(5),

    /**
     * 商城  红包兑换验证码
     */
    VERIFYCODE_CASH_MOBILE(6);

    private int index;

    VerifyCodeEnum(int index){
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
