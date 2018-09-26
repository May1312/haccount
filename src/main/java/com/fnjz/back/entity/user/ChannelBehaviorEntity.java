package com.fnjz.back.entity.user;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/9/17 14:59
 * @Description:用户渠道行为统计
 */
@ExcelTarget(value = "ChannelBehaviorEntity")
public class ChannelBehaviorEntity implements Serializable, Comparable<ChannelBehaviorEntity> {

    /**
     * 蜂鸟id
     */
    @Excel(name = "蜂鸟id")
    private String fengniaoId;
    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private String userId;
    /**
     * 用户昵称
     */
    @Excel(name = "用户昵称")
    private String userNickName;

    /**
     * 注册时间
     */
    @Excel(name = "注册时间")
    private Date registerDate;
    private Date registerstartDate;
    private Date registerendDate;

    /**
     * 终端系统标识
     */
    private String mobileSystem;
    /**
     * 安卓渠道
     */
    @Excel(name = "渠道")
    private String downloadChannel;
    /**
     * 记账总天数
     */
    @Excel(name = "记账总天数")
    private int accountTotalDayNumber;
    /**
     * 记账总笔数
     */
    @Excel(name = "记账总笔数")
    private int accountTotalTheNumber;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getRegisterstartDate() {
        return registerstartDate;
    }

    public void setRegisterstartDate(Date registerstartDate) {
        this.registerstartDate = registerstartDate;
    }

    public Date getRegisterendDate() {
        return registerendDate;
    }

    public void setRegisterendDate(Date registerendDate) {
        this.registerendDate = registerendDate;
    }

    public String getMobileSystem() {
        return mobileSystem;
    }

    public void setMobileSystem(String mobileSystem) {
        this.mobileSystem = mobileSystem;
    }

    public String getDownloadChannel() {
        return downloadChannel;
    }

    public void setDownloadChannel(String downloadChannel) {
        this.downloadChannel = downloadChannel;
    }

    public int getAccountTotalDayNumber() {
        return accountTotalDayNumber;
    }

    public void setAccountTotalDayNumber(int accountTotalDayNumber) {
        this.accountTotalDayNumber = accountTotalDayNumber;
    }

    public int getAccountTotalTheNumber() {
        return accountTotalTheNumber;
    }

    public void setAccountTotalTheNumber(int accountTotalTheNumber) {
        this.accountTotalTheNumber = accountTotalTheNumber;
    }

    public String getFengniaoId() {
        return fengniaoId;
    }

    public void setFengniaoId(String fengniaoId) {
        this.fengniaoId = fengniaoId;
    }


    @Override
    public int compareTo(ChannelBehaviorEntity o) {
        if (accountTotalTheNumber < o.getAccountTotalTheNumber()) {
            return 1;
        }
        if (accountTotalTheNumber > o.getAccountTotalTheNumber()) {
            return -1;
        }

        if (accountTotalTheNumber == o.getAccountTotalTheNumber()) {
            if (accountTotalDayNumber < o.getAccountTotalDayNumber()) {
                return 1;
            }
            if (accountTotalDayNumber > o.getAccountTotalDayNumber()) {
                return -1;
            }
        }
        return 0;
    }
}
