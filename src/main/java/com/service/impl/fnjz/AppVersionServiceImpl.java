package com.service.impl.fnjz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.service.fnjz.AppVersionServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("appVersionService")
@Transactional
public class AppVersionServiceImpl extends CommonServiceImpl implements AppVersionServiceI {
	
}