package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 日/月统计封装类
 * Created by yhang on 2018/6/27.
 */
public class StatisticsDaysRestDTO implements Serializable {

    private BigDecimal money;

    private Date time;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
