package com.fnjz.front.entity.api.buriedpointtype;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 埋点记录类
 * Created by yhang on 2019/1/5.
 */
@Entity
@Table(name = "hbird_buried_point")
public class BuriedPointRestEntity implements Serializable {

    /**id*/
    private Long id;
    /**用户详情id*/
    private Integer userInfoId;
    /**唯一标识 终端设备号（移动端） union（小程序）*/
    private String deviceNum;
    /**埋点类型id*/
    private Integer pointTypeId;
    /**上传终端类型 android ios wxapplet*/
    private String clientId;
    /**手机品牌*/
    private String brand;
    /**手机型号*/
    private String model;
    /**微信版本号*/
    private String wechatVersion;
    /**操作系统版本号*/
    private String system;
    /**客户端平台*/
    private String platform;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", unique = true,nullable=false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "USER_INFO_ID")
    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }
    @Column(name = "DEVICE_NUM")
    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }
    @Column(name = "POINT_TYPE_ID")
    public Integer getPointTypeId() {
        return pointTypeId;
    }

    public void setPointTypeId(Integer pointTypeId) {
        this.pointTypeId = pointTypeId;
    }
    @Column(name = "CLIENT_ID")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    @Column(name = "BRAND")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    @Column(name = "MODEL")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    @Column(name = "WECHAT_VERSION")
    public String getWechatVersion() {
        return wechatVersion;
    }

    public void setWechatVersion(String wechatVersion) {
        this.wechatVersion = wechatVersion;
    }
    @Column(name = "SYSTEM")
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
    @Column(name = "PLATFORM")
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
