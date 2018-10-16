package com.fnjz.front.enums;

/**
 * 行为类别方式枚举
 * Created by yhang on 2018/10/13.
 */
public enum CategoryOfBehaviorEnum {

    NewbieTask(1,"新手任务"),
    TodayTask(2,"今日任务");

    private int index;
    private String description;

    CategoryOfBehaviorEnum(int index, String description) {
        this.index = index;
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
}
