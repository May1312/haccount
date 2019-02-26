package com.fnjz.front.entity.api.userbadge;

/**
 * 我的页面--用户--徽章领取dto---徽章详情dto
 * Created by yhang on 2018/12/17.
 */
public class UserBadgeInfoCheckRestDTO extends UserBadgeInfoRestDTO {

    /**
     * 徽章类型图标
     */
    private String badgeTypeIcon;

    /**
     * 徽章类型英文描述
     */
    private String badgeTypeDesc;

    /**
     * 徽章类型id
     */
    private Integer badgeTypeId;

    /**
     * 总徽章数
     */
    private Integer totalBadges;

    /**
     * 获得徽章数
     */
    private Integer myBadges;

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

    public Integer getBadgeTypeId() {
        return badgeTypeId;
    }

    public void setBadgeTypeId(Integer badgeTypeId) {
        this.badgeTypeId = badgeTypeId;
    }

    public Integer getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(Integer totalBadges) {
        this.totalBadges = totalBadges;
    }

    public Integer getMyBadges() {
        return myBadges;
    }

    public void setMyBadges(Integer myBadges) {
        this.myBadges = myBadges;
    }
}
