package com.fnjz.front.service.impl.api.personalincometype;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.personalincometype.PersonalIncomeTypeRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("personalIncomeTypeRestService")
@Transactional
public class PersonalIncomeTypeRestServiceImpl extends CommonServiceImpl implements PersonalIncomeTypeRestServiceI {
	
}