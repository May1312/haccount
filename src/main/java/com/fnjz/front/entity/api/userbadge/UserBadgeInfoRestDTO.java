package com.fnjz.front.entity.api.userbadge;

import java.util.Date;

/**
 * 我的页面--用户--徽章领取dto---徽章详情dto
 * Created by yhang on 2018/12/17.
 */
public class UserBadgeInfoRestDTO {

    /**
     * 徽章id
     */
    private Integer badgeId;
    /**
     * 徽章名称
     */
    private String badgeName;
    /**
     * 进阶百分比 投资/工资/奖金/兼职徽章
     */
    private Double percentage;
    /**
     * 获得时间
     */
    private Date createDate;
    /**
     * 徽章图标
     */
    private String icon;
    /**
     * 工资数  工资徽章
     */
    private Double salary;
    /**
     * 话术
     */
    private String words;
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 优先级  倒序
     */
    private Integer priority;

    public Integer getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Integer badgeId) {
        this.badgeId = badgeId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
