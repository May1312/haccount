package com.fnjz.front.controller.api.push;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @Auther: yonghuizhao
 * @Date: 2018/11/9 17:11
 * @Description:
 */

public class JgPushExampls {

    private static final Logger LOG = Logger.getLogger(JgPushExampls.class);
    /**
     * 功能描述: 构建推送所需的 pushPayLoad  类
     *
     * @param: 推送内容，推送别名，跳转页面
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/14 11:54
     */
    public static PushPayload buildPushObject_android_and_ios(String alertContent, String alias, String jumpPage) {
        Map<String, String> extras = new HashMap<String, String>();
        extras.put("jumpPage", jumpPage);
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .setAlert(alertContent)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("蜂鸟记账")
                                .addExtras(extras).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtras(extras).build())
                        .build())
                .build();
    }

    /**
     * 功能描述: 根据别名推送指定人
     *
     * @param: appkey_masterSecret  哪个环境的key和秘钥
     * alias : 根据别名推送   多个  逗号分隔
     * alertContent ： 弹窗内容
     * jumpPage  : 点击跳转页面
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/14 11:53
     */
    public static void sendPushObject_android_and_ios(String alias, String alertContent, String jumpPage) {

        //根据运行环境获取key和秘钥
        PropertiesUtil propertiesUtil = new PropertiesUtil("jgpush.properties");
        String jg_app_key =String.valueOf(propertiesUtil.getProperties().get("JG_APP_KEY")) ;
        String JG_MASTER_SECRET =String.valueOf(propertiesUtil.getProperties().get("JG_MASTER_SECRET")) ;
        //获取客户端实例
        ClientConfig clientConfig = ClientConfig.getInstance();
        final JPushClient jpushClient = new JPushClient(JG_MASTER_SECRET, jg_app_key, null, clientConfig);
        //获取发送模板方式及信息内容
        PushPayload payload = buildPushObject_android_and_ios(alertContent, alias, jumpPage);
        try {
            //发送请求
            PushResult result = jpushClient.sendPush(payload);
            LOG.info("Got result - " + result);
            System.out.println(result);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            LOG.error("Sendno: " + payload.getSendno());

        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
            LOG.error("Sendno: " + payload.getSendno());
        } finally {
            //关闭客户端
            jpushClient.close();
        }
    }


}
