package com.fnjz.service.impl.appinfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.service.appinfo.AppVersionServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("appVersionService")
@Transactional
public class AppVersionServiceImpl extends CommonServiceImpl implements AppVersionServiceI {
	
}