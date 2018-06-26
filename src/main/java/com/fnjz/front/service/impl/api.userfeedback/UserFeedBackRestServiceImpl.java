package com.fnjz.front.service.impl.api.userfeedback;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.userfeedback.UserFeedBackRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userFeedBackRestService")
@Transactional
public class UserFeedBackRestServiceImpl extends CommonServiceImpl implements UserFeedBackRestServiceI {
	
}