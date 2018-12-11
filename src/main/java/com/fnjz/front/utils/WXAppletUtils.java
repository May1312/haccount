package com.fnjz.front.utils;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * 微信小程序登录
 * Created by yhang on 2018/5/31.
 */
public class WXAppletUtils {

    private static final Logger logger = Logger.getLogger(WXAppletUtils.class);

    private static String AppId;
    private static String AppSecret;
    private static String grant_type = "authorization_code";

    static {
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
    //获取openid session_key
    public static String getUser(String code){
        String hurl = "https://api.weixin.qq.com/sns/jscode2session?appid="+AppId+"&secret="+AppSecret+"&js_code="+code+"&grant_type="+grant_type+"";
        try {
            URL url = new URL(hurl);
            HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
            //设置本次请求的方式 ， 默认是GET方式， 参数要求都是大写字母
            conn.setRequestMethod("GET");
            //设置连接超时
            conn.setConnectTimeout(5000);
            //是否打开输入流 ， 此方法默认为true
            conn.setDoInput(true);
            //是否打开输出流， 此方法默认为false
            conn.setDoOutput(true);
            //表示连接
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            StringBuffer bs = new StringBuffer();
            String l = null;
            while((l=buffer.readLine())!=null){
                bs.append(l);
            }
            return bs.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return null;
    }

    /**
     * 获取access_token
     * @return
     */
    public static String getAccessToken(){
        String hurl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+AppId+"&secret="+AppSecret;
        try {
            URL url = new URL(hurl);
            HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
            //设置本次请求的方式 ， 默认是GET方式， 参数要求都是大写字母
            conn.setRequestMethod("GET");
            //设置连接超时
            conn.setConnectTimeout(5000);
            //是否打开输入流 ， 此方法默认为true
            conn.setDoInput(true);
            //是否打开输出流， 此方法默认为false
            conn.setDoOutput(true);
            //表示连接
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            StringBuffer bs = new StringBuffer();
            String l = null;
            while((l=buffer.readLine())!=null){
                bs.append(l);
            }
            return bs.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return null;
    }

    @Test
    public void run(){
        getUser("023o6EiE1Izze10y6TjE1BbDiE1o6Eii");
    }
}
