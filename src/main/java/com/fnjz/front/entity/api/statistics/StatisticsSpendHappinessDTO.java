package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *情绪消费统计排行榜封装类
 * @author yhang
 * @date 2018/6/28
 */
public class StatisticsSpendHappinessDTO implements Serializable {

    //愉悦度
    private Integer spendHappiness;
    //每个愉悦度对应笔数
    private Integer count;
    //总笔数
    private Integer totalCount;

    public Integer getSpendHappiness() {
        return spendHappiness;
    }

    public void setSpendHappiness(Integer spendHappiness) {
        this.spendHappiness = spendHappiness;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
