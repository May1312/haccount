package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 统计支出排行榜和情绪消费统计封装类
 * @author yhang
 * @date 2018/6/28
 */
public class StatisticsSpendTopAndHappinessDTO implements Serializable {

    /**
     * 统计支出排行榜集合
     */
    private List<StatisticsTopDTO> statisticsSpendTopArrays;
    /**
     * 统计情绪消费集合
     */
    private List<StatisticsSpendHappinessDTO> statisticsSpendHappinessArrays;

    /**
     * 总笔数
     */
    private Integer totalCount;

    /**
     * 总金额(按实际实际数值计算)
     */
    private BigDecimal trueTotalMoney;

    /**
     * 总金额(按实际实际数值计算)
     */
    private BigDecimal falseTotalMoney;

    public List<StatisticsTopDTO> getStatisticsSpendTopArrays() {
        return statisticsSpendTopArrays;
    }

    public void setStatisticsSpendTopArrays(List<StatisticsTopDTO> statisticsSpendTopArrays) {
        this.statisticsSpendTopArrays = statisticsSpendTopArrays;
    }

    public List<StatisticsSpendHappinessDTO> getStatisticsSpendHappinessArrays() {
        return statisticsSpendHappinessArrays;
    }

    public void setStatisticsSpendHappinessArrays(List<StatisticsSpendHappinessDTO> statisticsSpendHappinessArrays) {
        this.statisticsSpendHappinessArrays = statisticsSpendHappinessArrays;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTrueTotalMoney() {
        return trueTotalMoney;
    }

    public void setTrueTotalMoney(BigDecimal trueTotalMoney) {
        this.trueTotalMoney = trueTotalMoney;
    }

    public BigDecimal getFalseTotalMoney() {
        return falseTotalMoney;
    }

    public void setFalseTotalMoney(BigDecimal falseTotalMoney) {
        this.falseTotalMoney = falseTotalMoney;
    }
}
