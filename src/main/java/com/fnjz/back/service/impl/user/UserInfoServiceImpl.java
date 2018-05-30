package com.fnjz.back.service.impl.user;

import com.fnjz.back.service.user.UserInfoServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userInfoService")
@Transactional
public class UserInfoServiceImpl extends CommonServiceImpl implements UserInfoServiceI {
	
}