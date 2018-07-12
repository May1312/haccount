package com.fnjz.front.enums;

/**
 * 统计类型相关
 * Created by yhang on 2018/7/12.
 */
public enum StatisticsEnum {

    /**
     * 日统计类型
     */
    STATISTICS_FOR_DAY("1"),
    /**
     * 周统计类型
     */
    STATISTICS_FOR_WEEK("2"),
    /**
     * 月统计类型
     */
    STATISTICS_FOR_MONTH("3"),

    /**
     * 统计柱状图接口类型
     */
    STATISTICS_FOR_CHART("4"),
    /**
     * 统计柱状图接口类型
     */
    STATISTICS_FOR_TOP("5");

    private String index;

    StatisticsEnum(String index){
        this.index = index;
    }
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
