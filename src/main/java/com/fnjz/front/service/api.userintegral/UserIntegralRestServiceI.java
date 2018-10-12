package com.fnjz.front.service.api.userintegral;

import org.jeecgframework.core.common.service.CommonService;

public interface UserIntegralRestServiceI extends CommonService{

    void integral(String userInfoId, String shareCode);
}
