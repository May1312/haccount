package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/10/13.
 */
@MiniDao
public interface FengFengTicketRestDao {

    @ResultType(Map.class)
    @Sql("SELECT cycle,behavior_ticket_value as cycleAware FROM `hbird_fengfeng_ticket` where category_of_behavior = :categoryOfBehavior and acquisition_mode=:acquisitionMode and status=1 order by CAST(cycle AS SIGNED) ASC;")
    List<Map<String,String>> getSignInCycle(@Param("categoryOfBehavior")String categoryOfBehavior, @Param("acquisitionMode")String acquisitionMode);
}
