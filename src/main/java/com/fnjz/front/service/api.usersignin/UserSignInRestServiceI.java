package com.fnjz.front.service.api.usersignin;

import com.alibaba.fastjson.JSONObject;
import org.jeecgframework.core.common.service.CommonService;

import java.time.LocalDateTime;

public interface UserSignInRestServiceI extends CommonService{

    /**
     * 签到
     * @param userInfoId
     */
    Integer signIn(String userInfoId,String shareCode);

    /**
     * 查看当天是否已签到
     * @return
     */
    boolean checkSignInForCurrentDay(String userInfoId);

    /**
     * 获取签到情况
     * @param userInfoId
     * @param shareCode
     * @return
     */
    JSONObject getSignIn(String userInfoId, String shareCode);

    /**
     * 根据月份查询签到数据
     * @param userInfoId
     * @param time
     * @return
     */
    JSONObject getSignInForMonth(String userInfoId, String time);

    /**
     * 补签
     * @param userInfoId
     * @param shareCode
     */
    void reSignIn(String userInfoId, String shareCode, LocalDateTime signInDate);
}
