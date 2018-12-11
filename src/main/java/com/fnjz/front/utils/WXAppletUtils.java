package com.fnjz.front.utils;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.*;
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

    /**
     * 获取微信二维码
     * @return
     */
    public static byte[] getWXACode(String accessToken,String shareCode){
        String hurl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken;
        try {
            URL url = new URL(hurl);
            HttpURLConnection  conn = (HttpURLConnection) url.openConnection();
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
            jsonObject.put("is_hyaline",false);
            //jsonObject.put("page","pages/details/index/main");
            jsonObject.put("page","pages/eventpage/lovemoneyduck/registerpage/main");
            jsonObject.put("width",280);
            //jsonObject.put("scene",shareCode);
            jsonObject.put("loveMoneyDuckInviteCode",shareCode);
            String param =JSONObject.fromObject(jsonObject).toString();//转化成json
            //建立输入流，向指向的URL传入参数
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(param);
            dos.flush();
            dos.close();

            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);
            return btImg;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return null;
    }
    private static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();

    }

    @Test
    public void run(){
        getUser("023o6EiE1Izze10y6TjE1BbDiE1o6Eii");
    }
}
