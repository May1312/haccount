package com.fnjz.front.entity.api.wxappletmessagetemp;

/**
 * 小程序服务通知发送数据临时表
 * Created by yhang on 2018/12/3.
 */

public class WXAppletAccountNotifyTempRestEntity extends  WXAppletMessageTempRestEntity {

    WXAppletAccountNotifyTempRestEntity(Integer userInfoId, String openId, String formId) {
        super(userInfoId, openId, formId);
    }

    private double spend;

    private double income;

    public double getSpend() {
        return spend;
    }

    public void setSpend(double spend) {
        this.spend = spend;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }
}
