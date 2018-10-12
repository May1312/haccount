package com.fnjz.front.service.api.userintegral;

import org.jeecgframework.core.common.service.CommonService;

public interface UserIntegralRestServiceI extends CommonService{

    /**
     * 添加积分流水记录
     * @param userInfoId
     * @param userIntegralRestEntity
     */
    void integral(String userInfoId, Object userIntegralRestEntity);
}
