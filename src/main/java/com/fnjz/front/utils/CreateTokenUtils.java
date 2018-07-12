package com.fnjz.front.utils;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jeecgframework.jwt.def.JwtConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token生成工具类
 * Created by yhang on 2018/6/1.
 */
@Component
public class CreateTokenUtils {

    @Autowired
    private  RedisTemplateUtils redisTemplateUtils;

    public String createToken(String code) {
        //使用sharcode作为源token
        String token = Jwts.builder().setId(code).setSubject(code).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, JwtConstants.JWT_SECRET).compact();
        return token;
    }

    /**
     * 登录/注册成功-->返回token-->设置缓存
     * @return
     */
    public ResultBean loginSuccess(UserLoginRestEntity task , String code){
        Map<String, Object> map = new HashMap<>();
        String token = this.createToken(code);
        map.put("X-AUTH-TOKEN", token);
        map.put("expire", RedisPrefix.USER_EXPIRE_TIME);
        //设置账本+用户缓存
        String user = JSON.toJSONString(task);
        redisTemplateUtils.cacheUserAndAccount(task.getUserInfoId(),user);
        return new ResultBean(ApiResultType.OK,map);
    }

    public ResultBean wxappletLoginSuccess(UserLoginRestEntity task , String code){
        Map<String, Object> map = new HashMap<>();
        String token = this.createToken(code);
        map.put("token", token);
        map.put("expire", RedisPrefix.USER_EXPIRE_TIME);
        //设置账本+用户缓存
        String user = JSON.toJSONString(task);
        redisTemplateUtils.cacheUserAndAccount(task.getUserInfoId(),user);
        return new ResultBean(ApiResultType.OK,map);
    }

    /**
     * 小程序01034情况下  返回key
     * @param sessionKey
     * @return
     */
    public ResultBean returnKeyToWXApplet(String sessionKey){
        String sessionKeyPrefix = CommonUtils.getSessionKeyPrefix();
        redisTemplateUtils.cacheSessionKey(sessionKeyPrefix,sessionKey);
        Map<String,String> map = new HashMap();
        map.put("key",sessionKeyPrefix);
        return new ResultBean(ApiResultType.UNIONID_IS_NULL,map);
    }
}
