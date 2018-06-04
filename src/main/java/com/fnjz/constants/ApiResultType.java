package com.fnjz.constants;

/**
 * 错误码枚举类
 */
public enum ApiResultType {

    OK("200", "success"),
    // 500- 服务器错误
    SERVER_ERROR("500", "服务器异常"),

    //登陆注册相关
    USERNAME_OR_PASSWORD_ERROR("01001","用户名或密码错误，请重新输入！"),
    USERNAME_OR_PASSWORD_ISNULL("01002","用户名或密码为空，请检查！"),
    MOBILE_IS_EXISTED("01003","手机号已注册！"),
    TOKEN_TIME_OUT("01004","TOKEN已失效，请重新登录！"),
    MOBILE_FORMAT_ERROR("01005","手机号格式错误，请检查！"),
    REQ_PARAMS_ERROR("01006","请求参数异常！"),
    VERIFYCODE_IS_ERROR("01007","验证码错误，请检查！"),
    VERIFYCODE_TIME_OUT("01008","验证码已失效！"),
    VERIFYCODE_IS_NULL("01009","验证码为空，请检查！"),
    WECHAT_PARAM_ERROR("01010","微信登录参数异常！"),
    GESTURE_PW_ERROR("01011","手势密码错误！"),
    USER_NOT_EXIST("01012","用户不存在！"),
    //用户修改手机号-->手机号未做修改时
    MOBILE_IS_NOT_EDIT("01013","手机号未做修改！"),
    USER_IS_NOT_LOGIN("01014","未登录状态，请登录！"),
    MOBILE_IS_NULL("01015","手机号为空，请检查！"),
    SEND_VERIFYCODE_ERROR("01016","验证码发送失败，请重试！"),
    USERNAME_OR_VERIFYCODE_ISNULL("01017","用户名或验证码为空，请检查！"),
    WXAPPLET_CODE_ISNULL("01018","小程序登录code为空，请检查！"),
    WXAPPLET_LOGIN_ERROR("01019","小程序登录异常，请检查！"),
    REGISTER_IS_ERROR("01020","注册用户失败，请重试！"),
    TOKEN_IS_INVALID("01021","TOKEN无效，请检查！"),
    PASSWORD_UPDATE_ERROR("01022","用户密码修改失败，请重试！"),
    WECHAT_LOGIN_ERROR("01023","微信授权登录异常，请重试！"),

    //手势密码相关
    GESTURE_PARAMS_ERROR("02001","参数异常，请检查！"),
    GESTURE_UPDATE_ERROR("02002","手势修改失败，请检查！"),
    GESTURE_PARAMS_LENGTH_ERROR("02003","参数长度异常，请检查！"),
    GESTURE_PASSWORD_IS_ERROR("02004","手势密码错误，请检查！"),

    //版本更新相关
    CHECK_VERSION_PARAMS_ERROR("03001","参数异常，请检查"),

    //明细相关
    ACCOUNT_PARAMS_ERROR("04001","参数异常，请检查"),
    ACCOUNT_TYPE_ERROR("04001","记账类型错误，请检查！"),
    DELETE_RECORD_ERROR("04002","删除记录异常，请重试！"),
    ADD_RECORD_ERROR("04003","添加记录异常，请重试！");

    //图表相关

    //我的相关

    ApiResultType(String status, String str) {
        setCode(status);
        setStr(str);
    }

    private String code;

    private String str;

    public String getCode() {
        return code;
    }

    public String getStr() {
        return str;
    }

    private void setCode(String code) {
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

