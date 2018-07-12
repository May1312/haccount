package com.fnjz.front.utils;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yhang on 2018/6/12.
 */
public class CommonUtils {

    /**
     * 单笔订单号生成规则   年月日+时间戳+5位随机数
     * @return
     */
    public static String getAccountOrder() {

        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();
        // 获取5位随机数
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
        // 当前时间+时间戳+随机数
        return str+System.currentTimeMillis()+rannum;
    }

    /**
     * session_key   时间戳+5位随机数
     * @return
     */
    public static String getSessionKeyPrefix() {

        Random random = new Random();
        // 获取5位随机数
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;
        // 时间戳+随机数
        return System.currentTimeMillis()+""+rannum;
    }

    /**
     * 手势查询结果返回集封装
     * @param userLoginRestEntity
     * @return
     */
    public static ResultBean returnGesture(UserLoginRestEntity userLoginRestEntity){
        Map<String, String> map = new HashMap<>();
        map.put("gesturePwType", userLoginRestEntity.getGesturePwType());
        map.put("gesturePw", userLoginRestEntity.getGesturePw());
        return new ResultBean(ApiResultType.OK,map);
    }

    /**
     * qiniu token结果返回集封装
     * @param upToken
     * @return
     */
    public static ResultBean returnQiNiuAuth(String upToken){
        Map<String, String> map = new HashMap<>();
        map.put("auth",upToken);
        return new ResultBean(ApiResultType.OK,map);
    }

    /**
     * 记账成功结果返回集封装
     * @param id
     * @return
     */
    public static ResultBean returnCharge(String id){
        Map<String, String> map = new HashMap<>();
        map.put("id",id);
        return new ResultBean(ApiResultType.OK,map);
    }
}
