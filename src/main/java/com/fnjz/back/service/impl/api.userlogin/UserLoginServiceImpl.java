package com.fnjz.back.service.impl.api.userlogin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.api.userlogin.UserLoginServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userLoginService")
@Transactional
public class UserLoginServiceImpl extends CommonServiceImpl implements UserLoginServiceI {
	
}