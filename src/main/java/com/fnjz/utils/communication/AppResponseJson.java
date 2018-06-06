package com.fnjz.utils.communication;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by sqwang-home on 2018/6/1.
 */
public class AppResponseJson {

    //常见响应码表对应code
    public static final int SUCCESS              = 100;
    public static final String SUCCESS_MSG       = "成功";
    public static final int ERROR                = -100;
    public static final String ERROR_MSG         = "失败";
    public static final int PARAM_ERROR          = -101;
    public static final String PARAM_ERROR_MSG   = "参数错误或者参数不完整";
    public static final int NO_PERMISSION        = -102;
    public static final String NO_PERMISSION_MSG = "无接口访问权限";
    public static final int UE01                 = -999;
    public static final String UE01_MSG          = "未知异常";

    //变量定义
    private int                 code       = SUCCESS;    // 是否成功
    private String              msg        = SUCCESS_MSG;// 提示信息
    private String              data       = null;        // 数据信息
    //

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int  getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getJsonStr(){
        JSONObject obj = new JSONObject();
        obj.put("code", this.getCode());
        obj.put("msg", this.getMsg());
        obj.put("data", this.data);
        return obj.toJSONString();
    }
}
