package com.fnjz.front.enums;

/**
 * 积分--获取方式枚举
 * Created by yhang on 2018/10/13.
 */
public enum AcquisitionModeEnum {

    //每日任务
    Inviting_friends(2,"Inviting_friends","邀请好友","inviteFriends"),
    Write_down_an_account(3,"Write_down_an_account","记一笔账","toCharge"),
    Get_a_new_Badge(4,"Get_a_new_Badge","获得新徽章","getBadge"),
    The_invitation_came_to_five(15,"The_invitation_came_to_five","邀请达5人","invite2five"),
    The_bookkeeping_came_to_three(16,"The_bookkeeping_came_to_three","记账达3笔","charge2three"),

    //新手任务
    binding_phone_or_wx(5,"binding_phone_or_wx","绑定手机号或微信","bindPhoneOrWX"),
    Setting_up_budget(6,"Setting_up_budget","设置预算","budget"),
    Setting_up_savings_efficiency(8,"Setting_up_savings_efficiency","设置存钱效率","savingEfficiency"),
    Perfecting_personal_data(9,"Perfecting_personal_data","完善个人资料","userInfo"),
    //Setup_account_reminder(10,"Setup_account_reminder","设置记账提醒","toCharge"),
    Check_in(11,"Check_in","补签到",""),
    SignIn(12,"SignIn","签到",""),
    Add_to_my_applet(13,"Add_to_my_applet","添加到我的小程序","add2wxapplet"),
    Become_hbird_user(14,"Become_hbird_user","成为蜂鸟记账用户","beUser"),
    //Record_keeping_mood(13,"Record_keeping_mood","记录记账心情","");
    //话术描述  返给您丰丰票红利
    BONUS(17,null,"返给您丰丰票红利",null),
    USER_INTEGRAL_ACTIVITY(18,null,"记账挑战赛扣除",null),
    USER_INTEGRAL_ACTIVITY2(19,null,"记账挑战成功",null);

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
