package com.fnjz.back.service.impl.api.useraccountbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.api.useraccountbook.UserAccountBookServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("userAccountBookService")
@Transactional
public class UserAccountBookServiceImpl extends CommonServiceImpl implements UserAccountBookServiceI {
	
}