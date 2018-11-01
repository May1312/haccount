package com.fnjz.front.enums;

/**
 * 签到状态
 * Created by yhang on 2018/7/11.
 */
public enum SignInEnum {

    /**
     * 已签到
     */
    HAS_SIGNED(2),
    /**
     * 未签到
     */
    NOT_SIGN(1),
    /**
     * 可补签
     */
    COMPLEMENT_SIGNED(3);

    private int index;

    SignInEnum(int index){
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
