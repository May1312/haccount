package com.fnjz.constants;

/**
 * 域名枚举类
 * Created by yhang on 2018/8/22.
 */
public enum DomainEnum {

    //七牛云上传域名---->用户头像
    HEAD_PICTURE_DOMAIN("head-picture","http://head.image.fengniaojizhang.cn/"),

    //七牛云上传域名---->小程序邀请码生成
    WXAPPLET_QR_CODE_DOMAIN("head-picture","https://head.image.fengniaojizhang.cn/"),

    //七牛云上传域名---->记账类目
    LABEL_PICTURE_DOMAIN("label-picture","http://label.image.fengniaojizhang.cn/");

    private String domainName;

    private String domainUrl;

    DomainEnum(String domainName, String domainUrl) {
        this.setDomainName(domainName);
        this.setDomainUrl(domainUrl);
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }
}
