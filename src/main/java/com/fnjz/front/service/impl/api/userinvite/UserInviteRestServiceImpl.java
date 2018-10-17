package com.fnjz.front.service.impl.api.userinvite;

import com.fnjz.front.dao.UserInviteRestDao;
import com.fnjz.front.service.api.api.userinvite.UserInviteRestServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yhang on 2018/10/17.
 */
@Service("userInviteRestService")
@Transactional
public class UserInviteRestServiceImpl implements UserInviteRestServiceI {

    @Autowired
    private UserInviteRestDao userInviteRestDao;

    /**
     * 获取邀请人数
     * @param userInfoId
     * @return
     */
    @Override
    public int getCountForInvitedUsers(String userInfoId) {
        return userInviteRestDao.getCountForInvitedUsers(userInfoId);
    }
}
