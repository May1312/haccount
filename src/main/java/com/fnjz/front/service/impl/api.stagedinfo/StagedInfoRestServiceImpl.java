package com.fnjz.front.service.impl.api.stagedinfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.stagedinfo.StagedInfoRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("stagedInfoRestService")
@Transactional
public class StagedInfoRestServiceImpl extends CommonServiceImpl implements StagedInfoRestServiceI {
	
}