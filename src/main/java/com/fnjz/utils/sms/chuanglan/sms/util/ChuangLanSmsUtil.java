package com.fnjz.utils.sms.chuanglan.sms.util;

import com.alibaba.fastjson.JSON;
import com.fnjz.utils.sms.chuanglan.sms.request.SmsSendRequest;
import com.fnjz.utils.sms.chuanglan.sms.response.SmsSendResponse;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author tianyh
 * @Description:HTTP 请求
 */
public class ChuangLanSmsUtil {

    //发送短信请求url
    private final static String URL = "https://smssh1.253.com/msg/send/json";
    //创蓝账户名
    private final static String account = "N6027302";
    //密码
    private final static String password = "hmfL6laW7re440";
    /**
     * 发送验证码方法
     */
    public static SmsSendResponse sendSmsByPost(String verifyCode, String msg, String phone, boolean report) {
        //赋值验证码进msg
        msg = StringUtils.replace(msg,"{s}",verifyCode);
        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, password, msg, phone,report+"");
        String requestJson = JSON.toJSONString(smsSingleRequest);
        URL url = null;
        try {
            url = new URL(URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

//			PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
//			printWriter.write(postContent);
//			printWriter.flush();

            httpURLConnection.connect();
            OutputStream os=httpURLConnection.getOutputStream();
            os.write(requestJson.getBytes("UTF-8"));
            os.flush();

            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                // 开始获取数据
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                String response = sb.toString();
                return JSON.parseObject(response, SmsSendResponse.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
