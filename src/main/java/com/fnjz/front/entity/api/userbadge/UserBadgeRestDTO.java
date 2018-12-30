package com.fnjz.front.entity.api.userbadge;

import org.apache.commons.lang.StringUtils;

/**
 * 我的页面--用户--徽章领取dto
 * Created by yhang on 2018/12/17.
 */
public class UserBadgeRestDTO {

    /**
     * 徽章类型id
     */
    private Integer badgeTypeId;
    /**
     * 徽章类型名称
     */
    private String badgeName;

    /**
     * 获取的徽章数 默认0
     */
    private Integer myBadges = 0;

    /**
     * 总共徽章数
     */
    private Integer totalBadges;

    /**
     * 徽章图标
     */
    private String icon;

    /**
     * 优先级  倒序
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

    /**
     * 重新equals方法
     * 徽章名称相等即为相等
     *
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            //自己和自己比较时,直接返回true
            if (obj == this) {
                return true;
            }
            //判断是否是同类型的对象进行比较
            if (obj instanceof UserBadgeRestDTO) {
                UserBadgeRestDTO dto = (UserBadgeRestDTO) obj;
                if (StringUtils.equals(dto.badgeName,this.badgeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Integer getBadgeTypeId() {
        return badgeTypeId;
    }

    public void setBadgeTypeId(Integer badgeTypeId) {
        this.badgeTypeId = badgeTypeId;
    }
}