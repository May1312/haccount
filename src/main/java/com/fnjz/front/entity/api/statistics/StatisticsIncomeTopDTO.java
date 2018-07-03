package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 统计收入排行榜封装类
 * @author yhang
 * @date 2018/7/3
 */
public class StatisticsIncomeTopDTO implements Serializable {

    /**
     * 统计收入排行榜集合
     */
    private List<StatisticsTopDTO> statisticsIncomeTopArrays;

    /**
     * 总金额
     */
    private BigDecimal totalMoney;

    public List<StatisticsTopDTO> getStatisticsIncomeTopArrays() {
        return statisticsIncomeTopArrays;
    }

    public void setStatisticsIncomeTopArrays(List<StatisticsTopDTO> statisticsIncomeTopArrays) {
        this.statisticsIncomeTopArrays = statisticsIncomeTopArrays;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }
}
