package com.fnjz.commonbean;

import com.fnjz.constants.ApiResultType;

import java.io.Serializable;

/**
 * Created by yhang on 2018/5/31.
 */
public class ResultBean implements Serializable {

    public String code;
    public String msg;
    public Object result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setFailMsg(ApiResultType type) {
        setCode(type.getCode());
        setMsg(type.getStr());
    }

    /**
     *
     */
    public void setFailMsg(String code, String str) {
        setCode(code);
        setMsg(str);
    }

    /**
     * @param result 成功返回数据
     */
    public void setSucResult(Object result) {
        setCode(ApiResultType.OK.getCode());
        setMsg(ApiResultType.OK.getStr());
        //setResult(result);
    }
}
