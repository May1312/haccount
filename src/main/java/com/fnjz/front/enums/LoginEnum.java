package com.fnjz.front.enums;

/**
 * 登录方式相关
 * Created by yhang on 2018/7/11.
 */
public enum LoginEnum {

    /**
     * 校验手机号密码
     */
    LOGIN_BY_PWD(1),
    /**
     * 校验手机号验证码
     */
    LOGIN_BY_VERIFYCODE(2),
    LOGIN_BY_WECHAT(3),
    LOGIN_BY_WXAPPLET(4);

    private int index;

    LoginEnum(int index){
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
