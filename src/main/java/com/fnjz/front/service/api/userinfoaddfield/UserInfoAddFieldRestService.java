package com.fnjz.front.service.api.userinfoaddfield;

import com.fnjz.front.entity.api.userinfo.ConsigneeAddressRestDTO;

import java.util.Map;

/**
 * Created by yhang on 2018/11/28.
 */
public interface UserInfoAddFieldRestService {

    /**
     * 判断是否绑定openId
     * @param userInfoId
     */
    Map<String,Object> checkExists(String userInfoId);

    /**
     * 获取收货地址
     * @param userInfoId
     * @return
     */
    ConsigneeAddressRestDTO getConsigneeAddress(String userInfoId);

    /**
     * save or update 收货地址
     * @param userInfoId
     * @param bean
     */
    void updateConsigneeAddress(String userInfoId, ConsigneeAddressRestDTO bean);

    void insertOpenId(String userInfoId, String openId, int i);

    void updateOpenId(String userInfoId, String openId, Integer id, int i);

    /**
     * 检查openid 是否存在
     * @param userInfoId
     * @param type
     * @return
     */
    Integer checkExistsOpenId(String userInfoId, Integer type);

    /**
     * 检查是否存在
     * @param userInfoId
     * @return
     */
    Map<String,Object> checkExistsOpenIdByUserInfoIdForWeChat(String userInfoId,int flag);
}
