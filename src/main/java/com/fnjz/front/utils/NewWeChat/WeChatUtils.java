package com.fnjz.front.utils.NewWeChat;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.RestTemplate.AppConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
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
        JSONObject jsonObject = restTemplate.customRestTemplate().getForObject(hurl,JSONObject.class);
        if (jsonObject.getString("errcode") != null) {
            //授权异常
            logger.error("wechat授权异常:"+jsonObject.getString("errmsg"));
            return null;
        }
        //根据openid获取用户信息
        String hur3 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + jsonObject.getString("access_token") + "&openid=" + jsonObject.getString("openid") + "";
        JSONObject jsonObject2 = restTemplate.customRestTemplate().getForObject(hur3,JSONObject.class);
        if (jsonObject2.getString("errcode") != null) {
            //授权异常
            logger.error("wechat授权异常:"+jsonObject2.getString("errmsg"));
            return null;
        }
        return jsonObject2;
    }
}
