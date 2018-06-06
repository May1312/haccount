package com.fnjz.front.service.impl.api.incometype;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.incometype.IncomeTypeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("incomeTypeRestService")
@Transactional
public class IncomeTypeRestServiceImpl extends CommonServiceImpl implements IncomeTypeRestServiceI {
	
}