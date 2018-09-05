package com.fnjz.front.entity.api.check;

/**
 * app启动系统参数检查类
 * Created by yhang on 2018/8/31.
 */
public class SystemParamCheckRestDTO {

    /**
     * 系统支出类目版本号
     */
    private String sysSpendTypeVersion;
    /**
     * 系统收入类目版本号
     */
    private String sysIncomeTypeVersion;
    /**
     * 用户常用支出类目版本号
     */
    private String userCommUseSpendTypeVersion;
    /**
     * 用户常用收入类目版本号
     */
    private String userCommUseIncomeTypeVersion;
    /**
     * 用户常用类目排序版本号
     */
    private String userCommTypePriorityVersion;

    public String getSysSpendTypeVersion() {
        return sysSpendTypeVersion;
    }

    public void setSysSpendTypeVersion(String sysSpendTypeVersion) {
        this.sysSpendTypeVersion = sysSpendTypeVersion;
    }

    public String getSysIncomeTypeVersion() {
        return sysIncomeTypeVersion;
    }

    public void setSysIncomeTypeVersion(String sysIncomeTypeVersion) {
        this.sysIncomeTypeVersion = sysIncomeTypeVersion;
    }

    public String getUserCommUseSpendTypeVersion() {
        return userCommUseSpendTypeVersion;
    }

    public void setUserCommUseSpendTypeVersion(String userCommUseSpendTypeVersion) {
        this.userCommUseSpendTypeVersion = userCommUseSpendTypeVersion;
    }

    public String getUserCommUseIncomeTypeVersion() {
        return userCommUseIncomeTypeVersion;
    }

    public void setUserCommUseIncomeTypeVersion(String userCommUseIncomeTypeVersion) {
        this.userCommUseIncomeTypeVersion = userCommUseIncomeTypeVersion;
    }

    public String getUserCommTypePriorityVersion() {
        return userCommTypePriorityVersion;
    }

    public void setUserCommTypePriorityVersion(String userCommTypePriorityVersion) {
        this.userCommTypePriorityVersion = userCommTypePriorityVersion;
    }
}
