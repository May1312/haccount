package com.fnjz.front.entity.api.accountbook;

import javax.persistence.*;
import java.util.Date;

/**
 * @version V1.0
 * @Title: Entity
 * @Description: 账本表相关
 * @date 2018-05-30 14:08:15
 */
@Entity
@Table(name = "hbird_account_book", schema = "")
public class AccountBookRestDTO implements java.io.Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * 账本名称
     */
    private String abName;
    /**
     * 更新时间
     */
    private Date updateDate;
    /**
     * 账本类型名称
     */
    private String abTypeName;
    /**
     * 成员数
     */
    private Integer member;
    /**
     * 是否为默认账本 1:默认账本 2:非默认
     */
    private Integer defaultFlag;
    /**
     * 当前用户类型
     */
    private Integer userType;
    /**账本预算类型*/
    private Integer typeBudget;
    /**账本图标*/
    private String icon;

    /**账本类型id*/
    private Integer abTypeId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    public Integer getId() {
        return this.id;
    }

    /**
     * 方法: 设置java.lang.Integer
     *
     * @param: java.lang.Integer  id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String  账本名称
     */
    @Column(name = "AB_NAME", nullable = true, length = 64)
    public String getAbName() {
        return this.abName;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String  账本名称
     */
    public void setAbName(String abName) {
        this.abName = abName;
    }

    /**
     * 方法: 取得java.util.Date
     *
     * @return: java.util.Date  更新时间
     */
    @Column(name = "UPDATE_DATE", nullable = true)
    public Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * 方法: 设置java.util.Date
     *
     * @param: java.util.Date  更新时间
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getAbTypeName() {
        return abTypeName;
    }

    public void setAbTypeName(String abTypeName) {
        this.abTypeName = abTypeName;
    }

    public Integer getTypeBudget() {
        return typeBudget;
    }

    public void setTypeBudget(Integer typeBudget) {
        this.typeBudget = typeBudget;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getAbTypeId() {
        return abTypeId;
    }

    public void setAbTypeId(Integer abTypeId) {
        this.abTypeId = abTypeId;
    }
}
