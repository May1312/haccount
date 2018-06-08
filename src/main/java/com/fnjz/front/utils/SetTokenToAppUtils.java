package com.fnjz.front.utils;

import com.fnjz.constants.RedisPrefix;

import java.util.Map;

/**
 * 封装map 返回token expire
 * Created by yhang on 2018/6/7.
 */
public class SetTokenToAppUtils {
    public static Map<String,Object> getTokenResult(Map<String,Object> map,String token){
        System.out.println("生成的token：" + token);
        map.put("X-AUTH-TOKEN", token);
        map.put("expire", RedisPrefix.USER_EXPIRE_TIME);
        return map;
    }
}
