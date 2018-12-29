package com.fnjz.utils.sms;

public enum TemplateCode {
    REGISTER_CODE("SMS_136025283","您正在申请手机注册，验证码为：${code}，5分钟内有效！"),
    TEMPLATE_02("SMS_136000280","您的${mtname}申请已于${submittime}审批通过，特此通知。"),
    LOGIN_CODE("SMS_136380629", "登录验证码"),
    RESETPWD_CODE("SMS_136394915","找回密码验证码"),
    BIND_MOBILE_CODE("SMS_136389856","绑定/解绑验证码"),
    /*admin 创建/删除测试用户验证码*/
    ADMIN_CERTAIN("SMS_140120912","验证管理员"),

    //创蓝模板定义
    CL_REGISTER("CL_REGISTER","【蜂鸟记账】验证码为：{s}，您注册所使用的验证码，若非本人操作，请勿泄露，3分钟内有效。"),
    CL_LOGIN("CL_LOGIN","【蜂鸟记账】验证码为：{s}，您本次登录所使用的验证码，若非本人操作，请勿泄露，3分钟内有效。"),
    CL_RESETPWD("CL_LOGIN","【蜂鸟记账】验证码为：{s}，您本次找回密码所使用的验证码，若非本人操作，请勿泄露，3分钟内有效。"),
    CL_BIND_MOBILE("CL_LOGIN","【蜂鸟记账】验证码为：{s}，您正在绑定/解绑手机号，若非本人操作，请勿泄露，3分钟内有效。"),
    CL_ADMIN_CERTAIN("CL_LOGIN","【蜂鸟记账】验证码为：{s}，您好，管理员，此验证码仅用于创建/删除测试用户，若非本人操作，请勿泄露，3分钟内有效。"),
    CL_CASH_MOBILE("CL_LOGIN","【蜂鸟记账】验证码为：{s}，您正在兑换现金红包类商品，若非本人操作，请勿泄露，3分钟内有效。"),
    DOWN_GOODS("DOWN_GOODS","【蜂鸟记账】您好，管理员，由于{s},系统执行下架商品，请登录web查看。"),
    SEND_EXCHANGE_GOODS("DOWN_GOODS","【蜂鸟记账】您已成功兑换“{s1}”，兑换码：{s2}。兑换截止日期：{s3}。"),
    ;
    TemplateCode(String templateCode, String templateContent) {
        TemplateCode = templateCode;
        TemplateContent = templateContent;
    }

    // 成员变量
    private String TemplateCode;
    private String TemplateContent;

    public String getTemplateCode() {
        return TemplateCode;
    }

    public TemplateCode setTemplateCode(String templateCode) {
        TemplateCode = templateCode;
        return this;
    }

    public String getTemplateContent() {
        return TemplateContent;
    }

    public TemplateCode setTemplateContent(String templateContent) {
        TemplateContent = templateContent;
        return this;
    }


}
