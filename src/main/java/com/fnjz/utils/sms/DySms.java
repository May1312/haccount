package com.fnjz.utils.sms;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.fnjz.utils.sms.service.impl.DySmsServiceImpl;

import java.util.Date;

public class DySms {

    public static SendSmsResponse sendSms(String phoneNumbers,String templateCode,String templateParam){
        DySmsServiceImpl dySmsService = new DySmsServiceImpl();
        SendSmsResponse sendSmsResponse = dySmsService.sendSms(phoneNumbers, templateCode, templateParam);
        return sendSmsResponse;
    }

    public static SendBatchSmsResponse batchSendSms(String phoneNumberJson, String signNameJson, String templateCode, String templateParamJson){
        DySmsServiceImpl dySmsService = new DySmsServiceImpl();
        SendBatchSmsResponse sendBatchSmsResponse = dySmsService.batchSendSms(phoneNumberJson, signNameJson, templateCode, templateParamJson);
        return  sendBatchSmsResponse;
    }

    public static QuerySendDetailsResponse querySendDetails(String  bizId, String phoneNumber, Date date, Long page, Long pageSize){
        DySmsServiceImpl dySmsService = new DySmsServiceImpl();
        QuerySendDetailsResponse querySendDetailsResponse = dySmsService.querySendDetails(bizId, phoneNumber, date, page, pageSize);
        return querySendDetailsResponse;
    }
}
