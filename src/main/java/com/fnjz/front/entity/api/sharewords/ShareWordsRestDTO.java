package com.fnjz.front.entity.api.sharewords;

import java.util.Date;

/**
 * 日签分享话术
 * Created by yhang on 2018/12/08.
 */

public class ShareWordsRestDTO{

    /**
     * 节日名称
     */
    private String festival;
    /**
     * 节日日期
     */
    private Date festivalDay;
    /**
     * 话术
     */
    private String words;

    /**
     * 图片
     */
    private String icon;

    /**
     * 签到领取的积分数
     */
    private Integer signInAware;

    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 累计记账天数
     */
    private int chargeDays;
    /**
     * 注册时间
     * @return
     */
    private Date registerDate;

    /**
     * 小程序二维码
     * @return
     */
    private String qrCodeUrl;

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public Date getFestivalDay() {
        return festivalDay;
    }

    public void setFestivalDay(Date festivalDay) {
        this.festivalDay = festivalDay;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSignInAware() {
        return signInAware;
    }

    public void setSignInAware(Integer signInAware) {
        this.signInAware = signInAware;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getChargeDays() {
        return chargeDays;
    }

    public void setChargeDays(int chargeDays) {
        this.chargeDays = chargeDays;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}