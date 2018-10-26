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
    USERNAME_OR_PASSWORD_ERROR("01001","请输入正确的手机号和密码"),
    USERNAME_OR_PASSWORD_ISNULL("01002","请输入手机号和密码"),
    MOBILE_IS_EXISTED("01003","该手机号已注册，请直接登录"),
    TOKEN_TIME_OUT("01004","注册信息过期，请重新登录"),
    MOBILE_FORMAT_ERROR("01005","请输入正确的手机号"),
    REQ_PARAMS_ERROR("01006","请求参数异常"),
    VERIFYCODE_IS_ERROR("01007","请输入正确的验证码"),
    VERIFYCODE_TIME_OUT("01008","验证码已失效，请重新获取"),
    VERIFYCODE_IS_NULL("01009","请输入验证码"),
    WECHAT_PARAM_ERROR("01010","微信登录异常，请尝试其他方式登录"),
    GESTURE_PW_ERROR("01011","手势密码有误，请重新输入"),
    USER_NOT_EXIST("01012","该账号未注册，请先注册账号"),
    //用户修改手机号-->手机号未做修改时
    MOBILE_IS_NOT_EDIT("01013","请先修改手机号"),
    USER_IS_NOT_LOGIN("01014","请先登录"),
    MOBILE_IS_NULL("01015","请输入手机号"),
    SEND_VERIFYCODE_ERROR("01016","验证码发送失败，请重试"),
    USERNAME_OR_VERIFYCODE_ISNULL("01017","请输入手机号或验证码"),
    WXAPPLET_CODE_ISNULL("01018","登录code为空，请检查"),
    WXAPPLET_LOGIN_ERROR("01019","登录异常，请重试"),
    REGISTER_IS_ERROR("01020","注册用户失败，请重试"),
    TOKEN_IS_INVALID("01021","TOKEN无效，请重试"),
    PASSWORD_UPDATE_ERROR("01022","密码修改失败，请重试"),
    WECHAT_LOGIN_ERROR("01023","微信授权登录异常，请重试"),
    PASSWORD_ERROR("01024","密码验证错误，请重新输入"),
    BIND_MOBILE_PWD_ERROR("01025","绑定手机号密码失败，请重试"),
    VERIFYCODE_LIMIT("01026","业务限流，对同一个手机号码发送短信验证码，支持1条/分钟，5条/小时 ，累计10条/天！"),
    NOT_ALLOW_BIND_WECHAT("01027","您已用微信注册，无需绑定微信"),
    WECHAT_CODE_ISNULL("01028","微信code为空，请检查"),
    WECHAT_BIND_ERROR("01029","微信授权绑定异常，请重试"),
    NOT_ALLOW_UNBIND_WECHAT("01030","您正在使用微信登录，无法解绑"),
    WECHAT_UNBIND_ERROR("01031","微信解绑失败，请重试"),
    MOBILE_UPDATE_ERROR("01032","用户手机号修改失败，请重试"),
    WECHAT_IS_BINDED("01033","该微信号已注册账号"),
    UNIONID_IS_NULL("01034","未获取到unionid，请重新授权"),
    encryptedData_IS_NULL("01035","encryptedData为空，请检查！"),
    IV_IS_NULL("01036","iv为空，请检查！"),
    KEY_IS_NULL("01037","key为空，请检查！"),
    MOBILE_IS_VAILD("999","手机号无此权限!"),
    ROBOT_IS_VAILD("888","测试用户号段必须不小于13位!"),

    //手势密码相关
    GESTURE_PARAMS_ERROR("02001","参数异常，请检查！"),
    GESTURE_UPDATE_ERROR("02002","手势修改失败，请重试"),
    GESTURE_PARAMS_LENGTH_ERROR("02003","参数长度异常，请检查"),
    GESTURE_PASSWORD_IS_ERROR("02004","手势密码错误，请重试"),

    //版本更新相关
    CHECK_VERSION_PARAMS_ERROR("03001","参数异常，请检查"),
    VERSION_IS_NULL("03002","版本号为空，请检查"),
    SYSTEM_TYPE_IS_NULL("03003","终端类型type为空，请检查"),

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
    SPEND_TYPE_ID_IS_NULL("04011","三级类目id为空，请检查！"),
    SPEND_TYPE_ID_IS_NOT_EXIST("04012","未查询到该支出类目id，请检查！"),
    SPEND_TYPE_ID_IS_ERROR("04013","当前类目id为二级id，请检查！"),
    SPEND_TYPE_IS_ADDED("04014","当前类目id已在常用类目列表，请检查！"),
    TYPE_IS_NULL("04015","类目类型为空，请检查！"),
    TYPE_RELATION_IS_NULL("04016","类目优先级关系为空，请检查！"),

    //图表统计相关
    TYPE_FLAG_IS_NULL("05001","统计类型flag为空，请检查！"),
    QUERY_TIME_IS_NULL("05002","日统计时间范围为空，请检查！"),
    QUERY_WEEK_IS_NULL("05003","周统计时间范围为空，请检查！"),
    QUERY_FLAG_IS_ERROR("05004","统计类型flag异常，请检查！"),
    TIME_IS_NULL("05005","未指定时间time，请检查！"),

    //我的相关
    MY_PARAMS_ERROR("06001","参数异常，请检查"),
    CONTENT_IS_NULL("06002","请输入内容后提交"),
    NICKNAME_NOT_FORMAT("06003","昵称不规范，请更改"),

    //预算相关
    BUDGET_MONEY_IS_SMALL("07001","预算金额小于当月固定支出金额，请检查"),
    FIXED_EXPENDITURE_IS_LARGE("07002","固定支出金额大于当月预算金额，请检查"),
    TIME_IS_ERROR("07003","time格式异常，请检查！"),

    //离线同步相关
    SYN_DATE_IS_ERROR("08001","同步时间校验不通过，请检查!"),

    //签到积分相关
    HAS_SIGNED("08002","已签到，请勿重复！"),

    //积分校验相关
    INTEGRAL_EXCHANGE_NOT_ALLOW2("09001","当前用户存在兑换中商品,不允许再次兑换！"),
    INTEGRAL_EXCHANGE_NOT_ALLOW("09002","当前用户积分数不允许兑换！");

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

