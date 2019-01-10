package com.fnjz.front.entity.api.integralsactivity;

import java.math.BigDecimal;

/**
 * Created by yhang on 2019/1/10.
 */
public class UserIntegralsActivitySumRestDTO {

    /**
     * 累计投入积分数
     */
    private BigDecimal integralsForSpend;

    /**
     * 累计获得积分数
     */
    private BigDecimal integralsForIncome;

    public BigDecimal getIntegralsForSpend() {
        return integralsForSpend;
    }

    public void setIntegralsForSpend(BigDecimal integralsForSpend) {
        this.integralsForSpend = integralsForSpend;
    }

    public BigDecimal getIntegralsForIncome() {
        return integralsForIncome;
    }

    public void setIntegralsForIncome(BigDecimal integralsForIncome) {
        this.integralsForIncome = integralsForIncome;
    }
}
