package com.fnjz.front.service.impl.api.accountbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.accountbook.AccountBookRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("accountBookRestService")
@Transactional
public class AccountBookRestServiceImpl extends CommonServiceImpl implements AccountBookRestServiceI {
	
}