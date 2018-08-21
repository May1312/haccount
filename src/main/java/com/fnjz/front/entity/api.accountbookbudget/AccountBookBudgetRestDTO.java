package com.fnjz.front.entity.api.accountbookbudget;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by yhang on 2018/7/27.
 */
public class AccountBookBudgetRestDTO implements Serializable {

    /**预算金额*/
    private BigDecimal budgetMoney;
    /**预算设置时间*/
    private java.lang.String time;
    /**每月固定大额支出*/
    private BigDecimal fixedLargeExpenditure;
    /**每月固定生活支出*/
    private BigDecimal fixedLifeExpenditure;

    public BigDecimal getBudgetMoney() {
        return budgetMoney;
    }

    public void setBudgetMoney(BigDecimal budgetMoney) {
        this.budgetMoney = budgetMoney;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getFixedLargeExpenditure() {
        return fixedLargeExpenditure;
    }

    public void setFixedLargeExpenditure(BigDecimal fixedLargeExpenditure) {
        this.fixedLargeExpenditure = fixedLargeExpenditure;
    }

    public BigDecimal getFixedLifeExpenditure() {
        return fixedLifeExpenditure;
    }

    public void setFixedLifeExpenditure(BigDecimal fixedLifeExpenditure) {
        this.fixedLifeExpenditure = fixedLifeExpenditure;
    }
}