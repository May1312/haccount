package com.fnjz.front.entity.api.accountbookbudget;

import java.math.BigDecimal;

/**
 * 存钱效率DTO
 * Created by yhang on 2018/7/31.
 */
public class SavingEfficiencyRestDTO extends AccountBookBudgetRestDTO {

    /**
     * 月支出
     */
    private BigDecimal spend;

    /**
     * 月收入
     */
    private BigDecimal income;

    public BigDecimal getSpend() {
        return spend;
    }

    public void setSpend(BigDecimal spend) {
        this.spend = spend;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
