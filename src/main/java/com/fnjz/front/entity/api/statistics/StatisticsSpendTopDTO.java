package com.fnjz.front.entity.api.statistics;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计支出排行榜封装类
 *
 * @author yhang
 * @date 2018/6/28
 */
public class StatisticsSpendTopDTO implements Serializable {

    /**
     * 单个类目总金额
     */
    private BigDecimal money;
    /**
     * 单个类目图标
     */
    private String icon;
    /**
     * 单个类目名称
     */
    private String spendName;
    /**
     * 总金额
     */
    private BigDecimal totalMoney;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSpendName() {
        return spendName;
    }

    public void setSpendName(String spendName) {
        this.spendName = spendName;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }
}
