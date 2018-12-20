package com.fnjz.front.service.api.userwxqrcode;

import org.jeecgframework.core.common.service.CommonService;


public interface UserWXQrCodeRestServiceI extends CommonService{

    /**
     * 插入用户-邀请码绑定关系
     * @param userInfoId
     * @param url
     */
    void insert(String userInfoId, String url,String type);

    /**
     * 获取邀请小程序码
     * @param userInfoId
     * @return
     */
    String getInviteQrCode(String userInfoId,String type);
}
