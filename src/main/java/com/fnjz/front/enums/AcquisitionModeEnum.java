package com.fnjz.front.enums;

/**
 * 积分--获取方式枚举
 * Created by yhang on 2018/10/13.
 */
public enum AcquisitionModeEnum {

    Inviting_friends(2,"Inviting_friends","邀请好友","inviteFriends"),
    Write_down_an_account(3,"Write_down_an_account","记一笔账","toCharge"),
    Get_a_new_Badge(4,"Get_a_new_Badge","获得新徽章",""),
    binding_phone_or_wx(5,"binding_phone_or_wx","绑定手机号或微信","bindPhoneOrWX"),
    Setting_up_budget(6,"Setting_up_budget","设置预算","budget"),
    Setting_up_savings_efficiency(8,"Setting_up_savings_efficiency","设置存钱效率","savingEfficiency"),
    Perfecting_personal_data(9,"Perfecting_personal_data","完善个人资料","userInfo"),
    Setup_account_reminder(10,"Setup_account_reminder","设置记账提醒","toCharge"),
    Check_in(11,"Check_in","补签到",""),
    SignIn(12,"SignIn","签到",""),
    Record_keeping_mood(13,"Record_keeping_mood","记录记账心情","");

    private int index;
    private String name;
    private String description;
    private String forUser;


    AcquisitionModeEnum(int index,String name, String description,String forUser){
        this.index = index;
        this.description = description;
        this.name = name;
        this.forUser = forUser;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getForUser() {
        return forUser;
    }

    public void setForUser(String forUser) {
        this.forUser = forUser;
    }
}
