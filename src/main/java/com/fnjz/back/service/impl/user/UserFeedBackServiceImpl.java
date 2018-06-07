package com.fnjz.back.service.impl.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.user.UserFeedBackServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userFeedBackService")
@Transactional
public class UserFeedBackServiceImpl extends CommonServiceImpl implements UserFeedBackServiceI {
	
}