package com.fnjz.front.entity.api;

import java.util.Date;

/**
 * 邀请用户展示对象
 * Created by yhang on 2018/10/17.
 */
public class UserInviteRestDTO {

    private String nickName;

    private String avatarUrl;

    private Date registerdate;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(Date registerdate) {
        this.registerdate = registerdate;
    }
}
