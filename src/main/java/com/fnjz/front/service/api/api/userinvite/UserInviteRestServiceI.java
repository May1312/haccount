package com.fnjz.front.service.api.api.userinvite;

import com.fnjz.front.entity.api.PageRest;

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

    /**
     * 邀请历史记录列表
     * @param userInfoId
     * @param curPage
     * @param pageSize
     * @return
     */
    PageRest listForPage(String userInfoId, Integer curPage, Integer pageSize);

    boolean insert(int i, int inviteCode);
}
