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
    private List<StatisticsSpendTopDTO> statisticsSpendTopArrays;
    /**
     * 统计情绪消费集合
     */
    private List<StatisticsSpendHappinessDTO> statisticsSpendHappinessArrays;

    /**
     * 总笔数
     */
    private Integer totalCount;

    /**
     * 总金额
     */
    private BigDecimal totalMoney;

    public List<StatisticsSpendTopDTO> getStatisticsSpendTopArrays() {
        return statisticsSpendTopArrays;
    }

    public void setStatisticsSpendTopArrays(List<StatisticsSpendTopDTO> statisticsSpendTopArrays) {
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

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }
}
