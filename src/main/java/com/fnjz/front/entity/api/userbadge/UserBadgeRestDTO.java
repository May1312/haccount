package com.fnjz.front.entity.api.userbadge;

/**
 * 我的页面--用户--徽章领取dto
 * Created by yhang on 2018/12/17.
 */
public class UserBadgeRestDTO {

    /**
     * 徽章类型名称
     */
    private String badgeName;

    /**
     * 获取的徽章数
     */
    private Integer myBadges;

    /**
     * 总共徽章数
     */
    private Integer totalBadges;

    /**
     * 徽章图标
     */
    private String icon;

    /**
     * 优先级
     */
    private Integer priority;

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public Integer getMyBadges() {
        return myBadges;
    }

    public void setMyBadges(Integer myBadges) {
        this.myBadges = myBadges;
    }

    public Integer getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(Integer totalBadges) {
        this.totalBadges = totalBadges;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
