package com.fnjz.constants;

import io.swagger.annotations.ApiModel;

/**
 * 错误码枚举类
 */
@ApiModel
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
    PASSWORD_ERROR("01024","密码错误，请重试！"),
    BIND_MOBILE_PWD_ERROR("01025","绑定手机号密码失败，请重试！"),
    VERIFYCODE_LIMIT("01026","业务限流，对同一个手机号码发送短信验证码，支持1条/分钟，5条/小时 ，累计10条/天！"),
    NOT_ALLOW_BIND_WECHAT("01027","微信注册用户不允许再绑定微信！"),
    WECHAT_CODE_ISNULL("01028","微信code为空，请检查！"),
    WECHAT_BIND_ERROR("01029","微信授权绑定异常，请重试！"),
    NOT_ALLOW_UNBIND_WECHAT("01030","当前用户为微信注册用户，无法解绑微信！"),
    WECHAT_UNBIND_ERROR("01031","微信解绑失败，请重试！"),
    MOBILE_UPDATE_ERROR("01032","用户手机号修改失败，请重试！"),
    WECHAT_IS_BINDED("01033","该微信号已被使用，请检查！"),
    UNIONID_IS_NULL("01034","未获取到unionid，请检查！"),
    encryptedData_IS_NULL("01035","encryptedData为空，请检查！"),
    IV_IS_NULL("01036","iv为空，请检查！"),
    KEY_IS_NULL("01037","key为空，请检查！"),

    //手势密码相关
    GESTURE_PARAMS_ERROR("02001","参数异常，请检查！"),
    GESTURE_UPDATE_ERROR("02002","手势修改失败，请检查！"),
    GESTURE_PARAMS_LENGTH_ERROR("02003","参数长度异常，请检查！"),
    GESTURE_PASSWORD_IS_ERROR("02004","手势密码错误，请检查！"),

    //版本更新相关
    CHECK_VERSION_PARAMS_ERROR("03001","参数异常，请检查"),

    //明细相关
    ACCOUNT_PARAMS_ERROR("04001","参数异常，请检查"),
    ACCOUNT_TYPE_ERROR("04002","记账类型为空，请检查！"),
    DELETE_RECORD_ERROR("04003","删除记录异常，请重试！"),
    ADD_RECORD_ERROR("04004","添加记录异常，请重试！"),
    ACCOUNT_MONEY_IS_NULL("04005","记账金额为空，请检查！"),
    ACCOUNT_MONEY_ERROR("04006","记账金额格式校验错误，请检查！"),
    ACCOUNT_SPENDDATE_ERROR("04007","记账时间为空，请检查！"),
    PAGE_PARAMS_ERROR("04008","分页参数异常，请检查！"),
    ORDER_ID_IS_NULL("04009","单笔记账详情id为空，请检查！"),
    GET_ORDER_ERROR("04010","未查询到该笔记账，请检查！"),
    SPEND_TYPE_ID_IS_NULL("04011","三级支出类目id为空，请检查！"),
    SPEND_TYPE_ID_IS_NOT_EXIST("04012","未查询到该支出类目id，请检查！"),
    SPEND_TYPE_ID_IS_ERROR("04013","当前类目id为二级id，请检查！"),
    SPEND_TYPE_IS_ADDED("04014","当前类目id已在常用类目列表，请检查！");

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

