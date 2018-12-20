package com.fnjz.front.service.impl.api.userwxqrcode;

import com.fnjz.constants.DomainEnum;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.UserWXQrCodeRestDao;
import com.fnjz.front.service.api.userwxqrcode.UserWXQrCodeRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import com.fnjz.front.utils.WXAppletUtils;
import com.fnjz.utils.upload.QiNiuUploadFileUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service("userWXQrCodeService")
@Transactional
public class UserWXQrCodeRestServiceImpl extends CommonServiceImpl implements UserWXQrCodeRestServiceI {

    private static final Logger logger = Logger.getLogger(UserWXQrCodeRestServiceImpl.class);

    @Autowired
    private UserWXQrCodeRestDao userWXQrCodeRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 插入用户-邀请码绑定关系
     * @param userInfoId
     * @param url
     */
    @Override
    public void insert(String userInfoId, String url,String type) {
        userWXQrCodeRestDao.insert(userInfoId,url,type);
    }

    @Override
    public String getInviteQrCode(String userInfoId,String type) {
        String url= userWXQrCodeRestDao.getInviteQrCode(userInfoId,type);
        if (StringUtils.isNotEmpty(url)) {
            return url;
        } else {
            String accessToken = checkAccessToken();
            byte[] result = getWXACode(accessToken, ShareCodeUtil.id2sharecode(Integer.valueOf(userInfoId)),type);
            if (result != null) {
                //上传七牛云
                url = new QiNiuUploadFileUtils().bytesUpload(DomainEnum.WXAPPLET_QR_CODE_DOMAIN.getDomainUrl(), result, DomainEnum.WXAPPLET_QR_CODE_DOMAIN.getDomainName(), "wxqrcode_" + CommonUtils.getAccountOrder());
                this.insert(userInfoId, url,type);
                return url;
            } else {
                //人格测试二维码获取不到情况下返回url
                return "https://head.image.fengniaojizhang.cn/loveMoneyDefaultPage.jpg";
            }
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
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(accessToken1);
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
     * 获取微信二维码
     *
     * @return
     */
    private byte[] getWXACode(String accessToken, String shareCode,String type) {
        String hurl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
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
            jsonObject.put("is_hyaline", false);
            if(StringUtils.equals("2",type)){
                jsonObject.put("page", "pages/details/index/main");
            }else if(StringUtils.equals("1",type)){
                jsonObject.put("page","pages/eventpage/lovemoneyduck/registerpage/main");
            }
            jsonObject.put("width", 280);
            jsonObject.put("scene", shareCode);
            String param = JSONObject.fromObject(jsonObject).toString();//转化成json
            //建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(param);
            dos.flush();
            dos.close();
            // 通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            byte[] btImg = readInputStream(inStream);
            //==========新增部分===========
            try {
                JSONObject jsonObject1 = JSONObject.fromObject(new String(btImg));
                if (jsonObject1.get("errcode") != null) {
                    //access token 失效
                    if (StringUtils.equals(jsonObject1.get("errcode") + "", "40001")) {
                        //重新获取access token
                        jsonObject1 = JSONObject.fromObject(WXAppletUtils.getAccessToken());
                        accessToken =jsonObject1.getString("access_token");
                        redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_ACCESS_TOKEN, accessToken, Long.valueOf(jsonObject1.getString("expires_in")), TimeUnit.SECONDS);
                        //重新调用
                        return getWXACode(accessToken, shareCode,type);
                    }
                    logger.error("获取小程序二维码异常:" + jsonObject1.getString("errmsg"));
                    return null;
                }
            } catch (Exception e) {
                return btImg;
            }
            return btImg;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e.toString());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
            return null;
        }
    }

    private byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        return outStream.toByteArray();
    }
}