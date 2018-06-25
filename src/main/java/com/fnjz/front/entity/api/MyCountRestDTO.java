package com.fnjz.front.entity.api;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yhang on 2018/6/25.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL) //为空字段不返回
public class MyCountRestDTO implements Serializable {

    //本月记账天数
    private String daysCount;
    //连续打卡天数
    private int clockInDays;
    //最后打卡的时间戳
    private Date clockInTime;
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

    public String getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(String daysCount) {
        this.daysCount = daysCount;
    }

    public Date getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(Date clockInTime) {
        this.clockInTime = clockInTime;
    }
}
