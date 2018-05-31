package com.fnjz.front.utils;

import com.alibaba.fastjson.JSONObject;
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
 * app微信授权登录
 * Created by yhang on 2018/5/31.
 */
public class WeChatUtils {
    private static String AppId;
    private static String AppSecret;
    private static String grant_type = "authorization_code";
    static {
        // 获取小程序配置参数
        Properties p = new Properties();
        InputStream in;
        in = WXAppletUtils.class.getResourceAsStream("/fnjz/wechat.properties");
        try {
            p.load(in);
            AppId = p.getProperty("appId", "");
            AppSecret = p.getProperty("appSecret", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取用户信息
    public static String getUser(String code){
        String hurl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+AppId+"&secret="+AppSecret+"&code="+code+"&grant_type="+grant_type+"";
        try {
            URL url = new URL(hurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");   //设置本次请求的方式 ， 默认是GET方式， 参数要求都是大写字母
            conn.setConnectTimeout(5000);//设置连接超时
            conn.setDoInput(true);//是否打开输入流 ， 此方法默认为true
            conn.setDoOutput(true);//是否打开输出流， 此方法默认为false
            conn.connect();//表示连接
            InputStream is = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            StringBuffer bs = new StringBuffer();
            String l = null;
            while((l=buffer.readLine())!=null){
                bs.append(l);
            }
            System.out.println(bs.toString());
            //转换成json对象
            JSONObject jsonObject = JSONObject.parseObject(bs.toString());
            if(jsonObject.getString("errcode")!=null){
                //授权异常
                return null;
            }
            //刷新refresh_token  生效时间30天
            String hur2 = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+AppId+"&grant_type=refresh_token&refresh_token="+jsonObject.getString("refresh_token")+"";
            //查看授权作用域
            //openid----->判断是否首次登录！！ 判断作用域范围？？？
            //if(jsonObject.getString("scope")){

            //}
            return bs.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } ;
        return null;
    }
    @Test
    public void run(){
        getUser("023o6EiE1Izze10y6TjE1BbDiE1o6Eii");
    }
}
