package com.fnjz.front.entity.api.warterorder;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @Description: 账本流水表相关--->移动端多账本封装类  添加用户头像  昵称  账本名称
 */
@Entity
public class APPWarterOrderRestDTO extends WarterOrderRestEntity {

    /**用户自有类目id*/
    private Integer userPrivateLabelId;

    /**三级类目icon*/
    private String icon;

    /**修改者信息--昵称*/
    private String reporterNickName;

    /**修改者信息--头像*/
    private String reporterAvatar;

    /**账本名称*/
    private String abName;

    /**账本类型id*/
    private Integer abTypeId;

    /**
     * 账户id(资产类型id)'
     */
    private Integer assetsId = 0;

    /**
     * 账户名称
     * @return
     */
    private String assetsName;

    public Integer getUserPrivateLabelId() {
        return userPrivateLabelId;
    }

    public void setUserPrivateLabelId(Integer userPrivateLabelId) {
        this.userPrivateLabelId = userPrivateLabelId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getReporterNickName() {
        return reporterNickName;
    }

    public void setReporterNickName(String reporterNickName) {
        this.reporterNickName = reporterNickName;
    }

    public String getReporterAvatar() {
        return reporterAvatar;
    }

    public void setReporterAvatar(String reporterAvatar) {
        this.reporterAvatar = reporterAvatar;
    }

    public String getAbName() {
        return abName;
    }

    public void setAbName(String abName) {
        this.abName = abName;
    }

    public Integer getAbTypeId() {
        return abTypeId;
    }

    public void setAbTypeId(Integer abTypeId) {
        this.abTypeId = abTypeId;
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
}
