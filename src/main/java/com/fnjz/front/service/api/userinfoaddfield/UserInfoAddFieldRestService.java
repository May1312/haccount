package com.fnjz.front.service.api.userinfoaddfield;

/**
 * Created by yhang on 2018/11/28.
 */
public interface UserInfoAddFieldRestService {

    /**
     * 判断是否绑定openId
     * @param userInfoId
     * @param opendId
     */
    void checkExists(String userInfoId, String opendId);
}
