package com.fnjz.front.utils.NewWeChat;

import com.fnjz.front.RestTemplate.AppConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 微信小程序登录
 * Created by yhang on 2018/5/31.
 */
@Component
public class WXAppletUtils {

    private static final Logger logger = Logger.getLogger(WXAppletUtils.class);

    private String AppId;
    private String AppSecret;
    private String grant_type = "authorization_code";

    @PostConstruct
    public void init() {
        // 获取小程序配置参数
        Properties p = new Properties();
        InputStream in;
        in = WXAppletUtils.class.getResourceAsStream("/fnjz/wxapplet.properties");
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

    //获取openid session_key
    public String getUser(String code){
        String hurl = "https://api.weixin.qq.com/sns/jscode2session?appid="+AppId+"&secret="+AppSecret+"&js_code="+code+"&grant_type="+grant_type+"";
        return restTemplate.customRestTemplate().getForObject(hurl,String.class);
    }

    /**
     * 获取access_token
     * @return
     */
    public String getAccessToken(){
        String hurl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+AppId+"&secret="+AppSecret;
        return restTemplate.customRestTemplate().getForObject(hurl,String.class);
    }
}
