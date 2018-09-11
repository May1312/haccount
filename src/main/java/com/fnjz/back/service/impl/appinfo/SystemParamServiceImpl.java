package com.fnjz.back.service.impl.appinfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.appinfo.SystemParamServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("systemParamService")
@Transactional
public class SystemParamServiceImpl extends CommonServiceImpl implements SystemParamServiceI {
	
}