package com.fnjz.front.enums;

/**
 * 积分--获取方式枚举
 * Created by yhang on 2018/10/13.
 */
public enum AcquisitionModeEnum {

    Inviting_friends("Inviting_friends","邀请好友"),
    Write_down_an_account("Write_down_an_account","记一笔账"),
    Get_a_new_Badge("Get_a_new_Badge","获得新徽章"),
    binding_phone_or_wx("binding_phone_or_wx","绑定手机号或微信"),
    Setting_up_budget("Setting_up_budget","设置预算"),
    Setting_up_savings_efficiency("Setting_up_savings_efficiency","设置存钱效率"),
    Perfecting_personal_data("Perfecting_personal_data","完善个人资料"),
    Setup_account_reminder("Setup_account_reminder","设置记账提醒"),
    Check_in("Check_in","补签到"),
    SignIn("SignIn","补签到"),
    Record_keeping_mood("Record_keeping_mood","记录记账心情");

    private String enlish;
    private String description;


    AcquisitionModeEnum(String enlish, String description){
        this.description = description;
        this.enlish = enlish;
    }
    public String getIndex() {
        return enlish;
    }

    public void setIndex(String enlish) {
        this.enlish = enlish;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
