package com.fnjz.front.entity.api.accountbookbudget.DTO;

import java.math.BigDecimal;

/**
 * Created by yhang on 2018/8/2.
 */
public class SavingEfficiencyDTO extends BudgetBaseDTO {
    /**月收入*/
    private BigDecimal monthIncome;
    /**每月固定大额支出*/
    private BigDecimal fixedLargeExpenditure;
    /**每月固定生活支出*/
    private BigDecimal fixedLifeExpenditure;

    public BigDecimal getMonthIncome() {
        return monthIncome;
    }

    public void setMonthIncome(BigDecimal monthIncome) {
        this.monthIncome = monthIncome;
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
