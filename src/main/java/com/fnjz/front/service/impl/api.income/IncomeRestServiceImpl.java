package com.fnjz.front.service.impl.api.income;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.income.IncomeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("incomeRestService")
@Transactional
public class IncomeRestServiceImpl extends CommonServiceImpl implements IncomeRestServiceI {
	
}