package com.fnjz.back.service.impl.operating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.operating.IncomeTypeServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("incomeTypeService")
@Transactional
public class IncomeTypeServiceImpl extends CommonServiceImpl implements IncomeTypeServiceI {
	
}