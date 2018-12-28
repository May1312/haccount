package com.fnjz.front.utils.newWeChat;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.restTemplate.AppConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * app微信授权登录
 * Created by yhang on 2018/5/31.
 */
@Component
public class WeChatUtils {

    private static final Logger logger = Logger.getLogger(WeChatUtils.class);

    private String AppId;
    private String AppSecret;
    private String grant_type = "authorization_code";

    @PostConstruct
    public void init() {
        Properties p = new Properties();
        InputStream in;
        in = WXAppletUtils.class.getResourceAsStream("/fnjz/wechat.properties");
        try {
            p.load(in);
            AppId = p.getProperty("appId", "");
            AppSecret = p.getProperty("appSecret", "");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    @Autowired
    private AppConfig restTemplate;

    //获取用户信息
    public JSONObject getUser(String code) {
        String hurl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + AppId + "&secret=" + AppSecret + "&code=" + code + "&grant_type=" + grant_type + "";
        String result = restTemplate.customRestTemplate().getForObject(hurl, String.class);
        try {
            result = new String(result.getBytes("iso-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            //授权异常
            logger.error("wechat中文解码异常:" + e.toString());
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getString("errcode") != null) {
            //授权异常
            logger.error("wechat授权异常:" + jsonObject.getString("errmsg"));
            return null;
        }
        //根据openid获取用户信息
        String hur3 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + jsonObject.getString("access_token") + "&openid=" + jsonObject.getString("openid") + "";
        String result2 = restTemplate.customRestTemplate().getForObject(hur3, String.class);
        try {
            result2 = new String(result2.getBytes("iso-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("wechat中文解码异常:" + e.toString());
            return null;
        }
        JSONObject jsonObject2 = JSONObject.parseObject(result2);
        if (jsonObject2.getString("errcode") != null) {
            //授权异常
            logger.error("wechat授权异常:" + jsonObject2.getString("errmsg"));
            return null;
        }
        return jsonObject2;
    }
}
