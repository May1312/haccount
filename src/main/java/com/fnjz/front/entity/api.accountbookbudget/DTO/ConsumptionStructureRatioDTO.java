package com.fnjz.front.entity.api.accountbookbudget.DTO;

import java.math.BigDecimal;

/**
 * 消费结构比
 * Created by yhang on 2018/8/2.
 */
public class ConsumptionStructureRatioDTO extends BudgetBaseDTO {

    /**月中食物支出**/
    private BigDecimal foodSpend;

    public BigDecimal getFoodSpend() {
        return foodSpend;
    }

    public void setFoodSpend(BigDecimal foodSpend) {
        this.foodSpend = foodSpend;
    }
}
