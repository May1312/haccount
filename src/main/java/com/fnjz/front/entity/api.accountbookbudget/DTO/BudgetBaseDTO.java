package com.fnjz.front.entity.api.accountbookbudget.DTO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by yhang on 2018/8/2.
 */
public class BudgetBaseDTO implements Serializable {

    /**
     * 月总支出
     */
    private BigDecimal monthSpend;

    /**
     * 时间 年-月字符串格式
     */
    private String time;

    public BigDecimal getMonthSpend() {
        return monthSpend;
    }

    public void setMonthSpend(BigDecimal monthSpend) {
        this.monthSpend = monthSpend;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
