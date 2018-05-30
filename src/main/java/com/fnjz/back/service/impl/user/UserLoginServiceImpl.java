package com.fnjz.back.service.impl.user;

import com.fnjz.back.service.user.UserLoginServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userLoginService")
@Transactional
public class UserLoginServiceImpl extends CommonServiceImpl implements UserLoginServiceI {
	
}