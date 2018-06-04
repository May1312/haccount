package com.fnjz.back.service.impl.operating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.operating.SpendTypeServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("spendTypeService")
@Transactional
public class SpendTypeServiceImpl extends CommonServiceImpl implements SpendTypeServiceI {
	
}