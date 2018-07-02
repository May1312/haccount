package com.fnjz.front.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jeecgframework.jwt.def.JwtConstants;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * token生成工具类
 * Created by yhang on 2018/6/1.
 */
@Component
public class CreateTokenUtils {
    public String createToken(String code) {
        //使用uuid作为源token
        String token = Jwts.builder().setId(code).setSubject(code).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, JwtConstants.JWT_SECRET).compact();
        return token;
    }
}
