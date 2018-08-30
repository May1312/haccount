package com.fnjz.front.dao;

import com.fnjz.front.entity.api.statistics.StatisticsDaysRestDTO;
import com.fnjz.front.entity.api.statistics.StatisticsWeeksRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import org.jeecgframework.minidao.annotation.*;

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

    /**
     * 更新流水订单详情
     * @param warterOrderRestEntity
     * @return
     */
    Integer update(@Param("warterOrderRestEntity") WarterOrderRestEntity warterOrderRestEntity);

    /**
     * 获取流水订单详情
     * @param id
     * @return
     */
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.id=:id AND wo.delflag = 0")
    WarterOrderRestDTO findById(@Param("id")String id);

    /**
     * 查询指定时间记账天数  根据创建时间
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT DATE_FORMAT( create_date, '%Y-%m-%d' ) AS days FROM hbird_water_order WHERE account_book_id = :accountBookId AND delflag = 0 AND create_date LIKE concat(:time,'%') GROUP BY days")
    List<Map<String,String>> countChargeDays(@Param("time")String time, @Param("accountBookId")Integer accountBookId);

    /**
     * 根据记账时间
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT DATE_FORMAT( charge_date, '%Y-%m-%d' ) AS days FROM hbird_water_order WHERE account_book_id = :accountBookId AND delflag = 0 AND charge_date LIKE concat(:time,'%') GROUP BY days")
    List<Map<String,String>> countChargeDaysByChargeDays(@Param("time")String time, @Param("accountBookId")Integer accountBookId);

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
     * @param orderType
     * @return
     */
    @Sql("select sum(money) as money,charge_date as time from hbird_water_order where account_book_id= :accountBookId AND charge_date >= :endTime AND charge_date<= :beginTime and order_type = :orderType and delflag = 0 group by charge_date order by charge_date ASC;")
    List<StatisticsDaysRestDTO> statisticsForDays(@Param("beginTime")Date beginTime, @Param("endTime")Date endTime, @Param("accountBookId")Integer accountBookId, @Param("orderType")int orderType);

    /**
     * 按周统计
     * having后加入年份判断  只获取当前年份
     * @param beginWeek
     * @param endWeek
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum(wo.money) AS money, DATE_FORMAT( wo.charge_date, '%u' ) AS WEEK, DATE_FORMAT( wo.charge_date, '%Y-%u' ) AS yearweek FROM hbird_water_order AS wo WHERE wo.account_book_id = :accountBookId AND wo.order_type = :orderType AND wo.delflag = 0 AND DATE_FORMAT( wo.charge_date, '%Y-%u' ) >= concat( DATE_FORMAT( NOW( ), '%Y' ),'-',:endWeek ) AND DATE_FORMAT( wo.charge_date, '%Y-%u' ) <= concat( DATE_FORMAT( NOW( ), '%Y' ),'-',:beginWeek ) GROUP BY yearweek ORDER BY yearweek ASC;")
    List<StatisticsWeeksRestDTO> statisticsForWeeks(@Param("beginWeek")String beginWeek, @Param("endWeek")String endWeek, @Param("accountBookId")Integer accountBookId, @Param("orderType")int orderType);

    /**
     * 按月统计
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( money ) AS money,wo.charge_date as time,DATE_FORMAT( wo.charge_date, '%Y-%m' ) AS yearmonth FROM hbird_water_order AS wo WHERE wo.account_book_id = :accountBookId AND wo.order_type = :orderType AND wo.delflag = 0 AND DATE_FORMAT( wo.charge_date, '%Y-%m' ) like CONCAT(DATE_FORMAT( now( ), '%Y' ),'%') GROUP BY yearmonth ORDER BY yearmonth ASC;")
    List<StatisticsDaysRestDTO> statisticsForMonths(@Param("accountBookId")Integer accountBookId,@Param("orderType")int orderType);

    /**
     * 按日统计支出排行榜和情绪
     * @param date
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( wo.money ) as money,count(money) as moneytimes,wo.type_name,wo.spend_happiness,count(wo.spend_happiness) as count,( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order AS wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date = :date GROUP BY wo.type_id,wo.spend_happiness order by money DESC;")
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
    @Sql("SELECT SUM(wo.money) AS money, COUNT(money) AS moneytimes, wo.type_name , wo.spend_happiness, wo.charge_date, COUNT(wo.spend_happiness) AS count , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date >= :beginTime AND wo.charge_date <= :endTime GROUP BY wo.type_id,wo.spend_happiness ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForWeeksByTime(@Param("beginTime")String beginTime,@Param("endTime")String endTime, @Param("accountBookId")Integer accountBookId);

    /**
     * 按月统计支出类目排行和情绪
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM(wo.money) AS money, COUNT(wo.money) AS moneytimes, wo.type_name , wo.spend_happiness, COUNT(wo.spend_happiness) AS count , DATE_FORMAT(wo.charge_date, '%Y-%m') AS yearmonth, wo.charge_date , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date LIKE concat(DATE_FORMAT(NOW(), '%Y'), '-',:time,'%') GROUP BY wo.type_id,wo.spend_happiness ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForMonthsByTime(@Param("time")String time, @Param("accountBookId")Integer accountBookId);

    /**
     * 按日统计收入排行榜
     * @param date
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( wo.money ) as money,count(money) as moneytimes,wo.type_name,( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order AS wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 2 AND wo.delflag = 0 AND wo.charge_date = :date GROUP BY wo.type_id order by money DESC;")
    @ResultType(Map.class)
    List<Map<String,Object>> statisticsForDaysByTimeOfIncome(@Param("date")String date, @Param("accountBookId")Integer accountBookId);

    /**
     * 按周统计收入排行榜
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @return
     */
    @Sql("SELECT SUM(wo.money) AS money, COUNT(money) AS moneytimes, wo.type_name , wo.charge_date, CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 2 AND wo.delflag = 0 AND wo.charge_date >= :beginTime AND wo.charge_date <= :endTime GROUP BY wo.type_id ORDER BY money DESC;")
    @ResultType(Map.class)
    List<Map<String,Object>> statisticsForWeeksByTimeOfIncome(@Param("beginTime")String beginTime,@Param("endTime")String endTime, @Param("accountBookId")Integer accountBookId);

    /**
     * 按月统计支出类目排行和情绪
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM(wo.money) AS money, COUNT(wo.money) AS moneytimes, wo.type_name , DATE_FORMAT(wo.charge_date, '%Y-%m') AS yearmonth, wo.charge_date , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 2 AND wo.delflag = 0 AND wo.charge_date LIKE concat(DATE_FORMAT(NOW(), '%Y'), '-',:time,'%') GROUP BY wo.type_id ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForMonthsByTimeOfIncome(@Param("time")String time, @Param("accountBookId")Integer accountBookId);

    /**
     * 记录流水
     * @param charge
     * @return
     */
    @IdAutoGenerator(generator = "native")
    String insert(@Param("charge") WarterOrderRestEntity charge);

    /**
     * 查询年中--->日最大金额
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("SELECT MAX( dayList.sumMoney) from( SELECT sum(money) AS sumMoney FROM `hbird_water_order` WHERE account_book_id = :accountBookId AND delflag = 0 AND order_type = :orderType AND charge_date LIKE CONCAT( DATE_FORMAT( NOW( ), '%Y' ), '%' ) GROUP BY charge_date ) AS dayList;")
    String findMaxDayMoneyOfYear(@Param("accountBookId") Integer accountBookId, @Param("orderType")int orderType);

    /**
     * 查询年中--->周最大金额
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("SELECT max( moneyList.totalWeek) AS maxMoney FROM( SELECT sum( money ) AS totalWeek FROM `hbird_water_order` WHERE account_book_id = :accountBookId AND delflag = 0 AND order_type = :orderType AND DATE_FORMAT( charge_date, '%Y' ) LIKE DATE_FORMAT( NOW( ), '%Y' ) GROUP BY DATE_FORMAT( charge_date, '%Y-%v' ) ) AS moneyList;")
    String findMaxWeekMoneyOfYear(@Param("accountBookId") Integer accountBookId, @Param("orderType")int orderType);

    /**
     * 查询年中--->月最大金额
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("SELECT max( monthList.totalMonth) as maxMoney FROM( SELECT sum( money ) as totalMonth FROM `hbird_water_order` WHERE account_book_id = :accountBookId AND delflag = 0 AND order_type = :orderType AND DATE_FORMAT( charge_date, '%Y-%m' ) LIKE CONCAT( DATE_FORMAT( NOW( ), '%Y' ), '%' ) GROUP BY DATE_FORMAT( charge_date, '%Y-%m' ) ) AS monthList;")
    String findMaxMonthMoneyOfYear(@Param("accountBookId") Integer accountBookId, @Param("orderType")int orderType);

    /**
     * 根据create_by获取用户所有有效数据（共享账本情况下要考虑根据账本id查找）
     * 动态判断synDate 为null查询所有 反之
     * @param userInfoId
     * @param synDate
     * @return
     */
    @ResultType(WarterOrderRestDTO.class)
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.create_by=:userInfoId AND wo.delflag = 0 AND if(:synDate is null,1=1,wo.update_date>:synDate);")
    List<WarterOrderRestDTO> findAllWaterList(@Param("userInfoId") String userInfoId, @Param("synDate") Date synDate);

    /**
     * 离线数据新增或更新
     * @param warter
     */
    void saveOrUpdateOfflineData(@Param("charge") WarterOrderRestEntity warter);
}
