package com.fnjz.back.service.impl.api.userinfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.api.userinfo.UserInfoServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userInfoService")
@Transactional
public class UserInfoServiceImpl extends CommonServiceImpl implements UserInfoServiceI {
	
}