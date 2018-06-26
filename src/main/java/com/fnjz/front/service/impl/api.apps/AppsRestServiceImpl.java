package com.fnjz.front.service.impl.api.apps;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.apps.AppsRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("appsRestService")
@Transactional
public class AppsRestServiceImpl extends CommonServiceImpl implements AppsRestServiceI {
	
}