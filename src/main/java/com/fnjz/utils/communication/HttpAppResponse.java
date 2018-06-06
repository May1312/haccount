package com.fnjz.utils.communication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jeecgframework.p3.core.common.utils.StringUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by sqwang-home on 2018/6/1.
 */
public class HttpAppResponse {

    /**
     *
     * @Title: formatInput
     * @Description: TODO(格式化输入参数)
     * @param para
     * @return
     * @return JSONObject
     * @throws
     */
    public static JSONObject formatInput(String para){
        JSONObject obj = null;
        //传入参数必备校验检查
        if (StringUtil.isEmpty(para)) {
            return null;
        }
        try{
            obj = JSONObject.parseObject(para);
        }catch(Exception ex){
            return null;
        }
        return obj;
    }

    /**
     *
     * @Title: getResultMap
     * @Description: TODO(获取结果map)
     * @param jsonData
     * @param returnCode
     * @param returnInfo
     * @return
     * @return Map<String,Object>
     * @throws
     */
    public static Map<String, Object> getResultMap(String jsonData, int success, String returnInfo){
        Map<String, Object> attributes = new HashMap<String, Object> ();
        attributes.put("data", jsonData);
        attributes.put("returncode", "0");
        attributes.put("returninfo", "");
        return attributes;
    }

    /**
     *
     * @Title: responseJson
     * @Description: TODO(输出客户端)
     * @param ajaxJson
     * @param response
     * @return void
     * @throws
     */
    public static void responseJson(AppResponseJson j, HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().print(j.getJsonStr());
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
