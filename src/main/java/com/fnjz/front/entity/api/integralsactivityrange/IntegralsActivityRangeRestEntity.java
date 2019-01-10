package com.fnjz.front.entity.api.integralsactivityrange;

import java.math.BigDecimal;

/**
 * Created by yhang on 2019/1/9.
 */
public class IntegralsActivityRangeRestEntity {

    /**积分范围id*/
    private Integer id;

    /**积分数*/
    private BigDecimal integrals;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getIntegrals() {
        return integrals;
    }

    public void setIntegrals(BigDecimal integrals) {
        this.integrals = integrals;
    }
}
