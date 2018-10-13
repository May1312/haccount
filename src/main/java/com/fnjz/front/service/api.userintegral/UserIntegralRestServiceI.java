package com.fnjz.front.service.api.userintegral;

import org.jeecgframework.core.common.service.CommonService;

import java.util.Map;

public interface UserIntegralRestServiceI extends CommonService{

    /**
     * 添加积分流水记录
     * @param userInfoId
     * @param map
     */
    void signInIntegral(String userInfoId, String shareCode,Map<String,String> map);
}
