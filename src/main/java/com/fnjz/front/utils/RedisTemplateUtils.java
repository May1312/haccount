package com.fnjz.front.utils;

import com.alibaba.fastjson.JSON;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * 定义redisTemplate单例通用工具类型
 * redis key生成规则  蜂鸟id
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
    public void setCache (String user, int userInfoId){
        redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_LOGIN+ShareCodeUtil.id2sharecode(userInfoId), user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
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
    public void setAccountBookCache ( int userInfoId){
        UserAccountBookRestEntity task = userAccountBookRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
        if (task != null) {
            String userAccountBook = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + ShareCodeUtil.id2sharecode(task.getUserInfoId()), userAccountBook, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
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
     */
    public void cacheUserAndAccount (int userInfoId,String user) {
        //缓存账本
        this.setAccountBookCache(userInfoId);
        //缓存用户信息
        this.setCache(user,userInfoId);
    }

    /**
     * 从cache获取用户账本信息通用方法
     */
    public String getUseAccountCache(int userInfoId, String shareCode) {
        String user_account = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + shareCode);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user_account)) {
            UserAccountBookRestEntity task = userAccountBookRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            //设置redis缓存 缓存用户账本信息 30天
            String userAccount = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + shareCode, userAccount, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return userAccount;
        }
        return user_account;
    }

    /**
     * 获取我的页面 统计缓存
     * @param shareCode
     * @return
     */
    public String getMyCount(String shareCode){
        return (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_MY_COUNT + shareCode);
    }

    /**
     * 更新我的页面统计缓存
     * @param shareCode
     * @param myCount
     */
    public void updateMyCount(String shareCode,String myCount){
        redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + shareCode,myCount,RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 从缓存获取用户信息
     * @param key
     * @return
     */
    public UserLoginRestEntity getUserLoginRestEntityCache(String key){
        String user = this.getUserCache(key);
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
        return userLoginRestEntity;
    }

    /**
     * 更新用户缓存
     * @param userLoginRestEntity
     * @return
     */
    public void updateCacheSimple (UserLoginRestEntity userLoginRestEntity,String key){
        String user = JSON.toJSONString(userLoginRestEntity);
        this.updateCache(user, key);
    }
}
