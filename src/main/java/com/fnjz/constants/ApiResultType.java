package com.fnjz.constants;

/**
 * 错误码枚举类
 */
public enum ApiResultType {

    OK(200, "success"),
    // 500- 服务器错误
    SERVER_ERROR(500, "服务器异常"),

    //登陆注册相关
    USERNAME_OR_PASSWORD_ERROR(01001,"用户名或密码错误，请重新输入！"),
    USERNAME_OR_PASSWORD_ISNULL(01002,"用户名或密码为空，请检查！"),
    MOBILE_IS_EXISTED(01003,"手机号已注册！"),
    TOKEN_TIME_OUT(01004,"TOKEN已失效，请重新登录！"),
    MOBILE_FORMAT_ERROR(01005,"手机号格式错误，请检查！"),
    REQ_PARAMS_ERROR(01006,"请求参数异常！"),
    VERIFYCODE_IS_ERROR(01006,"验证码错误，请检查！"),
    VERIFYCODE_TIME_OUT(01007,"");

    ApiResultType(int status, String str) {
        setCode(status);
        setStr(str);
    }

    private int code;

    private String str;

    public int getCode() {
        return code;
    }

    public String getStr() {
        return str;
    }

    private void setCode(int code) {
        this.code = code;
    }

    private void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("code");
        sb.append(":");
        sb.append(code);
        sb.append(",");
        sb.append("msg");
        sb.append(":");
        sb.append(str);
        return sb.toString();
    }
}

