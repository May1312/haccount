package com.fnjz.front.dao;

import com.fnjz.front.entity.api.fengfengticket.FengFengTicketRestEntity;
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

    /**
     * 获取周数 对应 积分数
     * @param categoryOfBehavior
     * @param acquisitionMode
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT cycle,behavior_ticket_value as cycleAware,downtime FROM `hbird_fengfeng_ticket` where category_of_behavior = :categoryOfBehavior and acquisition_mode=:acquisitionMode and status=1 and if(uptime is null,1=1,uptime<=CURRENT_TIMESTAMP) and if(uptime is null,1=1,downtime>=CURRENT_TIMESTAMP) order by CAST(cycle AS SIGNED) ASC;")
    List<Map<String,String>> getSignInCycle(@Param("categoryOfBehavior")String categoryOfBehavior, @Param("acquisitionMode")String acquisitionMode);

    /**
     * 获取今日任务/新手任务 对应积分数
     * @param categoryOfBehavior
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT acquisition_mode as acquisitionMode, behavior_ticket_value as integralTaskAware,downtime FROM `hbird_fengfeng_ticket` where category_of_behavior = :categoryOfBehavior and status=1 and if(uptime is null,1=1,uptime<=CURRENT_TIMESTAMP) and if(uptime is null,1=1,downtime>=CURRENT_TIMESTAMP);")
    List<Map<String,Object>> getIntegralTaskAware(@Param("categoryOfBehavior")String categoryOfBehavior);

    /**
     * 根据周数获取所有参数
     * @param categoryOfBehavior
     * @param acquisitionMode
     * @param cycle
     * @return
     */
    @Sql("SELECT * FROM `hbird_fengfeng_ticket` where if(:categoryOfBehavior is null,1=1,category_of_behavior=:categoryOfBehavior) and acquisition_mode=:acquisitionMode and if(:cycle is null,1=1,cycle=:cycle) and status=1 and if(uptime is null,1=1,uptime<=CURRENT_TIMESTAMP) and if(uptime is null,1=1,downtime>=CURRENT_TIMESTAMP);")
    FengFengTicketRestEntity getFengFengTicket(@Param("categoryOfBehavior")String categoryOfBehavior, @Param("acquisitionMode")String acquisitionMode, @Param("cycle")Integer cycle);
}
