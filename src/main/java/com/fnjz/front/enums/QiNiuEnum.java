package com.fnjz.front.enums;

/**
 * 七牛云仓库上传类型
 * Created by yhang on 2018/7/11.
 */
public enum QiNiuEnum {

    /**
     * head-picture 头像
     */
    HEAD_PICTURE("head-picture","1"),
    /**
     * feedback-picture 用户反馈
     */
    FEEDBACK_PICTURE("feedback-picture","2");

    private String name;
    private String index;

    QiNiuEnum(String name,String index){
        this.name = name;
        this.index = index;
    }
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
