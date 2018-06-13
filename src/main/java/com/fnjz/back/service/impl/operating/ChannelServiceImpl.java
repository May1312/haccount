package com.fnjz.back.service.impl.operating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.operating.ChannelServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("channelService")
@Transactional
public class ChannelServiceImpl extends CommonServiceImpl implements ChannelServiceI {
	
}