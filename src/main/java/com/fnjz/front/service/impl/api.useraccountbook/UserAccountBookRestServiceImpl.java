package com.fnjz.front.service.impl.api.useraccountbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userAccountBookRestService")
@Transactional
public class UserAccountBookRestServiceImpl extends CommonServiceImpl implements UserAccountBookRestServiceI {
	
}