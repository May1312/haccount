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
     * 总金额(按实际实际数值计算)
     */
    private BigDecimal trueTotalMoney;
    /**
     * 总金额(按绝对值计算)
     */
    private BigDecimal falseTotalMoney;

    public List<StatisticsTopDTO> getStatisticsIncomeTopArrays() {
        return statisticsIncomeTopArrays;
    }

    public void setStatisticsIncomeTopArrays(List<StatisticsTopDTO> statisticsIncomeTopArrays) {
        this.statisticsIncomeTopArrays = statisticsIncomeTopArrays;
    }

    public BigDecimal getFalseTotalMoney() {
        return falseTotalMoney;
    }

    public void setFalseTotalMoney(BigDecimal falseTotalMoney) {
        this.falseTotalMoney = falseTotalMoney;
    }

    public BigDecimal getTrueTotalMoney() {
        return trueTotalMoney;
    }

    public void setTrueTotalMoney(BigDecimal trueTotalMoney) {
        this.trueTotalMoney = trueTotalMoney;
    }
}
