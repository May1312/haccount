package com.fnjz.front.entity.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 统计参数接收类
 * Created by yhang on 2018/6/27.
 */
public class StatisticsParamsRestDTO implements Serializable {

    /**
     * 日统计参数
     */
    private Date beginTime;
    /**
     * 日统计参数
     */
    private Date endTime;
    /**
     * 统计类型
     */
    private String flag;
    /**
     * 周统计参数
     */
    private String beginWeek;
    /**
     * 周统计参数
     */
    private String endWeek;

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBeginWeek() {
        return beginWeek;
    }

    public void setBeginWeek(String beginWeek) {
        this.beginWeek = beginWeek;
    }

    public String getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(String endWeek) {
        this.endWeek = endWeek;
    }
}
