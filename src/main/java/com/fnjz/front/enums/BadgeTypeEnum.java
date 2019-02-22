package com.fnjz.front.enums;

/**
 * todo 暂时业务中定义 徽章类型的图标和描述  后期徽章如果增加,这两个字段放到db中
 * Created by yhang on 2019/2/22.
 */
public enum BadgeTypeEnum {

    /**
     * 攒钱徽章
     */
    zanqhz("攒钱","http://label.image.fengniaojizhang.cn/badgetype/Icon_zanqhz_disabled@2x.png","Save money"),
    /**
     * 投资徽章
     */
    tzhz("投资","http://label.image.fengniaojizhang.cn/badgetype/Icon_tzhz_disabled@2x.png","Money-invested"),
    /**
     * 奖金徽章
     */
    jzhz("奖金","http://label.image.fengniaojizhang.cn/badgetype/Icon_jzhz_disabled@2x.png","Award-money"),
    /**
     * 兼职徽章
     */
    jjhz("兼职","http://label.image.fengniaojizhang.cn/badgetype/Icon_jjhz_disabled@2x.png","Pluralism"),
    /**
     * 工资徽章
     */
    gzhz("工资","http://label.image.fengniaojizhang.cn/badgetype/Icon_gzhz_disabled@2x.png","Pay-packet");

    private String badgeTypeName;
    private String badgeTypeIcon;
    private String badgeTypeDesc;

    BadgeTypeEnum(String badgeTypeName,String badgeTypeIcon, String badgeTypeDesc){
        this.badgeTypeName = badgeTypeName;
        this.badgeTypeIcon = badgeTypeIcon;
        this.badgeTypeDesc = badgeTypeDesc;
    }

    public String getBadgeTypeName() {
        return badgeTypeName;
    }

    public void setBadgeTypeName(String badgeTypeName) {
        this.badgeTypeName = badgeTypeName;
    }

    public String getBadgeTypeIcon() {
        return badgeTypeIcon;
    }

    public void setBadgeTypeIcon(String badgeTypeIcon) {
        this.badgeTypeIcon = badgeTypeIcon;
    }

    public String getBadgeTypeDesc() {
        return badgeTypeDesc;
    }

    public void setBadgeTypeDesc(String badgeTypeDesc) {
        this.badgeTypeDesc = badgeTypeDesc;
    }
}
