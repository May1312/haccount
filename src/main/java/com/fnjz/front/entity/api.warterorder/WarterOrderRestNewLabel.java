package com.fnjz.front.entity.api.warterorder;
import javax.persistence.*;

/**
 * Created by yhang on 2018/11/7.
 */
public class WarterOrderRestNewLabel extends WarterOrderRestEntity{

    private Integer userPrivateLabelId;

    /**图标*/
    private java.lang.String icon;

    /**
     * 账户id(资产类型id)'
     */
    private Integer assetsId = 0;

    /**
     * 账户名称
     * @return
     */
    private String assetsName;

    /**记账终端*/
    private java.lang.String clientId;

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

    @Column(name = "ASSETS_ID")
    public Integer getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(Integer assetsId) {
        this.assetsId = assetsId;
    }

    @Column(name = "ASSETS_NAME")
    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }

    @Column(name ="CLIENT_ID")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
