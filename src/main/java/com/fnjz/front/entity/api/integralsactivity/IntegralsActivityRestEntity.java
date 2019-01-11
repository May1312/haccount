package com.fnjz.front.entity.api.integralsactivity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by yhang on 2019/1/9.
 */
public class IntegralsActivityRestEntity {

    /**
     * 当前活动最新期数id(期数也沿用此字段)
     */
    private Integer id;

    /**
     * 池内总积分数
     */
    private BigDecimal totalIntegrals;

    /**
     * 总积分数对应现金数
     */
    private BigDecimal money;

    /**
     * 活动创建时间
     */
    private Date createDate;

    /**
     * 昨日 即上期数据  成功人数
     * @return
     */
    private Integer falseTotalUsers;

    /**
     * 昨日 即上期数据  成功人数
     * @return
     */
    private Integer falseSuccessUsers;

    /**
     * 昨日 即上期数据  失败人数
     * @return
     */
    private Integer falseFailUsers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTotalIntegrals() {
        return totalIntegrals;
    }

    public void setTotalIntegrals(BigDecimal totalIntegrals) {
        this.totalIntegrals = totalIntegrals;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getFalseSuccessUsers() {
        return falseSuccessUsers;
    }

    public void setFalseSuccessUsers(Integer falseSuccessUsers) {
        this.falseSuccessUsers = falseSuccessUsers;
    }

    public Integer getFalseFailUsers() {
        return falseFailUsers;
    }

    public void setFalseFailUsers(Integer falseFailUsers) {
        this.falseFailUsers = falseFailUsers;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getFalseTotalUsers() {
        return falseTotalUsers;
    }

    public void setFalseTotalUsers(Integer falseTotalUsers) {
        this.falseTotalUsers = falseTotalUsers;
    }
}
