package com.fnjz.front.service.impl.appinfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.appinfo.AppVersionRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("appVersionRestService")
@Transactional
public class AppVersionRestServiceImpl extends CommonServiceImpl implements AppVersionRestServiceI {
	
}