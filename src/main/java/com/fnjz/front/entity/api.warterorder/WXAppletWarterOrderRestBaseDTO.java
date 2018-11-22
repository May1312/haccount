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
 * @Description: 账本流水表相关--->移动端多账本封装类  小程序流水封装类
 */
@Entity
@Table(name = "hbird_water_order", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class WXAppletWarterOrderRestBaseDTO implements Serializable {

    /**流水记录号*/
    private String id;
    /**单笔金额*/
    private BigDecimal money;
    /**订单类型 1:支出  2:收入*/
    private Integer orderType;
    /**愉悦度,0:高兴 1:一般 2:差*/
    private Integer spendHappiness;
    /**用户自有类目id*/
    private Integer userPrivateLabelId;
    /**三级类目_name*/
    private String typeName;
    /**记账时间*/
    private java.util.Date chargeDate;
    /**备注*/
    private String remark;
    /**三级类目icon*/
    private String icon;
    /**记录人头像*/
    private String reporterAvatar;
    /**成员数*/
    private Integer member;
    /**是否为当前用户记账标识  1:是  2:否*/
    private Integer isYour;
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getSpendHappiness() {
        return spendHappiness;
    }

    public void setSpendHappiness(Integer spendHappiness) {
        this.spendHappiness = spendHappiness;
    }

    public Integer getUserPrivateLabelId() {
        return userPrivateLabelId;
    }

    public void setUserPrivateLabelId(Integer userPrivateLabelId) {
        this.userPrivateLabelId = userPrivateLabelId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(Date chargeDate) {
        this.chargeDate = chargeDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getReporterAvatar() {
        return reporterAvatar;
    }

    public void setReporterAvatar(String reporterAvatar) {
        this.reporterAvatar = reporterAvatar;
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }

    public Integer getIsYour() {
        return isYour;
    }

    public void setIsYour(Integer isYour) {
        this.isYour = isYour;
    }
}
