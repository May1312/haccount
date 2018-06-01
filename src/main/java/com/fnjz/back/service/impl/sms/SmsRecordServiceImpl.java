package com.fnjz.back.service.impl.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.fnjz.back.entity.sms.SmsRecordEntity;
import com.fnjz.back.entity.sms.SmsRecordInfoEntity;
import com.fnjz.back.entity.user.UserInfoEntity;
import com.fnjz.back.service.sms.SmsRecordInfoServiceI;
import com.fnjz.back.service.sms.SmsRecordServiceI;
import com.fnjz.back.service.user.UserInfoServiceI;
import com.fnjz.utils.sms.DySms;
import net.sf.json.JSONArray;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.util.DateUtils;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service("smsRecordService")
@Transactional
public class SmsRecordServiceImpl extends CommonServiceImpl implements SmsRecordServiceI {

    @Autowired
    private SmsRecordInfoServiceI smsRecordInfoService;
    @Autowired
    private UserInfoServiceI userInfoService;

    @Override
    public void dySmsTimingSending() {
        String phoneNumbers = "";
        String templateParams = "";
        String signName = "蜂鸟记账";
        String sendState = "";
        String hql = "from SmsRecordEntity where sendstate = 'unsend' order by sendTime asc ";
        List<SmsRecordEntity> timingSendList = this.findByQueryString(hql);
        for (SmsRecordEntity smsRecordEntity : timingSendList) {

            HashMap<String, String> templateParamsMap = new HashMap<>();
            ArrayList<String> SignNameList = new ArrayList<>();

            String str = DateUtils.compateDate(smsRecordEntity.getSendtime(), DateUtils.TimeComputation());

            String[] split = null;
            List<String> userInfoMobileList = new ArrayList<>();
            if (str != "after") {//发

                if (StringUtil.isEmpty(smsRecordEntity.getSendmobile())) {//按终端发送
                    String userInfoHql = "from UserInfoEntity where mobileSystem = ? ";
                    List<UserInfoEntity> userinflList = userInfoService.findHql(userInfoHql, smsRecordEntity.getTerminaltype());


                    for (UserInfoEntity userInfoEntity : userinflList) {

                        userInfoMobileList.add(userInfoEntity.getMobile());

                        SignNameList.add(signName);
                        templateParams += createtemplateParams();
                    }
                    phoneNumbers = ObjectToJsonStr(userInfoMobileList);

                } else {//按手机号
                    split = smsRecordEntity.getSendmobile().split(",");
                    phoneNumbers = ObjectToJsonStr(split);
                    for (String s : split) {
                        SignNameList.add(signName);
                        templateParams += createtemplateParams();
                    }
                }
                signName = ObjectToJsonStr(SignNameList);
                String templateCode = smsRecordEntity.getSendTemplateCode();
                SendBatchSmsResponse sendBatchSmsResponse = DySms.batchSendSms(phoneNumbers, signName, templateCode, templateParams.replace("][", ","));
                if (sendBatchSmsResponse.getCode().equalsIgnoreCase("ok")) {//发送成功更改状态
                    smsRecordEntity.setSendstate("hasSend");
                    smsRecordEntity.setReturntime(DateUtils.TimeComputation());
                    this.saveOrUpdate(smsRecordEntity);
                    sendState = "success";
                } else {
                    sendState = "fail";
                }
                //生成发送记录
                if (split != null && split.length > 0) {
                    userInfoMobileList = Arrays.asList(split);
                }
                createSmsRecordInfo(userInfoMobileList, smsRecordEntity, sendState);

            }


        }
    }

    public String createtemplateParams() {
        HashMap<String, String> templateParamsMap = new HashMap<>();
        templateParamsMap.put("userName", "");
        JSONArray json = JSONArray.fromObject(templateParamsMap);
        String templateParams = json.toString();
        return templateParams;
    }

    public String ObjectToJsonStr(Object o) {
        JSONArray jsonArray = JSONArray.fromObject(o);
        String str = jsonArray.toString();
        return str;
    }

    public void createSmsRecordInfo(List<String> split, SmsRecordEntity smsRecordEntity, String sendState) {
        for (String s : split) {
            SmsRecordInfoEntity smsRecordInfoEntity = new SmsRecordInfoEntity();
            smsRecordInfoEntity.setSendmobile(s);
            smsRecordInfoEntity.setSendstate(sendState);
            smsRecordInfoEntity.setTerminaltype(smsRecordEntity.getTerminaltype());
            smsRecordInfoEntity.setSendtemplatecode(smsRecordEntity.getSendTemplateCode());
            smsRecordInfoEntity.setSendtime(smsRecordEntity.getSendtime());
            smsRecordInfoEntity.setReturntime(DateUtils.TimeComputation());
            smsRecordInfoEntity.setSmsrecordid(smsRecordEntity.getId());
            smsRecordInfoService.save(smsRecordInfoEntity);
        }
    }
}