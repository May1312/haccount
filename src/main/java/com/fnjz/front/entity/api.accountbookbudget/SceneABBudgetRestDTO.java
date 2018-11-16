package com.fnjz.front.entity.api.accountbookbudget;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 场景账本封装类
 * Created by yhang on 2018/11/12.
 */
public class SceneABBudgetRestDTO implements Serializable {

    private Integer id;
    /**账本id*/
    private Integer accountBookId;
    /**预算金额*/
    private BigDecimal budgetMoney;
    /**场景账本 开始时间*/
    private Date beginTime;
    /**场景账本 结束时间*/
    private Date endTime;

    public BigDecimal getBudgetMoney() {
        return budgetMoney;
    }

    public void setBudgetMoney(BigDecimal budgetMoney) {
        this.budgetMoney = budgetMoney;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getAccountBookId() {
        return accountBookId;
    }

    public void setAccountBookId(Integer accountBookId) {
        this.accountBookId = accountBookId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}