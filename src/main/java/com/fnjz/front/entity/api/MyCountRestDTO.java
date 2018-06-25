package com.fnjz.front.entity.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yhang on 2018/6/25.
 */
public class MyCountRestDTO implements Serializable {

    //本月记账天数
    private int daysCount;
    //clockInDays连续打卡天数
    private int clockInDays;
    //记账总笔数
    private int chargeTotal;

    public int getClockInDays() {
        return clockInDays;
    }

    public void setClockInDays(int clockInDays) {
        this.clockInDays = clockInDays;
    }

    public int getChargeTotal() {
        return chargeTotal;
    }

    public void setChargeTotal(int chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    public int getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(int daysCount) {
        this.daysCount = daysCount;
    }
}
