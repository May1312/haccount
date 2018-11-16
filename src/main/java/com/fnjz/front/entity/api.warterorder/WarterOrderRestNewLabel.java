package com.fnjz.front.entity.api.warterorder;
import javax.persistence.*;

/**
 * Created by yhang on 2018/11/7.
 */
public class WarterOrderRestNewLabel extends WarterOrderRestEntity{

    private Integer userPrivateLabelId;

    /**图标*/
    private java.lang.String icon;

    @Column(name = "USER_PRIVATE_LABEL_ID", nullable = true)
    public Integer getUserPrivateLabelId() {
        return userPrivateLabelId;
    }

    public void setUserPrivateLabelId(Integer userPrivateLabelId) {
        this.userPrivateLabelId = userPrivateLabelId;
    }

    @Column(name ="ICON",nullable=true,length=255)
    public java.lang.String getIcon(){
        return this.icon;
    }

    /**
     *方法: 设置java.lang.String
     *@param: java.lang.String  图标
     */
    public void setIcon(java.lang.String icon){
        this.icon = icon;
    }
}
