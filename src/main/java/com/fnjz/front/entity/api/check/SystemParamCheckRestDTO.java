package com.fnjz.front.entity.api.check;

import java.util.Date;

/**
 * app启动检查类
 * Created by yhang on 2018/8/31.
 */
public class SystemParamCheckRestDTO {

    /**
     * 系统支出表最新时间
     */
    private Date sysSpendTypeSynDate;
    /**
     * 系统收入表最新时间
     */
    private Date sysIncomeTypeSynDate;
    /**
     * 用户常用支出表最新时间
     */
    private Date userCommUseSpendTypeSynDate;
    /**
     * 用户常用收入表最新时间
     */
    private Date userCommUseIncomeTypeSynDate;

    public Date getSysSpendTypeSynDate() {
        return sysSpendTypeSynDate;
    }

    public void setSysSpendTypeSynDate(Date sysSpendTypeSynDate) {
        this.sysSpendTypeSynDate = sysSpendTypeSynDate;
    }

    public Date getSysIncomeTypeSynDate() {
        return sysIncomeTypeSynDate;
    }

    public void setSysIncomeTypeSynDate(Date sysIncomeTypeSynDate) {
        this.sysIncomeTypeSynDate = sysIncomeTypeSynDate;
    }

    public Date getUserCommUseSpendTypeSynDate() {
        return userCommUseSpendTypeSynDate;
    }

    public void setUserCommUseSpendTypeSynDate(Date userCommUseSpendTypeSynDate) {
        this.userCommUseSpendTypeSynDate = userCommUseSpendTypeSynDate;
    }

    public Date getUserCommUseIncomeTypeSynDate() {
        return userCommUseIncomeTypeSynDate;
    }

    public void setUserCommUseIncomeTypeSynDate(Date userCommUseIncomeTypeSynDate) {
        this.userCommUseIncomeTypeSynDate = userCommUseIncomeTypeSynDate;
    }
}
