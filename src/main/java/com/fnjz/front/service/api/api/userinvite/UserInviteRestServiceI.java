package com.fnjz.front.service.api.api.userinvite;

/**
 * Created by yhang on 2018/10/17.
 */
public interface UserInviteRestServiceI {
    /**
     * 获取邀请人数
     * @param userInfoId
     * @return
     */
    int getCountForInvitedUsers(String userInfoId);
}
