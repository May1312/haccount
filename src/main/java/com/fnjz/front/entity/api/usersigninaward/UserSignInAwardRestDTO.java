package com.fnjz.front.entity.api.usersigninaward;

/**
 * 用户连签奖励领取情况表
 * Created by yhang on 2018/12/10.
 */
public class UserSignInAwardRestDTO {

    public UserSignInAwardRestDTO(){};

    public UserSignInAwardRestDTO(Integer cycle, Integer cycleAwardStatus, Integer getTimes, Integer cycleAward) {
        this.cycle = cycle;
        this.cycleAwardStatus = cycleAwardStatus;
        this.getTimes = getTimes;
        this.cycleAward = cycleAward;
    }

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

    /**
     * 连续签到周期数 奖励的丰丰票数
     */
    private Integer cycleAward;

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Integer getCycleAwardStatus() {
        return cycleAwardStatus;
    }

    public void setCycleAwardStatus(Integer cycleAwardStatus) {
        this.cycleAwardStatus = cycleAwardStatus;
    }

    public Integer getGetTimes() {
        return getTimes;
    }

    public void setGetTimes(Integer getTimes) {
        this.getTimes = getTimes;
    }

    public Integer getCycleAward() {
        return cycleAward;
    }

    public void setCycleAward(Integer cycleAward) {
        this.cycleAward = cycleAward;
    }
}