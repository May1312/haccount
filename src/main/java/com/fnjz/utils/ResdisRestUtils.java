package com.fnjz.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 移动端redis 缓存工具类
 * Created by yhang on 2018/5/31.
 */
public class ResdisRestUtils {

    private static JedisPool jedisPool = null;

    private static String HOST_URL = "47.95.0.94";

    private static Integer PORT = 6380;

    private static String PASSWORD = "Fnredis2018";

    //用户登录验证码前缀
    public static final String PROFIX_USER_VERIFYCODE_LOGIN = "user_verifycode_login:";
    //用户注册验证码前缀
    public static final String PROFIX_USER_VERIFYCODE_REGISTER = "user_verifycode_register:";

    //初始化redis
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(1024);
        config.setMaxTotal(1024);
        config.setMinIdle(20);
        config.setMaxWaitMillis(1000 * 10);
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config, HOST_URL, PORT, 0, PASSWORD, 0);

    }

    /**
     * 通过key获取value
     * @param key
     * @return value
     * @param database 数据库编号  redis含 0-15 共16个数据库
     */
    public static String get(String key,Integer database,int seconds){
        Jedis jedis = null;
        if(database==null)database=0;
        try {
            if(jedisPool!=null){
                jedis = jedisPool.getResource();
                if(jedis!=null){
                    jedis.select(database);
                    //重置keys生存时间,ttl获取剩余存活时间
                    Long ttl = jedis.ttl(key);
                    /*if(ttl>0 && ttl<(60*30)){
                        jedis.expire(key,(60*30));
                    }*/
                    if(seconds != 0){
                        if(ttl>0){
                            jedis.expire(key,seconds);
                        }
                    }
                    return jedis.get(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                //返还到连接池
                jedis.close();
            }

        }
        return null;
    }

    /**
     * 根据key和value以及期限进行设置
     * @param key
     * @param value
     * @param database 数据库编号  redis含 0-15 共16个数据库
     * @param seconds 时间期限（单位：s）
     */
    public static void set(String key, String value, int seconds,Integer database){
        Jedis jedis = null;
        if(database==null)database=0;
        try {
            if(jedisPool!=null){
                jedis = jedisPool.getResource();
                if(jedis!=null){
                    jedis.select(database);
                    if(seconds == 0){
                        jedis.set(key, value);
                    } else {
                        jedis.setex(key, seconds, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                //返还到连接池
                jedis.close();
            }
        }
    }

    /**
     * 根据key删除
     * @param key
     * @param database 数据库编号  redis含 0-15 共16个数据库
     */
    public static void del(String key,Integer database){
        Jedis jedis = null;
        if(database==null)database=0;
        try {
            if(jedisPool!=null){
                jedis = jedisPool.getResource();
                if(jedis!=null){
                    jedis.select(database);
                    jedis.del(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                //返还到连接池
                jedis.close();
            }
        }
    }
    /**
     * 根据key删除
     * @param key
     * @param database 数据库编号  redis含 0-15 共16个数据库
     */
    public static boolean isExisit(String key,Integer database){
        Jedis jedis = null;
        if(database==null)database=0;
        try {
            if(jedisPool!=null){
                jedis = jedisPool.getResource();
                if(jedis!=null){
                    jedis.select(database);
                    return jedis.exists(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                //返还到连接池
                jedis.close();
            }
        }
        return false;
    }


    /**
     * 列表类型的值设置
     * @param key
     * @param database 数据库编号  redis含 0-15 共16个数据库
     * @param values
     */
    public static void lPush(String key,Integer database, String ... values){
        Jedis jedis = null;
        if(database==null)database=0;
        try {
            if(jedisPool!=null){
                jedis = jedisPool.getResource();
                if(jedis!=null){
                    jedis.select(database);
                    jedis.lpush(key, values);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                //返还到连接池
                jedis.close();
            }
        }
    }
}
