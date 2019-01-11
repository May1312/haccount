package com.fnjz.front.entity.api;

import javax.persistence.Column;
import java.util.Date;

/**
 * 邀请用户展示对象
 * Created by yhang on 2018/10/17.
 */
public class UserInviteRestDTO {

    private String nickName;

    private String avatarUrl;

    private Date registerDate;

    private double integralNum;

    @Column(name ="NICK_NAME",length=32)
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    @Column(name ="AVATAR_URL",nullable=true,length=255)
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    @Column(name ="REGISTER_DATE")
    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public double getIntegralNum() {
        return integralNum;
    }

    public void setIntegralNum(double integralNum) {
        this.integralNum = integralNum;
    }
}
