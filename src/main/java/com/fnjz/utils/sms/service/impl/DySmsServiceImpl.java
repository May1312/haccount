package com.fnjz.utils.sms.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fnjz.utils.sms.service.DySmsService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service("dySmsService")
public class DySmsServiceImpl implements DySmsService {

    static final String product = "Dysmsapi";
    static final String domain = "dysmsapi.aliyuncs.com";
    static final String accessKeyId = "LTAIBzBb8yhwteYF";
    static final String accessKeySecret = "JMc1o5ETjuYgqEdyiFZ3oAtM7bDM3Y";
    static final String signName = "蜂鸟记账";

    /**
     * 单发
     *
     * @param phoneNumbers  手机号
     * @param templateCode  模板id
     * @param templateParam 发送参数   "{\"code\":\"1234\"}"
     * @return RequestId    String	8906582E-6722	请求ID
     * Code	String	OK	状态码-返回OK代表请求成功,其他错误码详见错误码列表
     * Message	String	请求成功	状态码的描述
     * BizId	String	134523^4351232	发送回执ID,可根据该ID查询具体的发送状态
     */
    @Override
    public SendSmsResponse sendSms(String phoneNumbers, String templateCode, String templateParam)  {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        SendSmsResponse sendSmsResponse =null;
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumbers);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam(templateParam);
            //hint 此处可能会抛出异常，注意catch
            sendSmsResponse = acsClient.getAcsResponse(request);

            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {//请求成功
                System.out.println("result===="+"请求成功");
            }else {
                System.out.println("result===="+"请求失败");
            }

        } catch (ClientException e) {
            e.printStackTrace();
        }

        return sendSmsResponse;

    }

    /**
     * @param phoneNumberJson   手机号  "[\"15712878576\",\"15712878158\"]"
     * @param signNameJson      短信签名 "[\"蜂鸟记账\",\"蜂鸟记账\"]"
     * @param templateCode      短信模板id
     * @param templateParamJson 发送内容 "[{\"mtname\":\"Tom\", \"submittime\":\"123\"},{\"name\":\"mtname\", \"submittime\":\"456\"}]"
     * @return
     */
    @Override
    public SendBatchSmsResponse batchSendSms(String phoneNumberJson, String signNameJson, String templateCode, String templateParamJson) {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        SendBatchSmsResponse sendSmsResponse =null;
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,accessKeySecret);

        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendBatchSmsRequest request = new SendBatchSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持JSON格式的批量调用，批量上限为100个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumberJson(phoneNumberJson);
            //必填:短信签名-支持不同的号码发送不同的短信签名
            request.setSignNameJson(signNameJson);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //必填:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParamJson(templateParamJson);
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCodeJson("[\"90997\",\"90998\"]");
            //请求失败这里会抛ClientException异常
            sendSmsResponse = acsClient.getAcsResponse(request);

        } catch (ClientException e) {
            e.printStackTrace();
        }

        return sendSmsResponse;

    }

    /**
     * @param bizId 发送回执ID,可根据该ID查询具体的发送状态
     * @return
     */
    @Override
    public QuerySendDetailsResponse querySendDetails(String bizId ,String phoneNumber, Date date,Long page,Long pageSize) {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        QuerySendDetailsResponse querySendDetailsResponse =null;
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            QuerySendDetailsRequest request = new QuerySendDetailsRequest();
            //必填-号码
            request.setPhoneNumber(phoneNumber);
            //可选-流水号
            request.setBizId(bizId);
            //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            request.setSendDate(ft.format(date));
            //必填-页大小
            request.setPageSize(pageSize);
            //必填-当前页码从1开始计数
            request.setCurrentPage(page);

            //hint 此处可能会抛出异常，注意catch
            querySendDetailsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return querySendDetailsResponse;
    }
}
