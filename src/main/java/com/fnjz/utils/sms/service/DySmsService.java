package com.fnjz.utils.sms.service;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;

import java.util.Date;

/**
 * 大鱼短信接口
 */
public interface DySmsService {

    /**
     * 单发
     * @param phoneNumbers   手机号
     * @param templateCode   模板id
     * @param templateParam  发送参数   "{\"code\":\"1234\"}"

     * @return  RequestId	String	8906582E-6722	请求ID
                Code	String	OK	状态码-返回OK代表请求成功,其他错误码详见错误码列表
                Message	String	请求成功	状态码的描述
                BizId	String	134523^4351232	发送回执ID,可根据该ID查询具体的发送状态
     */
    SendSmsResponse sendSms(String phoneNumbers,String templateCode,String templateParam) ;

    /**
     *
     * @param phoneNumberJson   手机号  "[\"15712878576\",\"15712878158\"]"
     * @param signNameJson      短信签名 "[\"蜂鸟记账\",\"蜂鸟记账\"]"
     * @param templateCode      短信模板id
     * @param templateParamJson 发送内容 "[{\"mtname\":\"Tom\", \"submittime\":\"123\"},{\"name\":\"mtname\", \"submittime\":\"456\"}]"
     * @return
     */
    SendBatchSmsResponse batchSendSms(String phoneNumberJson,String signNameJson,String templateCode,String templateParamJson);

    /**
     *
     * @param bizId         发送回执ID,可根据该ID查询具体的发送状态
     * @param phoneNumber   手机号
     * @param date          短信发送日期
     * @param page
     * @param pageSize
     * @return  RequestId	String	8906582E-6722	请求ID
                Code	String	OK	状态码-返回OK代表请求成功,其他错误码详见错误码列表
                Message	String	请求成功	状态码的描述
                TotalCount	Number	100	发送总条数
                smsSendDetailDTOs	Object	-	发送明细结构体
     */
    QuerySendDetailsResponse querySendDetails(String  bizId, String phoneNumber, Date date,Long page,Long pageSize);
}
