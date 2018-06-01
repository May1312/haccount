package com.fnjz.back.service.impl.sms;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.back.service.sms.SmsRecordInfoServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("smsRecordInfoService")
@Transactional
public class SmsRecordInfoServiceImpl extends CommonServiceImpl implements SmsRecordInfoServiceI {
	
}