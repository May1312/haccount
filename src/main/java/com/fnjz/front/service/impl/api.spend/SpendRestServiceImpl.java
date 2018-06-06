package com.fnjz.front.service.impl.api.spend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.spend.SpendRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("spendRestService")
@Transactional
public class SpendRestServiceImpl extends CommonServiceImpl implements SpendRestServiceI {
	
}