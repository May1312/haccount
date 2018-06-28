package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.util.ArrayList;
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
    private List<StatisticsSpendTopDTO> statisticsSpendTop = new ArrayList<>();
    /**
     * 统计情绪消费集合
     */
    private List<StatisticsSpendHappinessDTO> statisticsSpendHappiness = new ArrayList<>();

    public List<StatisticsSpendTopDTO> getStatisticsSpendTop() {
        return statisticsSpendTop;
    }

    public void setStatisticsSpendTop(List<StatisticsSpendTopDTO> statisticsSpendTop) {
        this.statisticsSpendTop = statisticsSpendTop;
    }

    public List<StatisticsSpendHappinessDTO> getStatisticsSpendHappiness() {
        return statisticsSpendHappiness;
    }

    public void setStatisticsSpendHappiness(List<StatisticsSpendHappinessDTO> statisticsSpendHappiness) {
        this.statisticsSpendHappiness = statisticsSpendHappiness;
    }
}
