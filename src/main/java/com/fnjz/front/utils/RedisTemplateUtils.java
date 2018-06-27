package com.fnjz.front.utils;

import com.alibaba.fastjson.JSON;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * 定义redisTemplate单例通用工具类型
 * redis key生成规则  _时间戳
 * Created by yhang on 2018/6/26.
 */

public class RedisTemplateUtils {

    private RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        //设置redis key以字符串显示
        redisTemplate.setKeySerializer(new StringRedisSerializer());
    }

    @Autowired
    private UserAccountBookRestServiceI userAccountBookRestServiceI;

    /**
     * 设置redis用户缓存通用方法
     */
    public void setCache (String user, String code,long time){
        redisTemplate.opsForValue().set(code+"_"+time, user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 设置redis用户缓存通用方法
     */
    public void updateCache (String user, String code){
        redisTemplate.opsForValue().set(code, user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 通用方法  用户登录之后缓存用户---账本关系表
     */
    public void setAccountBookCache ( int userInfoId, String code,long time){
        UserAccountBookRestEntity task = userAccountBookRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
        if (task != null) {
            String userAccountBook = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code+"_"+time, userAccountBook, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        }
    }

    /**
     * 小程序注册缓存session key
     * @param sessionKeyPrefix
     * @param session_key
     */
    public void cacheSessionKey (String sessionKeyPrefix,String session_key){
        redisTemplate.opsForValue().set(RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY + sessionKeyPrefix, session_key, RedisPrefix.SESSION_KEY_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取session key
     * @param key
     */
    public String getSessionKey (String key) {
        return (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY + key);
    }

    /**
     * 删除缓存
     * @param key
     */
    public void deleteKey (String key) {
        redisTemplate.delete(key);
    }

    /**
     * 从cache获取用户信息
     */
    public String getUserCache (String code){

        String user = (String) redisTemplate.opsForValue().get(code);
        /*//校验 微信登录用户但是已绑定手机号情况
        if (StringUtils.isEmpty(user)&&!ValidateUtils.isMobile(code)) {
            //union 用户
            UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", code);
            if(task2.getMobile()!=null){
                user = (String) redisTemplate.opsForValue().get(task2.getMobile());
            }
        }
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user)) {
            UserLoginRestEntity task;
            //判断code类型
            if (ValidateUtils.isMobile(code)) {
                task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", code);
            } else {
                task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", code);
            }
            //设置redis缓存 缓存用户信息 30天 毫秒
            String r_user = JSON.toJSONString(task);
            if(StringUtils.isNotEmpty(r_user)){
                if (StringUtils.isNotEmpty(task.getMobile())) {
                    updateCache(r_user, task.getMobile());
                }else{
                    updateCache(r_user, task.getWechatAuth());
                }
            }
            return r_user;
        }*/
        return user;
    }

    /**
     * 获取验证码
     * @param key
     */
    public String getVerifyCode (String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存用户及账本信息
     * @param userInfoId
     * @param user
     * @param code
     * @param time
     */
    public void cacheUserAndAccount (int userInfoId,String user,String code,long time) {
        //缓存账本
        this.setAccountBookCache(userInfoId, code,time);
        //缓存用户信息
        this.setCache(user,code,time);
    }

    /**
     * 从cache获取用户账本信息通用方法
     */
    public String getUseAccountCache(int userInfoId, String code) {
        String user_account = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user_account)) {
            UserAccountBookRestEntity task = userAccountBookRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            //设置redis缓存 缓存用户账本信息 30天
            String r_user_account = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code, r_user_account, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return r_user_account;
        }
        return user_account;
    }
}
