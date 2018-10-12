package com.fnjz.front.enums;

/**
 * 积分类型枚举
 * Created by yhang on 2018/10/12.
 */
public enum IntegralEnum {

    /**
     * 七天签到
     */
    SIGNIN_7(1,"七天签到"),
    /**
     * 十四天签到
     */
    SIGNIN_14(2,"十四天签到"),
    /**
     * 二十一天签到
     */
    SIGNIN_21(3,"二十一天签到"),
    /**
     * 二十八天签到
     */
    SIGNIN_28(4,"二十八天签到");

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
