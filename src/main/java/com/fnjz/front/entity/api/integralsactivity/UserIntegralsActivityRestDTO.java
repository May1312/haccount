package com.fnjz.front.entity.api.integralsactivity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by yhang on 2019/1/9.
 */
public class UserIntegralsActivityRestDTO {

    /**
     * 扣除的积分数
     */
    private BigDecimal integrals;

    /**
     * 实际获取的积分数
     */
    private BigDecimal getIntegrals;

    /**
     * status 活动参与状态  1:已报名 2:已记账 3:挑战成功 4:挑战失败
     */
    private Integer status;

    /**
     * 报名日期
     */
    private Date createDate;

    /**
     * 记账日期
     */
    private Date chargeDate;

    /**
     * 本期结束日期
     */
    private Date endDate;

    /**
     * 日期
     */
    private Date date;

    public BigDecimal getIntegrals() {
        return integrals;
    }

    public void setIntegrals(BigDecimal integrals) {
        this.integrals = integrals;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(Date chargeDate) {
        this.chargeDate = chargeDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getGetIntegrals() {
        return getIntegrals;
    }

    public void setGetIntegrals(BigDecimal getIntegrals) {
        this.getIntegrals = getIntegrals;
    }
}
