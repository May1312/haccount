package com.fnjz.front.entity.api.usersigninaward;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户连签奖励领取情况表
 * Created by yhang on 2018/12/08.
 */
@Entity
@Table(name = "hbird_account_book")
public class UserSignInAwardRestEntity {

    public UserSignInAwardRestEntity(){};

    public UserSignInAwardRestEntity(Integer userInfoId, String categoryOfBehavior, Integer cycle, Integer cycleAwardStatus, Integer getTimes, Integer delflag) {
        this.userInfoId = userInfoId;
        this.categoryOfBehavior = categoryOfBehavior;
        this.cycle = cycle;
        this.cycleAwardStatus = cycleAwardStatus;
        this.getTimes = getTimes;
        this.delflag = delflag;
    }

    /**
     * id
     */
    private Integer id;
    /**
     * userInfoId
     */
    private Integer userInfoId;
    /**
     * 行为类别  新手任务NewbieTask  每日任务TodayTask  签到奖励SignIn
     */
    private String categoryOfBehavior;
    /**
     * 签到周期定义  值可能为7/14/21/28
     */
    private Integer cycle;

    /**
     * 奖励领取状态 1:未领取 2:已领取 3:不可领
     */
    private Integer cycleAwardStatus;

    /**
     * 可领取次数 默认0
     */
    private Integer getTimes;

    private Date createDate;

    private Date updateDate;

    /**
     * 删除标记   0有效  1:失效
     */
    private Integer delflag;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @Column(name = "USER_INFO_ID")
    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }
    @Column(name = "CATEGORY_OF_BEHAVIOR")
    public String getCategoryOfBehavior() {
        return categoryOfBehavior;
    }

    public void setCategoryOfBehavior(String categoryOfBehavior) {
        this.categoryOfBehavior = categoryOfBehavior;
    }
    @Column(name = "CYCLE")
    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }
    @Column(name = "CYCLE_AWARD_STATUS")
    public Integer getCycleAwardStatus() {
        return cycleAwardStatus;
    }

    public void setCycleAwardStatus(Integer cycleAwardStatus) {
        this.cycleAwardStatus = cycleAwardStatus;
    }
    @Column(name = "GET_TIMES")
    public Integer getGetTimes() {
        return getTimes;
    }

    public void setGetTimes(Integer getTimes) {
        this.getTimes = getTimes;
    }
    @Column(name = "CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    @Column(name = "UPDATE_DATE")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    @Column(name = "DELFLAG")
    public Integer getDelflag() {
        return delflag;
    }

    public void setDelflag(Integer delflag) {
        this.delflag = delflag;
    }
}