package com.fnjz.front.aspect;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.jeecgframework.core.util.IpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 请求响应切面类
 * Created by yhang on 2018/6/8.
 */
@Component
@Aspect
public class LogAspect {

    private static final Logger logger = Logger.getLogger(LogAspect.class);

    /**
     * 定义切点涉及api接口
     */
    @Pointcut("execution(* com.fnjz.front.controller..*.*(..))")
    public void logAspect() {
    }

    /**
     * 进入前执行
     *
     * @param joinPoint
     */
    @Before("logAspect()")
    public void before(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        logger.info("请求IP：" + IpUtil.getIpAddr(request));
        logger.info("请求路径：" + request.getRequestURL());
        logger.info("请求方式：" + request.getMethod());
        if(!StringUtils.equalsIgnoreCase(request.getMethod(),"GET")){
            //logger.info("请求参数：" + Arrays.toString(joinPoint.getArgs()));
            Object obj[] = joinPoint.getArgs();
            for(Object o :obj){
                if(o instanceof HttpServletRequest){ }else{
                    logger.info("请求参数：" + JSON.toJSONString(o));
                }
            }
        }
    }

    @Around("logAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();
        Object retVal = joinPoint.proceed(args);
        long endTime = System.currentTimeMillis();
        logger.info("执行时间: ms " + (endTime - startTime));
        logger.info("返回值:" + JSON.toJSON(retVal));
        return retVal;
    }

    /**
     * 异常时执行
     *
     * @param ex
     */
    @AfterThrowing(throwing = "ex", pointcut = "logAspect()")
    public void afterThrowing(Throwable ex) {
        logger.error("发生异常：" + ex.toString());
    }
}
