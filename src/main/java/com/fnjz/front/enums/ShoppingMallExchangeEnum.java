package com.fnjz.front.enums;

/**
 * 积分兑换类型枚举
 * Created by yhang on 2018/10/12.
 */
public enum ShoppingMallExchangeEnum {

    /**
     * 话费类型
     */
    TELEPHONE_CHARGE(1),
    /**
     * 流量类型
     */
    NETFLOW(2),
    /**
     * 视频会员类型
     */
    VIDEO_MEMBERSHIP(3);

    private int index;

    ShoppingMallExchangeEnum(int index){
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
