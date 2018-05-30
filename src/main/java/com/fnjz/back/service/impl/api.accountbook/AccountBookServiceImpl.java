package com.fnjz.back.service.impl.api.accountbook;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.api.accountbook.AccountBookServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("accountBookService")
@Transactional
public class AccountBookServiceImpl extends CommonServiceImpl implements AccountBookServiceI {
	
}