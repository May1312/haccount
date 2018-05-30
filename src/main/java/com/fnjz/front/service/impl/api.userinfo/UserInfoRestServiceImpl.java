package com.fnjz.front.service.impl.api.userinfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userInfoRestService")
@Transactional
public class UserInfoRestServiceImpl extends CommonServiceImpl implements UserInfoRestServiceI {
	
}