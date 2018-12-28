package com.fnjz.front.entity.api.userassets;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @version V1.0
 * @Title: Entity
 * @Description: 用户资产相关
 * @date 2018-10-20 11:11:26
 */

public class UserAssetsRestDTO implements java.io.Serializable {

    /**
     * 资产类型 当类型为1时 此字段有意义
     */
    private java.lang.Integer assetsType;
    /**
     * 金额
     */
    private BigDecimal money;
    /**
     * 更新时间
     */
    private java.util.Date updateDate;
    /**
     * 创建日期
     */
    private java.util.Date createDate;

    /**
     * 资产名称
     */
    private String assetsName;

    /**
     * 资产图标
     */
    private String icon;

    /**
     * 优先级   升序
     */
    private Integer priority;
    /**
     * 方法: 取得java.util.Date
     *
     * @return: java.util.Date  资产类型 当类型为1时 此字段有意义
     */
    @Column(name = "ASSETS_TYPE", nullable = true)
    public java.lang.Integer getAssetsType() {
        return this.assetsType;
    }

    /**
     * 方法: 设置java.util.Date
     *
     * @param: java.util.Date  资产类型 当类型为1时 此字段有意义
     */
    public void setAssetsType(java.lang.Integer assetsType) {
        this.assetsType = assetsType;
    }

    /**
     * 方法: 取得BigDecimal
     *
     * @return: BigDecimal  金额
     */
    @Column(name = "MONEY")
    public BigDecimal getMoney() {
        return this.money;
    }

    /**
     * 方法: 设置BigDecimal
     *
     * @param: BigDecimal  金额
     */
    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    /**
     * 方法: 取得java.util.Date
     *
     * @return: java.util.Date  更新时间
     */
    @Column(name = "UPDATE_DATE", nullable = true)
    public java.util.Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * 方法: 设置java.util.Date
     *
     * @param: java.util.Date  更新时间
     */
    public void setUpdateDate(java.util.Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 方法: 取得java.util.Date
     *
     * @return: java.util.Date  创建日期
     */
    @Column(name = "CREATE_DATE", nullable = true)
    public java.util.Date getCreateDate() {
        return this.createDate;
    }

    /**
     * 方法: 设置java.util.Date
     *
     * @param: java.util.Date  创建日期
     */
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }
    @Column(name = "ASSETS_NAME")
    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }
    @Column(name = "ICON")
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
