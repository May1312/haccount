package com.fnjz.front.entity.api.accountbookbudget.DTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by yhang on 2018/11/21.
 */
public class SceneBaseDTO implements Serializable {

    /**
     * 支出
     */
    private BigDecimal money;

    /**
     * 时间 年-月字符串格式
     */
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //getSavingEfficiency 需要比较time值  覆写equals hashcode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        SceneBaseDTO that = (SceneBaseDTO) o;
        return Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
