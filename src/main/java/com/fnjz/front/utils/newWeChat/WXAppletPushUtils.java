package com.fnjz.front.utils.newWeChat;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.restTemplate.AppConfig;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 微信小程序服务推送
 * Created by yhang on 2018/11/27.
 */
@Component
public class WXAppletPushUtils {

    private static final Logger logger = Logger.getLogger(WXAppletPushUtils.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private AppConfig restTemplate;

    @Autowired
    private WXAppletUtils wxAppletUtils;

    /**
     * 移除成员
     */
    public final static String removeMemberId = "OFyL4qOVfddM5tsgvslEugsmimukPITIhXHSzJS_G_M";

    /**
     * 邀请成功
     */
    public final static String inviteFriendId = "IxvkgOzU5F8TXM7_EBc_J1guWk14M64g0TzX_M1-zFA";

    /**
     * 账单通知
     */
    public final static String accountNotifyId = "XKZJ-8QcULHJAeujplQiQmy_MpWxFUKXQMKsJjjqzIE";
    //账单统计页
    public final static String accountNotifyPage="pages/chart/index/main";
    //移除成员页
    public final static String removeMemberPage="pages/mine/index/main";
    //邀请成功页
    public final static String inviteFriendPage="pages/mine/index/main";
    /**
     * 推送
     *
     * @param templateId
     * @param openId
     */
    public void wxappletPush(String templateId, String openId, String formId,String page, WXAppletMessageBean bean) {
        //获取accessToken
        String accessToken = this.checkAccessToken();
        //发送消息
        if(StringUtils.isNotEmpty(accessToken)){
            sendPostMessage(accessToken, openId, templateId, page, formId, bean, "");
        }
    }

    /**
     * 获取accessToken
     *
     * @return
     */
    private String checkAccessToken() {
        String accessToken = redisTemplateUtils.getForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN);
        if (StringUtils.isEmpty(accessToken)) {
            //重新获取access token
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(wxAppletUtils.getAccessToken());
            if (jsonObject.get("errcode") != null) {
                logger.error("小程序 消息模板 服务通知:   ----获取access token异常-----");
                return null;
            }
            accessToken = jsonObject.getString("access_token");
            //缓存2小时
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN, accessToken, Long.valueOf(jsonObject.getString("expires_in")), TimeUnit.SECONDS);
        }
        return accessToken;
    }

    /**
     * @param accessToken
     * @param openId
     * @param templateId      所需下发的模板消息的id
     * @param page            点击模板卡片后的跳转页面
     * @param formId
     * @param emphasisKeyword 模板需要放大的关键词
     */
    private void sendPostMessage(String accessToken, String openId, String templateId, String page, String formId, WXAppletMessageBean bean, String emphasisKeyword) {
        String hurl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", openId);
        jsonObject.put("template_id", templateId);
        if (StringUtils.isNotEmpty(page)) {
            jsonObject.put("page", page);
        }
        jsonObject.put("form_id", formId);
        jsonObject.put("data", bean);
        if (StringUtils.isNotEmpty(emphasisKeyword)) {
            jsonObject.put("emphasis_keyword", emphasisKeyword);
        }
        JSONObject result = restTemplate.customRestTemplate().postForObject(hurl, jsonObject, com.alibaba.fastjson.JSONObject.class);
        logger.info("--------服务通知结果---------"+result.toString());
        //access token 失效
        if (StringUtils.equals(result.get("errcode") + "", "40001")) {
            //重新获取access token
            jsonObject = com.alibaba.fastjson.JSONObject.parseObject(wxAppletUtils.getAccessToken());
            accessToken =jsonObject.getString("access_token");
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN, accessToken, Long.valueOf(jsonObject.getString("expires_in")), TimeUnit.SECONDS);
            //重新调用
            sendPostMessage(accessToken, openId, templateId, page, formId, bean, emphasisKeyword);
        }
    }
}
