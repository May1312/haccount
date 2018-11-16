package com.fnjz.front.entity.api.accountbookbudget;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @version V1.0
 * @Title: Entity
 * @Description: 账本-预算相关
 * @date 2018-07-26 16:14:37
 */
@Entity
@Table(name = "hbird_accountbook_budget", schema = "")
@DynamicUpdate(true)
@DynamicInsert(true)
@SuppressWarnings("serial")
public class AccountBookBudgetRestEntity implements java.io.Serializable {
    /**
     * id
     */
    private java.lang.Integer id;
    /**
     * 所属账本
     */
    private java.lang.Integer accountBookId;
    /**
     * 预算金额
     */
    private BigDecimal budgetMoney;
    /**
     * 预算设置时间
     */
    private java.lang.String time;
    /**
     * 每月固定大额支出
     */
    private BigDecimal fixedLargeExpenditure;
    /**
     * 每月固定生活支出
     */
    private BigDecimal fixedLifeExpenditure;
    /**
     * 更新时间
     */
    private java.util.Date updateDate;
    /**
     * 创建时间
     */
    private java.util.Date createDate;
    /**
     * 删除时间
     */
    private java.util.Date delDate;
    /**
     * 创建者id
     */
    private java.lang.Integer createBy;
    /**
     * 创建者名称
     */
    private java.lang.String createName;
    /**
     * 修改者id
     */
    private java.lang.Integer updateBy;
    /**
     * 修改者名称
     */
    private java.lang.String updateName;
    /**
     * 场景账本 开始时间
     */
    private Date beginTime;
    /**
     * 场景账本 结束时间
     */
    private Date endTime;

    /**
     * 方法: 取得java.lang.Integer
     *
     * @return: java.lang.Integer  id
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    public java.lang.Integer getId() {
        return this.id;
    }

    /**
     * 方法: 设置java.lang.Integer
     *
     * @param: java.lang.Integer  id
     */
    public void setId(java.lang.Integer id) {
        this.id = id;
    }

    /**
     * 方法: 取得java.lang.Integer
     *
     * @return: java.lang.Integer  所属账本
     */
    @Column(name = "ACCOUNT_BOOK_ID", nullable = true, precision = 10, scale = 0)
    public java.lang.Integer getAccountBookId() {
        return this.accountBookId;
    }

    /**
     * 方法: 设置java.lang.Integer
     *
     * @param: java.lang.Integer  所属账本
     */
    public void setAccountBookId(java.lang.Integer accountBookId) {
        this.accountBookId = accountBookId;
    }

    /**
     * 方法: 取得BigDecimal
     *
     * @return: BigDecimal  预算金额
     */
    @Column(name = "BUDGET_MONEY", nullable = true, precision = 32, scale = 2)
    public BigDecimal getBudgetMoney() {
        return this.budgetMoney;
    }

    /**
     * 方法: 设置BigDecimal
     *
     * @param: BigDecimal  预算金额
     */
    public void setBudgetMoney(BigDecimal budgetMoney) {
        this.budgetMoney = budgetMoney;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String  预算设置时间
     */
    @Column(name = "TIME", nullable = true, length = 32)
    public java.lang.String getTime() {
        return this.time;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String  预算设置时间
     */
    public void setTime(java.lang.String time) {
        this.time = time;
    }

    /**
     * 方法: 取得BigDecimal
     *
     * @return: BigDecimal  每月固定大额支出
     */
    @Column(name = "FIXED_LARGE_EXPENDITURE", nullable = true, precision = 32, scale = 2)
    public BigDecimal getFixedLargeExpenditure() {
        return this.fixedLargeExpenditure;
    }

    /**
     * 方法: 设置BigDecimal
     *
     * @param: BigDecimal  每月固定大额支出
     */
    public void setFixedLargeExpenditure(BigDecimal fixedLargeExpenditure) {
        this.fixedLargeExpenditure = fixedLargeExpenditure;
    }

    /**
     * 方法: 取得BigDecimal
     *
     * @return: BigDecimal  每月固定生活支出
     */
    @Column(name = "FIXED_LIFE_EXPENDITURE", nullable = true, precision = 32, scale = 2)
    public BigDecimal getFixedLifeExpenditure() {
        return this.fixedLifeExpenditure;
    }

    /**
     * 方法: 设置BigDecimal
     *
     * @param: BigDecimal  每月固定生活支出
     */
    public void setFixedLifeExpenditure(BigDecimal fixedLifeExpenditure) {
        this.fixedLifeExpenditure = fixedLifeExpenditure;
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
     * @return: java.util.Date  创建时间
     */
    @Column(name = "CREATE_DATE", nullable = true)
    public java.util.Date getCreateDate() {
        return this.createDate;
    }

    /**
     * 方法: 设置java.util.Date
     *
     * @param: java.util.Date  创建时间
     */
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 方法: 取得java.util.Date
     *
     * @return: java.util.Date  删除时间
     */
    @Column(name = "DEL_DATE", nullable = true)
    public java.util.Date getDelDate() {
        return this.delDate;
    }

    /**
     * 方法: 设置java.util.Date
     *
     * @param: java.util.Date  删除时间
     */
    public void setDelDate(java.util.Date delDate) {
        this.delDate = delDate;
    }

    /**
     * 方法: 取得java.lang.Integer
     *
     * @return: java.lang.Integer  创建者id
     */
    @Column(name = "CREATE_BY", nullable = true, precision = 10, scale = 0)
    public java.lang.Integer getCreateBy() {
        return this.createBy;
    }

    /**
     * 方法: 设置java.lang.Integer
     *
     * @param: java.lang.Integer  创建者id
     */
    public void setCreateBy(java.lang.Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String  创建者名称
     */
    @Column(name = "CREATE_NAME", nullable = true, length = 64)
    public java.lang.String getCreateName() {
        return this.createName;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String  创建者名称
     */
    public void setCreateName(java.lang.String createName) {
        this.createName = createName;
    }

    /**
     * 方法: 取得java.lang.Integer
     *
     * @return: java.lang.Integer  修改者id
     */
    @Column(name = "UPDATE_BY", nullable = true, precision = 10, scale = 0)
    public java.lang.Integer getUpdateBy() {
        return this.updateBy;
    }

    /**
     * 方法: 设置java.lang.Integer
     *
     * @param: java.lang.Integer  修改者id
     */
    public void setUpdateBy(java.lang.Integer updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String  修改者名称
     */
    @Column(name = "UPDATE_NAME", nullable = true, length = 64)
    public java.lang.String getUpdateName() {
        return this.updateName;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String  修改者名称
     */
    public void setUpdateName(java.lang.String updateName) {
        this.updateName = updateName;
    }

    @Column(name = "BEGIN_TIME", nullable = true)
    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    @Column(name = "END_TIME", nullable = true)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
