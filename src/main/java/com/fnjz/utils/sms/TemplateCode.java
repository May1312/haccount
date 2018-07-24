package com.fnjz.utils.sms;

public enum TemplateCode {
    REGISTER_CODE("SMS_136025283","您正在申请手机注册，验证码为：${code}，5分钟内有效！"),
    TEMPLATE_02("SMS_136000280","您的${mtname}申请已于${submittime}审批通过，特此通知。"),
    LOGIN_CODE("SMS_136380629", "登录验证码"),
    RESETPWD_CODE("SMS_136394915","找回密码验证码"),
    BIND_MOBILE_CODE("SMS_136389856","绑定/解绑验证码"),
    /*admin 创建/删除测试用户验证码*/
    ADMIN_CERTAIN("SMS_140120912","验证管理员");
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
