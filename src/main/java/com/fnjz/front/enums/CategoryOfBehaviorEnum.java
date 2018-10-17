package com.fnjz.front.enums;

/**
 * 行为类别方式枚举
 * Created by yhang on 2018/10/13.
 */
public enum CategoryOfBehaviorEnum {

    NewbieTask(1,"NewbieTask","新手任务"),
    TodayTask(2,"TodayTask","今日任务"),
    SignIn(3,"SignIn","签到");

    private int index;
    private String name;
    private String description;

    CategoryOfBehaviorEnum(int index,String name, String description) {
        this.index = index;
        this.name = name;
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
