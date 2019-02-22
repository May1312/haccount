package com.fnjz.front.enums;

/**
 * todo 暂时业务中定义 徽章类型的图标和描述  后期徽章如果增加,这两个字段放到db中
 * Created by yhang on 2019/2/22.
 */
public enum BadgeTypeEnum {

    /**
     * 攒钱徽章
     */
    zanqhz("badgetype/Icon_zanqhz_disabled@2x.png","Save money"),
    /**
     * 投资徽章
     */
    tzhz("badgetype/Icon_tzhz_disabled@2x.png","Money-invested"),
    /**
     * 奖金徽章
     */
    jzhz("badgetype/Icon_jzhz_disabled@2x.png","Award-money"),
    /**
     * 兼职徽章
     */
    jjhz("badgetype/Icon_jjhz_disabled@2x.png","Pluralism"),
    /**
     * 工资徽章
     */
    gzhz("badgetype/Icon_gzhz_disabled@2x.png","Pay-packet");

    private String badge;
    private String desc;

    BadgeTypeEnum(String badge, String desc){
        this.badge = badge;
        this.desc = desc;
    }

}
