package com.fnjz.front.entity.api.warterorder;

import java.util.Date;

/**
 * @Description: 账本流水表相关--->移动端多账本封装类  小程序流水详情封装类
 */

public class WXAppletWarterOrderRestInfoDTO extends WXAppletWarterOrderRestBaseDTO{

    /**修改者信息--昵称*/
    private String reporterNickName;

    /**账本名称*/
    private String abName;

    /**创建时间*/
    private Date createDate;

    /**更新时间*/
    private Date updateDate;

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

    public String getReporterNickName() {
        return reporterNickName;
    }

    public void setReporterNickName(String reporterNickName) {
        this.reporterNickName = reporterNickName;
    }

    public String getAbName() {
        return abName;
    }

    public void setAbName(String abName) {
        this.abName = abName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public Integer getAbTypeId() {
        return abTypeId;
    }

    @Override
    public void setAbTypeId(Integer abTypeId) {
        this.abTypeId = abTypeId;
    }

    public Integer getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(Integer assetsId) {
        this.assetsId = assetsId;
    }

    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }
}
