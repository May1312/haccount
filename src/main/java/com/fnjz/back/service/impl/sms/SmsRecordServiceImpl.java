package com.fnjz.back.service.impl.sms;

import com.fnjz.back.entity.sms.SmsRecordEntity;
import com.fnjz.back.service.sms.SmsRecordServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("smsRecordService")
@Transactional
public class SmsRecordServiceImpl extends CommonServiceImpl implements SmsRecordServiceI {

    @Override
    public void dySmsTimingSending() {
        String hql = "from SmsRecordEntity where sendstate = 'unsend' and  ";
        List<SmsRecordEntity> byQueryString = this.findByQueryString("");
    }
}