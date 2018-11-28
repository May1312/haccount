package com.fnjz.front.utils;

import com.fnjz.constants.RedisPrefix;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
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
     * 推送
     * @param templateId
     * @param openId
     */
    public void wxappletPush(String templateId,String openId,String formId) throws IOException{
        //获取accessToken
        String accessToken = this.checkAccessToken();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("value","测试服务通知");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value",LocalDate.now().toString());
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("value","此事必有蹊跷");
        jsonObject.put("keyword1",jsonObject1);
        jsonObject.put("keyword2",jsonObject2);
        jsonObject.put("keyword3",jsonObject3);
        sendPostMessage(accessToken,openId,"GM-VzmyHmQtfHh4_YOWFDDJjnySksazVGVbDfcSel-k","",formId,jsonObject,"");
    }

    /**
     * 获取accessToken
     * @return
     */
    private String checkAccessToken(){
        String accessToken = redisTemplateUtils.getForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN);
        if(StringUtils.isEmpty(accessToken)){
            //重新获取access token
            String accessToken1 = WXAppletUtils.getAccessToken();
            JSONObject jsonObject = JSONObject.fromObject(accessToken1);
            accessToken = jsonObject.getString("access_token");
            //缓存2小时
            redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN,accessToken,7200L,TimeUnit.SECONDS);
        }else{
            //判断剩余时间间隔
            long expire = redisTemplateUtils.getExpire(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN);
            //判断缓存小于10分钟刷新缓存
            if(expire<=600){
                accessToken = WeChatUtils.getRefreshToken(accessToken);
                if(StringUtils.isEmpty(accessToken)){
                    accessToken = WXAppletUtils.getAccessToken();
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN,accessToken,7200L,TimeUnit.SECONDS);
                }else{
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN,accessToken,7200L,TimeUnit.SECONDS);
                }
            }
        }
        return accessToken;
    }

    /**
     *
     * @param accessToken
     * @param openId
     * @param templateId  所需下发的模板消息的id
     * @param page  点击模板卡片后的跳转页面
     * @param formId
     * @param data  模板内容
     * @param emphasisKeyword 模板需要放大的关键词
     */
    private void sendPostMessage(String accessToken,String openId,String templateId,String page,String formId,JSONObject data,String emphasisKeyword){
        String hurl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="+accessToken;
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
            //jsonObject.put("access_token", accessToken);
            jsonObject.put("touser", openId);
            jsonObject.put("template_id", templateId);
            if(StringUtils.isNotEmpty(page)){
                jsonObject.put("page", page);
            }
            jsonObject.put("form_id", formId);
            if(data!=null){

                jsonObject.put("data", data);
            }
            if(StringUtils.isNotEmpty(emphasisKeyword)){
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
            while((l=buffer.readLine())!=null){
                bs.append(l);
            }
            System.out.println(bs.toString());
        } catch (IOException e){
            logger.error(e.toString());
        }
    }
}
