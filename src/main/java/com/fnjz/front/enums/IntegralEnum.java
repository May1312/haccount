package com.fnjz.front.enums;

/**
 * 积分类型枚举
 * Created by yhang on 2018/10/12.
 */
public enum IntegralEnum {

    /**
     * 一天签到
     */
    SIGNIN_1(1,"一天签到"),
    /**
     * 七天签到
     */
    SIGNIN_7(7,"七天签到"),
    /**
     * 十四天签到
     */
    SIGNIN_14(14,"十四天签到"),
    /**
     * 二十一天签到
     */
    SIGNIN_21(21,"二十一天签到"),
    /**
     * 二十八天签到
     */
    SIGNIN_28(28,"二十八天签到"),

    /**
     * 行为类别
     */
    CATEGORY_OF_BEHAVIOR_SIGN_IN(29,"SignIn"),

    /**
     * 获取方式
     */
    ACQUISITION_MODE_SIGN_IN(30,"SignIn");

    private int index;
    private String description;


    IntegralEnum(int index,String description){
        this.description = description;
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
