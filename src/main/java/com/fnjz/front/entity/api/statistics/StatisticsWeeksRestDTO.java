package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 周统计封装类
 * Created by yhang on 2018/6/27.
 */
public class StatisticsWeeksRestDTO implements Serializable {

    private BigDecimal money;

    private String week;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
