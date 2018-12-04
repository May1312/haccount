package com.fnjz.front.entity.api.wxappletmessagetemp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 小程序服务通知临时表
 * Created by yhang on 2018/12/1.
 */
@Entity
@Table(name = "hbird_wxapplet_message_temp")
public class WXAppletMessageTempRestEntity implements Serializable {

    WXAppletMessageTempRestEntity(){};
    public WXAppletMessageTempRestEntity(Integer userInfoId, String openId, String formId) {
        this.userInfoId = userInfoId;
        this.openId = openId;
        this.formId = formId;
    }

    /**id*/
    private java.lang.Integer id;
    /**用户id*/
    private java.lang.Integer userInfoId;
    /**小程序open_id*/
    private java.lang.String openId;
    /**服务通知formId*/
    private java.lang.String formId;
    /**创建时间*/
    private java.util.Date createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true,nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name ="USER_INFO_ID")
    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }

    @Column(name ="OPEN_ID")
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Column(name ="FORM_ID")
    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    @Column(name ="CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
