package com.fnjz.front.entity.api.warterorder;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
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
}
