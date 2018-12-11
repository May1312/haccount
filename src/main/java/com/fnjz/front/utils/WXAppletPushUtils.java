package com.fnjz.front.utils;

import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.RedisPrefix;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
            String accessToken1 = WXAppletUtils.getAccessToken();
            JSONObject jsonObject = JSONObject.fromObject(accessToken1);
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
        try {
            URL url = new URL(hurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置本次请求的方式 ， 默认是GET方式， 参数要求都是大写字母
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //设置连接超时
            conn.setConnectTimeout(5000);
            //是否打开输入流 ， 此方法默认为true
            conn.setDoInput(true);
            //是否打开输出流， 此方法默认为false
            conn.setDoOutput(true);
            //表示连接
            conn.connect();
            //设置参数
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
            String param = jsonObject.toString();//转化成json
            //建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //中文解析异常
            dos.write(param.getBytes());
            dos.flush();
            dos.close();
            InputStream is = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            StringBuffer bs = new StringBuffer();
            String l = null;
            while ((l = buffer.readLine()) != null) {
                bs.append(l);
            }
            logger.info("--------服务通知结果---------"+bs.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
