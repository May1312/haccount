package com.fnjz.front.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    }

    @Autowired
    private UserAccountBookRestServiceI userAccountBookRestServiceI;

    /**
     * 设置redis用户缓存通用方法
     */
    public void setCache(String user, int userInfoId) {
        redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_LOGIN + ShareCodeUtil.id2sharecode(userInfoId), user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 设置redis用户缓存通用方法
     */
    public void updateCache(String user, String code) {
        redisTemplate.opsForValue().set(code, user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 通用方法  用户登录之后缓存用户---账本关系表
     */
    public void setAccountBookCache(int userInfoId) {
        UserAccountBookRestEntity task = userAccountBookRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
        if (task != null) {
            String userAccountBook = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + ShareCodeUtil.id2sharecode(task.getUserInfoId()), userAccountBook, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        }
    }

    /**
     * 小程序注册缓存session key
     *
     * @param sessionKeyPrefix
     * @param session_key
     */
    public void cacheSessionKey(String sessionKeyPrefix, String session_key) {
        redisTemplate.opsForValue().set(RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY + sessionKeyPrefix, session_key, RedisPrefix.SESSION_KEY_TIME, TimeUnit.DAYS);
    }

    /**
     * 获取session key
     *
     * @param key
     */
    public String getSessionKey(String key) {
        return (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY + key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除hash 字段
     * @param hash
     * @param field
     */
    public void deleteHashKey(String hash,String ... field) {
        redisTemplate.opsForHash().delete(hash,field);
    }

    /**
     * 从cache获取用户信息
     */
    public String getUserCache(String code) {
        String user = (String) redisTemplate.opsForValue().get(code);
        return user;
    }

    /**
     * 获取验证码
     *
     * @param key
     */
    public String getVerifyCode(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存验证码
     *
     * @param key
     */
    public void cacheVerifyCode(String key, String random) {
        redisTemplate.opsForValue().set(key, random, RedisPrefix.VERIFYCODE_VALID_TIME, TimeUnit.MINUTES);
    }

    /**
     * 缓存用户及账本信息
     *
     * @param userInfoId
     * @param user
     */
    public void cacheUserAndAccount(int userInfoId, String user) {
        //缓存账本
        //this.setAccountBookCache(userInfoId);
        //缓存用户信息
        this.setCache(user, userInfoId);
    }

    /**
     * 从cache获取用户账本信息通用方法
     */
    public String getUseAccountCache(int userInfoId, String shareCode) {
        String userAccount = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + shareCode);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(userAccount)) {
            UserAccountBookRestEntity task = userAccountBookRestServiceI.getUserAccountBookByUserInfoId(userInfoId);
            String userAccount2 = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + shareCode, userAccount2, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return userAccount2;
        }
        return userAccount;
    }

    public UserAccountBookRestEntity getUserAccountBookRestEntityCache(int userInfoId, String shareCode) {
        String userAccount = this.getUseAccountCache(userInfoId, shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(userAccount, UserAccountBookRestEntity.class);
        return userAccountBookRestEntity;
    }

    /**
     * 获取我的页面 统计缓存
     *Map 不能指定泛型  increment会异常
     * @param shareCode
     * @return
     */
    public Map getMyCount(String shareCode) {
        return redisTemplate.opsForHash().entries(RedisPrefix.PREFIX_MY_COUNT + shareCode);
    }

    /**
     * 获取剩余缓存时间
     * @param key
     * @return
     */
    public Long getExpireForSeconds(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取key的过期时间
     * @param key
     * @return
     */
    public Map getForHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 更新我的页面统计缓存
     * Map类型不能指定泛型 否则increment执行异常
     * @param shareCode
     * @param myCount
     */
    public void updateMyCount(String shareCode, Map myCount) {
        redisTemplate.opsForHash().putAll(RedisPrefix.PREFIX_MY_COUNT + shareCode, myCount);
        //设置缓存时间
        redisTemplate.expire(RedisPrefix.PREFIX_MY_COUNT + shareCode, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 更新hash类型 数据
     * @param key
     * @param map
     */
    public void updateForHash(String key, Map map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 更新hash类型 数据 并设置缓存时间
     * @param key
     * @param map
     */
    public void updateForHash(String key, Map map,Long days) {
        redisTemplate.opsForHash().putAll(key, map);
        if(days!=null){
            //设置缓存时间
            redisTemplate.expire(key, days, TimeUnit.DAYS);
        }
    }

    /**
     * 更新map中的指定key
     * @param key
     * @param mapKey
     */
    public void updateForHashKey(String key,String mapKey, int value) {
        redisTemplate.opsForHash().put(key,mapKey, value);
    }

    /**
     * 获取map中的指定key的value
     * @param key
     * @param mapKey
     */
    public int getForHashKey(String key,String mapKey) {
        return Integer.valueOf(redisTemplate.opsForHash().get(key,mapKey)+"");
    }

    public Object getForHashKeyObject(String key,String mapKey) {
        return redisTemplate.opsForHash().get(key,mapKey);
    }

    public void cacheForString(String key, String list) {
        redisTemplate.opsForValue().set(key,list);
    }

    public void cacheForString(String key, String list,Long days) {
        redisTemplate.opsForValue().set(key,list,days,TimeUnit.DAYS);
    }

    public void cacheForString(String key, String list,Long seconds,TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,list,seconds,timeUnit);
    }

    public String getForString(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * 记账总笔数 1为递增  2 为递减
     *
     * @param shareCode
     * @param file
     * @param flag
     */
    public void incrementMyCountTotal(String shareCode, String file, int flag) {
        if (1 == flag) {
            redisTemplate.opsForHash().increment(RedisPrefix.PREFIX_MY_COUNT + shareCode, file, 1);
        } else {
            redisTemplate.opsForHash().increment(RedisPrefix.PREFIX_MY_COUNT + shareCode, file, -1);
        }
    }

    public void incrementForHash(String key, String file,int num) {
        redisTemplate.opsForHash().increment(key,file,num);
    }

    /**
     * 1为递增  2 为递减
     * @param shareCode
     * @param file
     * @param flag
     */
    public void incrementForHashKey(String shareCode, String file, int flag) {
        if (1 == flag) {
            redisTemplate.opsForHash().increment(shareCode, file, 1);
        } else {
            redisTemplate.opsForHash().increment( shareCode, file, -1);
        }
    }

    /**
     * 从缓存获取用户信息
     *
     * @param key
     * @return
     */
    public UserLoginRestEntity getUserLoginRestEntityCache(String key) {
        String user = this.getUserCache(key);
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
        return userLoginRestEntity;
    }

    /**
     * 更新用户缓存
     *
     * @param userLoginRestEntity
     * @return
     */
    public void updateCacheSimple(UserLoginRestEntity userLoginRestEntity, String key) {
        String user = JSON.toJSONString(userLoginRestEntity);
        this.updateCache(user, key);
    }

    /**
     * 缓存用户类目信息map
     *
     * @param map
     * @param typeShareCode
     */
    public void cacheLabelType(Map<String, Object> map, String typeShareCode) {
        redisTemplate.opsForHash().putAll(typeShareCode, map);
        //设置缓存时间
        redisTemplate.expire(typeShareCode, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 缓存list格式数据   类目缓存
     * @param list
     * @param typeShareCode
     */
    public void cacheLabelTypeForList(String list, String typeShareCode) {
        redisTemplate.opsForValue().set(typeShareCode,list,RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 获取缓存用户类目信息
     *
     * @param typeShareCode
     * @return
     */
    @Deprecated
    public Map<String, Object> getCacheLabelType(String typeShareCode) {
        Map<String, Object> cacheData = redisTemplate.opsForHash().entries(typeShareCode);
        //重置缓存时间
        redisTemplate.expire(typeShareCode, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        return cacheData;
    }
    public List<?> getCacheLabelTypeForList(String typeShareCode) {
        String o = (String)redisTemplate.opsForValue().get(typeShareCode);
        JSONArray jsonArray = new JSONArray();
        if(StringUtils.isNotEmpty(o)){
            jsonArray = JSONArray.parseArray(o);
            //重置缓存时间
            redisTemplate.expire(typeShareCode, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        }

        return jsonArray;
    }

    public JSONArray getCacheLabelTypeForArray(String typeShareCode) {
        String o = (String)redisTemplate.opsForValue().get(typeShareCode);
        JSONArray jsonArray = new JSONArray();
        if(StringUtils.isNotEmpty(o)){
            jsonArray = JSONArray.parseArray(o);
            //重置缓存时间
            redisTemplate.expire(typeShareCode, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        }

        return jsonArray;
    }

    /**
     * 小程序活动老用户引导到记账小程序总访问量
     * @param wxappletChannel
     * @param field
     */
    public void incrementOldVisitor(String wxappletChannel,String field){
        redisTemplate.opsForHash().increment(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY +"_" +wxappletChannel+":sumOldVisitor", field, 1);
    }

    public void addOldVisitorToSet(String wxappletChannel,String shareCode){
        redisTemplate.opsForSet().add(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY +"_"+ wxappletChannel+":oldVisitorSet",shareCode);
    }

    /**
     * 统计游戏注册成功人数
     * @param wxappletChannel
     * @param field
     */
    public void incrementNewRegister(String wxappletChannel, String field) {
        redisTemplate.opsForHash().increment(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY +"_" +wxappletChannel+":sumNewRegister", field, 1);

    }

    public void incrementNewRegisterV2(String key, String field) {
        redisTemplate.opsForHash().increment(key, field, 1);

    }

    /**
     * 小程序活动新用户引导到记账小程序总访问量
     * @param wxappletChannel
     * @param field
     */
    public void incrementNewVisitor(String wxappletChannel, String field) {
        redisTemplate.opsForHash().increment(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY +"_" +wxappletChannel+":sumNewVisitor", field, 1);

    }

    public void incrementNewVisitor(String key, String field,int flag) {
        redisTemplate.opsForHash().increment(key, field, 1);
        if(1==flag){
            //设置缓存时间
            redisTemplate.expire(key,RedisPrefix.USER_VALID_TIME,TimeUnit.DAYS);
        }
    }

    /**
     * 统计游戏进入的有效注册用户数
     * @param wxappletChannel
     * @param shareCode
     */
    public void addNewVisitorToSet(String wxappletChannel,String shareCode){
        redisTemplate.opsForSet().add(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY +"_"+ wxappletChannel+":newVisitorSet",shareCode);
    }

    public void addNewVisitorToSet(String key,String value,int flag){
        redisTemplate.opsForSet().add(key,value);
        if(1==flag){
            //设置缓存时间
            redisTemplate.expire(key,RedisPrefix.USER_VALID_TIME,TimeUnit.DAYS);
        }
    }

    /**
     * 获取hash中的值以map形式返回
     * @param wxappletChannel
     * @return
     */
    public int getHashValue(String wxappletChannel,String keyName) {
        Map<String,Integer> entries = redisTemplate.opsForHash().entries(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + "_" + wxappletChannel + ":"+keyName);
        if(entries.get(keyName)==null){
            return 0;
        }
        return entries.get(keyName);
    }

    public int getHashValueV3(String wxappletChannel,String keyName) {
        Map<String,Integer> entries = redisTemplate.opsForHash().entries(wxappletChannel);
        if(entries.get(keyName)==null){
            return 0;
        }
        return entries.get(keyName);
    }

    /**
     * 获取hash中的值
     * @return
     */
    public List getHashValueV2(String prefix,String keyName) {
        return (List) redisTemplate.opsForHash().get(prefix, keyName);
    }

    /**
     * 删除hash中的值
     * @return
     */
    public void deleteHashValueV2(String prefix,String keyName) {
        redisTemplate.opsForHash().delete(prefix, keyName);
    }

    public boolean hasKey(String prefix,String keyName) {
        if(redisTemplate.hasKey(prefix)){
            return redisTemplate.opsForHash().hasKey(prefix,keyName);
        }
        return false;
    }
    /**
     * 获取hash map值
     * @param key
     * @return
     */
    public Map getHashMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public long getSetSize(String wxappletChannel,String keyName) {
        return redisTemplate.opsForSet().size(RedisPrefix.PREFIX_WXAPPLET_ACTIVITY + "_" + wxappletChannel + ":" + keyName);
    }

    public long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 获取剩余缓存时间
     * @param prefix
     * @return
     */
    public long getExpire(String prefix) {
        return redisTemplate.getExpire(prefix);
    }

    /**
     * 模糊匹配获取keys
     * @param prefix
     * @return
     */
    public Set getKeys(String prefix) {
        return redisTemplate.keys(prefix+"*");
    }

    /**
     * 往list中添加元素,不存在list 创建
     * flag 1:设置缓存时间   2:不设置缓存时间
     */
    public void setListRight(String prefix,List<String> value,int flag) {
        if(1==flag){
            redisTemplate.opsForList().rightPushAll(prefix,value);
            //设置缓存时间
            redisTemplate.expire(prefix,7L,TimeUnit.DAYS);
        }else{
            redisTemplate.opsForList().rightPushAll(prefix,value);
        }
    }

    /**
     * 弹出list最右边元素
     */
    public Object popListRight(String prefix) {
        return redisTemplate.opsForList().rightPop(prefix);
    }

    /**
     * 判断是否有key
     * @param prefix
     * @return
     */
    public boolean hasKey(String prefix) {
        if(redisTemplate.hasKey(prefix)){
            return true;
        }
        return false;
    }
}

