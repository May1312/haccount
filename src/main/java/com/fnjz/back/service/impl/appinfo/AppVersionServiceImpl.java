package com.fnjz.back.service.impl.appinfo;

import com.fnjz.back.service.appinfo.AppVersionServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("appVersionService")
@Transactional
public class AppVersionServiceImpl extends CommonServiceImpl implements AppVersionServiceI {
	
}