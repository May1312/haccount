package com.fnjz.front.service.impl.api.userwxqrcode;

import com.fnjz.front.dao.UserWXQrCodeRestDao;
import com.fnjz.front.service.api.userwxqrcode.UserWXQrCodeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userWXQrCodeService")
@Transactional
public class UserWXQrCodeRestServiceImpl extends CommonServiceImpl implements UserWXQrCodeRestServiceI {

    @Autowired
    private UserWXQrCodeRestDao userWXQrCodeRestDao;

    /**
     * 插入用户-邀请码绑定关系
     * @param userInfoId
     * @param url
     */
    @Override
    public void insert(String userInfoId, String url) {
        userWXQrCodeRestDao.insert(userInfoId,url);
    }

    @Override
    public String getInviteQrCode(String userInfoId) {
        return userWXQrCodeRestDao.getInviteQrCode(userInfoId);
    }
}