package com.fnjz.front.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.utils.MD5Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.jeecgframework.core.util.MD5Util;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.jwt.def.JwtConstants;
import org.jeecgframework.jwt.model.TokenModel;
import org.jeecgframework.jwt.service.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * 移动端 拦截器
 */

@Component
public class ApiInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void afterCompletion(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object obj, Exception exception) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        if(requestPath.indexOf("/rest/api/")==-1 || excludeUrls.contains(requestPath) ||moHuContain(excludeContainUrls, requestPath)){
            return true;
        }

        //从header中得到token
        String authHeader = request.getHeader("token");
        if (authHeader == null) {
            throw new ServletException("Missing or invalid token header.");
        }
        // 验证token
        Claims claims = null;
        ResultBean rb = new ResultBean();
        try {
            claims = Jwts.parser().setSigningKey(JwtConstants.JWT_SECRET).parseClaimsJws(authHeader).getBody();
        }catch (final SignatureException e) {
            rb.setFailMsg(ApiResultType.TOKEN_IS_INVALID);
            this.sendJsonMessage(response,rb);
            return false;
        }

        Object username = claims.getId();
        if (oConvertUtils.isEmpty(username)) {
            rb.setFailMsg(ApiResultType.TOKEN_IS_INVALID);
            this.sendJsonMessage(response,rb);
            return false;
        }
        String user = (String)redisTemplate.opsForValue().get(MD5Utils.getMD5(username.toString()));
        if (StringUtil.isEmpty(user)) {
            rb.setFailMsg(ApiResultType.TOKEN_TIME_OUT);
            this.sendJsonMessage(response,rb);
            return false;
        } else {
            //如果token验证成功，将token对应的用户id存在request中，便于之后注入
            //request.setAttribute("","");
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object obj, ModelAndView modelandview) throws Exception {
        // TODO Auto-generated method stub
    }

    private List<String> excludeUrls;
    /**
     * 包含匹配（请求链接包含该配置链接，就进行过滤处理）
     */
    private List<String> excludeContainUrls;

    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public List<String> getExcludeContainUrls() {
        return excludeContainUrls;
    }

    public void setExcludeContainUrls(List<String> excludeContainUrls) {
        this.excludeContainUrls = excludeContainUrls;
    }
    /**
     * 模糊匹配字符串
     * @param list
     * @param key
     * @return
     */
    private boolean moHuContain(List<String> list,String key){
        for(String str : list){
            if(key.contains(str)){
                return true;
            }
        }
        return false;
    }

    public void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat));
        writer.close();
        response.flushBuffer();
    }
}