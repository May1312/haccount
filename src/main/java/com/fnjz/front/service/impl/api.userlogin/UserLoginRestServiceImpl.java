package com.fnjz.front.service.impl.api.userlogin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userLoginRestService")
@Transactional
public class UserLoginRestServiceImpl extends CommonServiceImpl implements UserLoginRestServiceI {
	
}