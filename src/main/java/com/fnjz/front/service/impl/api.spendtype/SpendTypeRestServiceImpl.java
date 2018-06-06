package com.fnjz.front.service.impl.api.spendtype;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.spendtype.SpendTypeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("spendTypeRestService")
@Transactional
public class SpendTypeRestServiceImpl extends CommonServiceImpl implements SpendTypeRestServiceI {
	
}