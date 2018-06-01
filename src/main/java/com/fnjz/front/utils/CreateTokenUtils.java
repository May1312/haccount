package com.fnjz.front.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jeecgframework.jwt.def.JwtConstants;
import org.jeecgframework.web.system.pojo.base.TSUser;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * token生成工具类
 * Created by yhang on 2018/6/1.
 */
public class CreateTokenUtils {
    public String createToken(TSUser user) {
        //使用uuid作为源token
        String token = Jwts.builder().setId(user.getUserName()).setSubject(user.getUserName()).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, JwtConstants.JWT_SECRET).compact();
        //存储到redis并设置过期时间
        //redisTemplate.boundValueOps(user.getUserName()).set(token, JwtConstants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS);
        return token;
    }
}
