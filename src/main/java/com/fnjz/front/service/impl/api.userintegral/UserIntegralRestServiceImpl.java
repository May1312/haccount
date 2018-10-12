package com.fnjz.front.service.impl.api.userintegral;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userIntegralRestService")
@Transactional
public class UserIntegralRestServiceImpl extends CommonServiceImpl implements UserIntegralRestServiceI {

    @Override
    public void integral(String userInfoId, String shareCode) {

    }
}