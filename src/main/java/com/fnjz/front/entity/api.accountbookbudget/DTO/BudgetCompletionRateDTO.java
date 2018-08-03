package com.fnjz.front.entity.api.accountbookbudget.DTO;

import java.math.BigDecimal;

/**
 * 预算完成率
 * Created by yhang on 2018/8/2.
 */
public class BudgetCompletionRateDTO extends BudgetBaseDTO {

    /**月预算金额**/
    private BigDecimal budgetMoney;

    public BigDecimal getBudgetMoney() {
        return budgetMoney;
    }

    public void setBudgetMoney(BigDecimal budgetMoney) {
        this.budgetMoney = budgetMoney;
    }
}
