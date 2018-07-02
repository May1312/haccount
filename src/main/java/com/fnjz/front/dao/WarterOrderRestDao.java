package com.fnjz.front.dao;

import com.fnjz.front.entity.api.statistics.StatisticsDaysRestDTO;
import com.fnjz.front.entity.api.statistics.StatisticsWeeksRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/6/14.
 */
@MiniDao
public interface WarterOrderRestDao {

    /**
     * 分页查询流水
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(WarterOrderRestDTO.class)
    //@Sql("SELECT * FROM hbird_water_order where account_book_id=:accountBookId AND delflag = 0 AND create_date like concat(:time,'%') ORDER BY create_date DESC")
    //List<WarterOrderRestDTO> findListForPage(@Param("time") String time, @Param("accountBookId") String accountBookId, @Param("curPage") Integer curPage, @Param("pageSize") Integer pageSize);
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.account_book_id=:accountBookId AND wo.delflag = 0 AND wo.charge_date like concat(:time,'%') ORDER BY wo.charge_date DESC,wo.create_date DESC")
    List<WarterOrderRestDTO> findListForPage(@Param("time") String time, @Param("accountBookId") String accountBookId);
    /**
     * 查询总记录数
     * @param time
     * @param accountBookId
     * @return
     */
    @Sql("select count(*) from hbird_water_order where account_book_id=:accountBookId AND delflag = 0 AND create_date like concat(:time,'%')")
    Integer getCount(@Param("time") String time , @Param("accountBookId") String accountBookId);


    Integer update(@Param("warterOrderRestEntity") WarterOrderRestEntity warterOrderRestEntity);

    /**
     * 获取流水订单详情
     * @param id
     * @return
     */
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.id=:id AND wo.delflag = 0")
    WarterOrderRestDTO findById(@Param("id")String id);

    /**
     * 查询指定时间记账天数
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT DATE_FORMAT( create_date, '%Y-%m-%d' ) AS days FROM hbird_water_order WHERE account_book_id = :accountBookId AND delflag = 0 AND create_date LIKE concat(:time,'%') GROUP BY days")
    List<Map<String,String>> countChargeDays(@Param("time")String time, @Param("accountBookId")Integer accountBookId);

    /**
     * 统计用户记账总笔数
     * @param accountBookId
     * @return
     */
    @Sql("select count(*) from hbird_water_order where account_book_id=:accountBookId AND delflag = 0")
    int chargeTotal(@Param("accountBookId")Integer accountBookId);

    /**
     * 按日统计
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @return
     */
    @Sql("select sum(money) as money,charge_date as time from hbird_water_order where account_book_id= :accountBookId AND charge_date >= :endTime AND charge_date<= :beginTime and order_type = 1 and delflag = 0 group by charge_date order by charge_date DESC;")
    List<StatisticsDaysRestDTO> statisticsForDays(@Param("beginTime")Date beginTime, @Param("endTime")Date endTime, @Param("accountBookId")Integer accountBookId);

    /**
     * 按周统计
     * having后加入年份判断  只获取当前年份
     * @param beginWeek
     * @param endWeek
     * @param accountBookId
     * @return
     */
    @Sql("select sum(money) as money,DATE_FORMAT(charge_date,'%u') as week ,DATE_FORMAT(charge_date,'%Y-%u') as yearweek from hbird_water_order as wo where wo.account_book_id= :accountBookId and wo.order_type = 1 and wo.delflag = 0 GROUP BY yearweek having yearweek>=concat(DATE_FORMAT(NOW(),'%Y'),-:endWeek) and yearweek<=concat(DATE_FORMAT(NOW(),'%Y'),-:beginWeek) order by yearweek DESC;")
    List<StatisticsWeeksRestDTO> statisticsForWeeks(@Param("beginWeek")String beginWeek, @Param("endWeek")String endWeek, @Param("accountBookId")Integer accountBookId);

    /**
     * 按月统计
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( money ) AS money,wo.charge_date as time,DATE_FORMAT( wo.charge_date, '%Y-%m' ) AS yearmonth,DATE_FORMAT( now( ), '%Y' ) AS year FROM hbird_water_order AS wo WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 GROUP BY yearmonth HAVING yearmonth >=year ORDER BY yearmonth DESC;")
    List<StatisticsDaysRestDTO> statisticsForMonths(@Param("accountBookId")Integer accountBookId);

    /**
     * 按日统计支出排行榜和情绪
     * @param date
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( wo.money ) as money,count(money) as moneytimes,wo.type_name,wo.spend_happiness,count(wo.spend_happiness) as count,( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order AS wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date = :date GROUP BY wo.type_id order by money DESC;")
    @ResultType(Map.class)
    List<Map<String,Object>> statisticsForDaysByTime(@Param("date")String date, @Param("accountBookId")Integer accountBookId);

    /**
     * 按周统计支出排行榜和情绪
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM(wo.money) AS money, COUNT(money) AS moneytimes, wo.type_name , wo.spend_happiness, wo.charge_date, COUNT(wo.spend_happiness) AS count , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date >= :beginTime AND wo.charge_date <= :endTime GROUP BY wo.type_id ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForWeeksByTime(@Param("beginTime")String beginTime,@Param("endTime")String endTime, @Param("accountBookId")Integer accountBookId);

    /**
     * 按月统计支出类目排行和情绪
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  //like concat(:time,'%')
    @Sql("SELECT SUM(wo.money) AS money, COUNT(wo.money) AS moneytimes, wo.type_name , wo.spend_happiness, COUNT(wo.spend_happiness) AS count , DATE_FORMAT(wo.charge_date, '%Y-%m') AS yearmonth, wo.charge_date , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date LIKE concat(DATE_FORMAT(NOW(), '%Y'), '-',:time,'%') GROUP BY wo.type_id ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForMonthsByTime(@Param("time")String time, @Param("accountBookId")Integer accountBookId);
}
