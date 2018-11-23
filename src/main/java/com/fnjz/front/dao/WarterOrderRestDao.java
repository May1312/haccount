package com.fnjz.front.dao;

import com.fnjz.front.entity.api.statistics.StatisticsDaysRestDTO;
import com.fnjz.front.entity.api.statistics.StatisticsWeeksRestDTO;
import com.fnjz.front.entity.api.warterorder.*;
import org.jeecgframework.minidao.annotation.*;

import java.math.BigDecimal;
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
     *
     * @param accountBookId
     * @return
     */
    @ResultType(WarterOrderRestDTO.class)
    //@Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.account_book_id=:accountBookId AND wo.delflag = 0 AND wo.charge_date between :first and :end ORDER BY wo.charge_date,wo.create_date DESC LIMIT :curpage,:itemPerPage")
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.account_book_id=:accountBookId AND wo.delflag = 0 AND wo.charge_date between :first and :end ORDER BY wo.charge_date,wo.create_date DESC;")
    List<WarterOrderRestDTO> findListForPage(@Param("first") String first,@Param("end") String end, @Param("accountBookId") String accountBookId,@Param("curPage") Integer curPage,@Param("itemPerPage") Integer itemPerPage);

    /**
     * 小程序分页
     * @return
     */
    @ResultType(WXAppletWarterOrderRestBaseDTO.class)
    @Sql("SELECT base1.id,base1.update_by as isYour, base1.money, base1.order_type, base1.spend_happiness, base1.type_name, base1.remark, base1.icon, base1.user_private_label_id, base1.charge_date, base2.avatar_url as reporter_avatar,base3.member,base3.account_book_type_id as abTypeId FROM hbird_water_order as base1 LEFT JOIN hbird_user_info as base2 on base1.update_by=base2.id LEFT JOIN hbird_account_book as base3 on base1.account_book_id=base3.id where base1.account_book_id=:accountBookId AND base1.delflag = 0 AND base1.charge_date between :first and :end order by base1.charge_date desc,base1.create_date desc LIMIT :startIndex,:pageSize")
    List<WXAppletWarterOrderRestBaseDTO> findListForPagev2(@Param("first") String first, @Param("end") String end, @Param("accountBookId") String accountBookId, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    /**
     * 查询总记录数
     *
     * @param accountBookId
     * @return
     */
    @Sql("select COALESCE(count(id),0) from hbird_water_order where account_book_id=:accountBookId AND delflag = 0 AND charge_date between :first and :end;")
    Integer getCount(@Param("first") String first,@Param("end") String end, @Param("accountBookId") String accountBookId);

    @Sql("select COALESCE(count(id),0) from hbird_water_order where account_book_id in (( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id = :userInfoId AND delflag = 0 )) AND delflag = 0 AND charge_date between :first and :end;")
    Integer getCountv2(@Param("first") String first,@Param("end") String end, @Param("userInfoId") String userInfoId);

    /**
     * 更新流水订单详情
     *
     * @param warterOrderRestEntity
     * @return
     */
    Integer update(@Param("warterOrderRestEntity") WarterOrderRestEntity warterOrderRestEntity);

    /**
     * 获取流水订单详情
     *
     * @param id
     * @return
     */
    @Sql("SELECT wo.id,wo.money,wo.account_book_id,wo.order_type,wo.is_staged,wo.spend_happiness,wo.use_degree,wo.type_pid,wo.type_pname,wo.type_id,wo.type_name,wo.picture_url,wo.create_date,wo.charge_date,wo.remark, ( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id where wo.id=:id AND wo.delflag = 0")
    WarterOrderRestDTO findById(@Param("id") String id);

    /**
     * 查询指定时间记账天数  根据创建时间
     *
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT DATE_FORMAT( create_date, '%Y-%m-%d' ) AS days FROM hbird_water_order WHERE account_book_id = :accountBookId AND delflag = 0 AND create_date LIKE concat(:time,'%') GROUP BY days")
    List<Map<String, String>> countChargeDays(@Param("time") String time, @Param("accountBookId") Integer accountBookId);

    /**
     * 根据记账时间
     *
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT DATE_FORMAT( charge_date, '%Y-%m-%d' ) AS days FROM hbird_water_order WHERE account_book_id = :accountBookId AND delflag = 0 AND charge_date LIKE concat(:time,'%') GROUP BY days")
    List<Map<String, String>> countChargeDaysByChargeDays(@Param("time") String time, @Param("accountBookId") Integer accountBookId);

    /**
     * 统计用户记账总笔数
     *
     * @param accountBookId
     * @return
     */
    @Sql("select count(*) from hbird_water_order where account_book_id=:accountBookId AND delflag = 0")
    int chargeTotal(@Param("accountBookId") Integer accountBookId);

    /**
     * 按日统计
     *
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("select sum(money) as money,charge_date as time from hbird_water_order where account_book_id= :accountBookId AND charge_date >= :endTime AND charge_date<= :beginTime and order_type = :orderType and delflag = 0 group by charge_date order by charge_date ASC;")
    List<StatisticsDaysRestDTO> statisticsForDays(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("accountBookId") Integer accountBookId, @Param("orderType") int orderType);

    /**
     * 按周统计
     * having后加入年份判断  只获取当前年份
     *
     * @param beginWeek
     * @param endWeek
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum(wo.money) AS money, DATE_FORMAT( wo.charge_date, '%u' ) AS WEEK, DATE_FORMAT( wo.charge_date, '%Y-%u' ) AS yearweek FROM hbird_water_order AS wo WHERE wo.account_book_id = :accountBookId AND wo.order_type = :orderType AND wo.delflag = 0 AND DATE_FORMAT( wo.charge_date, '%Y-%u' ) >= concat( DATE_FORMAT( NOW( ), '%Y' ),'-',:endWeek ) AND DATE_FORMAT( wo.charge_date, '%Y-%u' ) <= concat( DATE_FORMAT( NOW( ), '%Y' ),'-',:beginWeek ) GROUP BY yearweek ORDER BY yearweek ASC;")
    List<StatisticsWeeksRestDTO> statisticsForWeeks(@Param("beginWeek") String beginWeek, @Param("endWeek") String endWeek, @Param("accountBookId") Integer accountBookId, @Param("orderType") int orderType);

    /**
     * 按月统计
     *
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( money ) AS money,wo.charge_date as time,DATE_FORMAT( wo.charge_date, '%Y-%m' ) AS yearmonth FROM hbird_water_order AS wo WHERE wo.account_book_id = :accountBookId AND wo.order_type = :orderType AND wo.delflag = 0 AND DATE_FORMAT( wo.charge_date, '%Y-%m' ) like CONCAT(DATE_FORMAT( now( ), '%Y' ),'%') GROUP BY yearmonth ORDER BY yearmonth ASC;")
    List<StatisticsDaysRestDTO> statisticsForMonths(@Param("accountBookId") Integer accountBookId, @Param("orderType") int orderType);

    /**
     * 按日统计支出排行榜和情绪
     *
     * @param date
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( wo.money ) as money,count(money) as moneytimes,wo.type_name,wo.spend_happiness,count(wo.spend_happiness) as count,( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order AS wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date = :date GROUP BY wo.type_id,wo.spend_happiness order by money DESC;")
    @ResultType(Map.class)
    List<Map<String, Object>> statisticsForDaysByTime(@Param("date") String date, @Param("accountBookId") Integer accountBookId);

    /**
     * 按周统计支出排行榜和情绪
     *
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM(wo.money) AS money, COUNT(money) AS moneytimes, wo.type_name , wo.spend_happiness, wo.charge_date, COUNT(wo.spend_happiness) AS count , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date >= :beginTime AND wo.charge_date <= :endTime GROUP BY wo.type_id,wo.spend_happiness ORDER BY money DESC;")
    List<Map<String, Object>> statisticsForWeeksByTime(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("accountBookId") Integer accountBookId);

    /**
     * 按月统计支出类目排行和情绪
     *
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM(wo.money) AS money, COUNT(wo.money) AS moneytimes, wo.type_name , wo.spend_happiness, COUNT(wo.spend_happiness) AS count , DATE_FORMAT(wo.charge_date, '%Y-%m') AS yearmonth, wo.charge_date , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 1 AND wo.delflag = 0 AND wo.charge_date LIKE concat(DATE_FORMAT(NOW(), '%Y'), '-',:time,'%') GROUP BY wo.type_id,wo.spend_happiness ORDER BY money DESC;")
    List<Map<String, Object>> statisticsForMonthsByTime(@Param("time") String time, @Param("accountBookId") Integer accountBookId);

    /**
     * 按日统计收入排行榜
     *
     * @param date
     * @param accountBookId
     * @return
     */
    @Sql("SELECT sum( wo.money ) as money,count(money) as moneytimes,wo.type_name,( CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END ) AS icon FROM hbird_water_order AS wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 2 AND wo.delflag = 0 AND wo.charge_date = :date GROUP BY wo.type_id order by money DESC;")
    @ResultType(Map.class)
    List<Map<String, Object>> statisticsForDaysByTimeOfIncome(@Param("date") String date, @Param("accountBookId") Integer accountBookId);

    /**
     * 按周统计收入排行榜
     *
     * @param beginTime
     * @param endTime
     * @param accountBookId
     * @return
     */
    @Sql("SELECT SUM(wo.money) AS money, COUNT(money) AS moneytimes, wo.type_name , wo.charge_date, CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 2 AND wo.delflag = 0 AND wo.charge_date >= :beginTime AND wo.charge_date <= :endTime GROUP BY wo.type_id ORDER BY money DESC;")
    @ResultType(Map.class)
    List<Map<String, Object>> statisticsForWeeksByTimeOfIncome(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("accountBookId") Integer accountBookId);

    /**
     * 按月统计支出类目排行和情绪
     *
     * @param time
     * @param accountBookId
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM(wo.money) AS money, COUNT(wo.money) AS moneytimes, wo.type_name , DATE_FORMAT(wo.charge_date, '%Y-%m') AS yearmonth, wo.charge_date , CASE wo.order_type WHEN 1 THEN st.icon WHEN 2 THEN it.icon ELSE NULL END AS icon FROM hbird_water_order wo LEFT JOIN hbird_spend_type st ON wo.type_id = st.id LEFT JOIN hbird_income_type it ON wo.type_id = it.id WHERE wo.account_book_id = :accountBookId AND wo.order_type = 2 AND wo.delflag = 0 AND wo.charge_date LIKE concat(DATE_FORMAT(NOW(), '%Y'), '-',:time,'%') GROUP BY wo.type_id ORDER BY money DESC;")
    List<Map<String, Object>> statisticsForMonthsByTimeOfIncome(@Param("time") String time, @Param("accountBookId") Integer accountBookId);

    /**
     * 记录流水
     *
     * @param charge
     * @return
     */
    //@IdAutoGenerator(generator = "native")
    @Sql("INSERT INTO `hbird_water_order` ( `id`, `account_book_id`, `money`, `order_type`, `is_staged`, `spend_happiness`, `type_pid`, `type_id`, `type_name`, `create_date`, `charge_date`, `delflag`, `create_by`, `update_by`, `create_name`, `remark`, `user_private_label_id`,`icon` ) VALUES ( UUID( ), :charge.accountBookId, :charge.money, :charge.orderType, :charge.isStaged, :charge.spendHappiness, :charge.typePid, :charge.typeId, :charge.typeName, NOW(), :charge.chargeDate, :charge.delflag, :charge.createBy,:charge.updateBy, :charge.createName, :charge.remark, :charge.userPrivateLabelId,:charge.icon);")
    void insert(@Param("charge") WarterOrderRestNewLabel charge);

    /**
     * 查询年中--->日最大金额
     *
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("SELECT MAX( dayList.sumMoney) from( SELECT sum(money) AS sumMoney FROM `hbird_water_order` WHERE account_book_id = :accountBookId AND delflag = 0 AND order_type = :orderType AND charge_date LIKE CONCAT( DATE_FORMAT( NOW( ), '%Y' ), '%' ) GROUP BY charge_date ) AS dayList;")
    String findMaxDayMoneyOfYear(@Param("accountBookId") Integer accountBookId, @Param("orderType") int orderType);

    /**
     * 查询年中--->周最大金额
     *
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("SELECT max( moneyList.totalWeek) AS maxMoney FROM( SELECT sum( money ) AS totalWeek FROM `hbird_water_order` WHERE account_book_id = :accountBookId AND delflag = 0 AND order_type = :orderType AND DATE_FORMAT( charge_date, '%Y' ) LIKE DATE_FORMAT( NOW( ), '%Y' ) GROUP BY DATE_FORMAT( charge_date, '%Y-%v' ) ) AS moneyList;")
    String findMaxWeekMoneyOfYear(@Param("accountBookId") Integer accountBookId, @Param("orderType") int orderType);

    /**
     * 查询年中--->月最大金额
     *
     * @param accountBookId
     * @param orderType
     * @return
     */
    @Sql("SELECT max( monthList.totalMonth) as maxMoney FROM( SELECT sum( money ) as totalMonth FROM `hbird_water_order` WHERE account_book_id = :accountBookId AND delflag = 0 AND order_type = :orderType AND DATE_FORMAT( charge_date, '%Y-%m' ) LIKE CONCAT( DATE_FORMAT( NOW( ), '%Y' ), '%' ) GROUP BY DATE_FORMAT( charge_date, '%Y-%m' ) ) AS monthList;")
    String findMaxMonthMoneyOfYear(@Param("accountBookId") Integer accountBookId, @Param("orderType") int orderType);

    /**
     * 根据create_by获取用户所有有效数据（共享账本情况下要考虑根据账本id查找）
     * 动态判断synDate 为null查询所有 反之
     *
     * @param userInfoId
     * @param synDate
     * @return
     */
    @ResultType(WarterOrderRestEntity.class)
    @Sql("SELECT * FROM hbird_water_order where create_by=:userInfoId AND if(:synDate is null,1=1,update_date>:synDate);")
    List<WarterOrderRestEntity> findAllWaterList(@Param("userInfoId") String userInfoId, @Param("synDate") Date synDate);

    @ResultType(APPWarterOrderRestDTO.class)
    @Sql("SELECT base1.*,base3.nick_name as reporter_nick_name,base3.avatar_url as reporter_avatar,base2.ab_name as ab_name,base2.account_book_type_id as abTypeId FROM hbird_water_order AS base1 INNER JOIN hbird_user_info AS base3 ON base1.update_by = base3.id, ( SELECT base2.id, base2.ab_name,base2.account_book_type_id FROM ( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id = :userInfoId ) AS base1, hbird_account_book AS base2 WHERE base2.id = base1.account_book_id AND base2.STATUS = 0 ) AS base2 WHERE base1.account_book_id = base2.id AND if(:synDate is null,1=1,base1.update_date>:synDate);")
    List<APPWarterOrderRestDTO> findAllWaterListV2(@Param("userInfoId") String userInfoId, @Param("synDate") Date synDate);
    /**
     * 只返回有效记录
     *
     * @param userInfoId
     * @param synDate
     * @return
     */
    @ResultType(WarterOrderRestEntity.class)
    @Sql("SELECT * FROM hbird_water_order where create_by=:userInfoId AND if(:synDate is null,1=1,update_date>:synDate) and delflag=0;")
    List<WarterOrderRestEntity> findAllWaterListOfNoDel(@Param("userInfoId") String userInfoId, @Param("synDate") Date synDate);

    /**
     * 返回有效记录---->多账本 追加修改者昵称头像+账本名称
     * @param userInfoId
     * @param synDate
     * @return
     */
    @ResultType(APPWarterOrderRestDTO.class)
    @Sql("SELECT base1.*,base3.nick_name as reporter_nick_name,base3.avatar_url as reporter_avatar,base2.ab_name as ab_name,base2.account_book_type_id as abTypeId FROM hbird_water_order AS base1 INNER JOIN hbird_user_info AS base3 ON base1.update_by = base3.id, ( SELECT base2.id, base2.ab_name,base2.account_book_type_id FROM ( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id = :userInfoId and delflag=0 ) AS base1, hbird_account_book AS base2 WHERE base2.id = base1.account_book_id AND base2.STATUS = 0 ) AS base2 WHERE base1.account_book_id = base2.id AND if(:synDate is null,1=1,base1.update_date>:synDate) and base1.delflag=0;")
    List<APPWarterOrderRestDTO> findAllWaterListOfNoDelV2(@Param("userInfoId") String userInfoId, @Param("synDate") Date synDate);

    /**
     * 离线数据新增或更新
     *
     * @param warter
     */
    void saveOrUpdateOfflineData(@Param("charge") WarterOrderRestNewLabel warter);

    /**
     * 根据传入时间统计 收支总额
     * @param initDate
     * @return
     */
    @Sql("SELECT a.income - a.spend FROM ( SELECT ( CASE WHEN income.income IS NULL THEN 0 ELSE income.income END ) AS income, ( CASE WHEN spend.spend IS NULL THEN 0 ELSE spend.spend END ) AS spend FROM ( SELECT SUM( money ) AS spend FROM hbird_water_order WHERE create_by = :userInfoId AND delflag = 0 AND order_type = 1 AND create_date >= :initDate ) spend, ( SELECT SUM( money ) AS income FROM hbird_water_order WHERE create_by = :userInfoId AND delflag = 0 AND order_type = 2 AND create_date >= :initDate ) income ) AS a;")
    String getTotalByDate(@Param("initDate") Date initDate,@Param("userInfoId") String userInfoId);

    /**
     * 根据账本统计收入支出
     * @param accountBookId
     * @return
     */
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS spend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS income FROM `hbird_water_order` WHERE account_book_id = :accountBookId and charge_date BETWEEN :first AND :end AND delflag = 0;")
    Map<String,BigDecimal> getAccount(@Param("first") String first,@Param("end") String end,@Param("accountBookId") String accountBookId);

    /**
     * 统计总收入支出
     * @return
     */
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS spend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS income FROM `hbird_water_order` as base2,(select account_book_id from hbird_user_account_book where user_info_id=:userInfoId and delflag=0) as base1 WHERE base2.account_book_id = base1.account_book_id AND charge_date BETWEEN :first AND :end AND delflag = 0;")
    Map<String,BigDecimal> getAccountForAll(@Param("first") String first,@Param("end") String end,@Param("userInfoId") String userInfoId);

    /**
     * 统计场景账本收入支出
     * @param accountBookId
     * @return
     */
    @Sql("SELECT SUM( money ) AS spend FROM `hbird_water_order` WHERE account_book_id = :accountBookId and order_type=1 AND delflag = 0;")
    Map<String,BigDecimal> getAccountv2(@Param("accountBookId") Integer accountBookId);

    @ResultType(WXAppletWarterOrderRestBaseDTO.class)
    @Sql("SELECT id, money, order_type, spend_happiness, type_name, remark, icon, user_private_label_id, charge_date FROM hbird_water_order WHERE account_book_id =:accountBookId AND charge_date BETWEEN :first AND :end AND delflag = 0 ORDER BY charge_date DESC, create_date DESC LIMIT :startIndex,:pageSize")
    List<WXAppletWarterOrderRestBaseDTO> findListForPagev2NOAvatar(@Param("first") String first,@Param("end") String end, @Param("accountBookId") String accountBookId,@Param("startIndex") Integer startIndex,@Param("pageSize") Integer pageSize);

    /**
     * 获取订单详情  + 头像+账本名称
     * @param id
     * @return
     */
    @Sql("SELECT base1.id, base1.money, base1.order_type, base1.spend_happiness, base1.type_name, base1.remark, base1.icon, base1.user_private_label_id, base1.charge_date,base1.create_date,base1.update_date, base2.avatar_url AS reporter_avatar, base2.nick_name AS reporter_nick_name, base3.ab_name,base3.account_book_type_id as abTypeId FROM hbird_water_order AS base1 LEFT JOIN hbird_user_info AS base2 ON base1.update_by = base2.id LEFT JOIN hbird_account_book AS base3 ON base1.account_book_id = base3.id WHERE base1.id = :id;")
    WXAppletWarterOrderRestInfoDTO findByIdv2(@Param("id") String id);

    /**
     * 获取订单详情 +账本名称
     * @param id
     * @return
     */
    @Sql("SELECT base1.id, base1.money, base1.order_type, base1.spend_happiness, base1.type_name, base1.remark, base1.icon, base1.user_private_label_id, base1.charge_date,base1.create_date,base1.update_date, base3.ab_name,base3.account_book_type_id as abTypeId FROM hbird_water_order AS base1 LEFT JOIN hbird_account_book AS base3 ON base1.account_book_id = base3.id WHERE base1.id = :id;")
    WXAppletWarterOrderRestInfoDTO findByIdv2NoAvatar(@Param("id")String id);

    /**
     * 删除账本下记录
     * @param abId
     */
    @Sql("update hbird_water_order set delflag=1 and del_date=now() and update_date=now() where account_book_id=:abId;")
    void deleteWaterOrderByABId(@Param("abId") Integer abId);

    /**
     * 总账本读取 返回组员数
     * @param startIndex
     * @param pageSize
     * @return
     */
    @Sql("SELECT base1.id,base1.update_by as isYour, base1.money, base1.order_type, base1.spend_happiness, base1.type_name, base1.remark, base1.icon, base1.user_private_label_id, base1.charge_date, base2.avatar_url as reporter_avatar,base3.member FROM hbird_water_order as base1 LEFT JOIN hbird_user_info as base2 on base1.update_by=base2.id LEFT JOIN hbird_account_book as base3 on base1.account_book_id=base3.id,( SELECT account_book_id FROM hbird_user_account_book WHERE user_info_id = :userInfoId and delflag=0 ) AS base4 where base1.account_book_id=base4.account_book_id AND base1.delflag = 0 AND base1.charge_date between :first and :end order by base1.charge_date desc,base1.create_date desc LIMIT :startIndex,:pageSize")
    List<WXAppletWarterOrderRestBaseDTO> findListForPagev2All(@Param("first") String first, @Param("end") String end, @Param("userInfoId") String userInfoId, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    /**
     * v2 按日统计  修改者为当前用户的记录
     * @param beginTime
     * @param endTime
     * @param userInfoId
     * @param orderType
     * @return
     */
    @Sql("select sum(money) as money,charge_date as time from hbird_water_order where update_by= :userInfoId AND charge_date between :beginTime AND :endTime and order_type = :orderType and delflag = 0 group by charge_date order by charge_date;")
    List<StatisticsDaysRestDTO> statisticsForDaysv2(@Param("beginTime") Date beginTime,@Param("endTime") Date endTime,@Param("userInfoId") String userInfoId,@Param("orderType") int orderType);

    /**
     * v2 查询年中最大金额 根据updateBy
     * @param userInfoId
     * @param orderType
     * @return
     */
    @Sql("SELECT MAX( dayList.sumMoney) from( SELECT sum(money) AS sumMoney FROM `hbird_water_order` WHERE update_by = :userInfoId AND delflag = 0 and charge_date between :beginTime and :endTime AND order_type = :orderType) AS dayList;")
    String findMaxDayMoneyOfYearv2(@Param("beginTime") Date beginTime,@Param("endTime")Date endTime,@Param("userInfoId") String userInfoId,@Param("orderType") int orderType);

    /**
     * v2 统计周
     * @param userInfoId
     * @param orderType
     * @return
     */
    @Sql("SELECT sum(wo.money) AS money, DATE_FORMAT( wo.charge_date, '%u' ) AS WEEK, DATE_FORMAT( wo.charge_date, '%Y-%u' ) AS yearweek FROM hbird_water_order AS wo WHERE wo.update_by = :userInfoId AND wo.charge_date between :beginTime and :endTime AND wo.order_type = :orderType AND wo.delflag = 0 GROUP BY yearweek ORDER BY yearweek;")
    List<StatisticsWeeksRestDTO> statisticsForWeeksv2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId,@Param("orderType") int orderType);

    /**
     * v2 年中周最大金额
     * @param userInfoId
     * @param orderType
     * @return
     */
    @Sql("SELECT max( moneyList.totalWeek) AS maxMoney FROM( SELECT sum( money ) AS totalWeek FROM `hbird_water_order` WHERE update_by = :userInfoId AND charge_date between :beginTime and :endTime AND delflag = 0 AND order_type = :orderType GROUP BY DATE_FORMAT( charge_date, '%Y-%v' ) ) AS moneyList;")
    String findMaxWeekMoneyOfYearv2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId,@Param("orderType") int orderType);

    /**
     * v2 统计月
     * @param userInfoId
     * @param orderType
     * @return
     */
    @Sql("SELECT sum( money ) AS money,wo.charge_date as time,DATE_FORMAT( wo.charge_date, '%Y-%m' ) AS yearmonth FROM hbird_water_order AS wo WHERE wo.update_by = :userInfoId AND wo.charge_date between :beginTime and :endTime AND wo.order_type = :orderType AND wo.delflag = 0 GROUP BY yearmonth ORDER BY yearmonth;")
    List<StatisticsDaysRestDTO> statisticsForMonthsv2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId,@Param("orderType") int orderType);

    /**
     * v2 年中月最大金额
     * @param userInfoId
     * @param orderType
     * @return
     */
    @Sql("SELECT max( monthList.totalMonth) as maxMoney FROM( SELECT sum( money ) as totalMonth FROM `hbird_water_order` WHERE update_by = :userInfoId AND charge_date between :beginTime and :endTime AND delflag = 0 AND order_type = :orderType GROUP BY DATE_FORMAT( charge_date, '%Y-%m' ) ) AS monthList;")
    String findMaxMonthMoneyOfYearv2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId,@Param("orderType") int orderType);

    /**
     * v2 按日统计支出排行榜和情绪
     * @param date
     * @param userInfoId
     * @return
     */
    @Sql("SELECT sum( money ) AS money, count( money ) AS moneytimes, type_name, spend_happiness, count( spend_happiness ) AS count, icon FROM hbird_water_order WHERE update_by = :userInfoId AND charge_date = :date AND order_type = 1 AND delflag = 0 GROUP BY user_private_label_id, spend_happiness ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForDaysByTimev2(@Param("date") String date,@Param("userInfoId") String userInfoId);

    /**
     * v2 按周统计支出排行榜和情绪
     * @param beginTime
     * @param endTime
     * @param userInfoId
     * @return
     */
    @Sql("SELECT SUM(money ) AS money, COUNT( money ) AS moneytimes, type_name, spend_happiness,COUNT( spend_happiness ) AS count, icon FROM hbird_water_order WHERE update_by = :userInfoId AND charge_date between :beginTime and :endTime AND order_type = 1 AND delflag = 0 GROUP BY user_private_label_id ,spend_happiness ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForWeeksByTimev2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId);

    /**
     * v2 按月统计支出排行榜和情绪
     * @param userInfoId
     * @return
     */
    @Sql("SELECT SUM( money ) AS money, COUNT( money ) AS moneytimes, type_name, spend_happiness, COUNT( spend_happiness ) AS count, icon FROM hbird_water_order WHERE update_by = :userInfoId AND charge_date BETWEEN :beginTime AND :endTime AND order_type = 1 AND delflag = 0 GROUP BY user_private_label_id, spend_happiness ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForMonthsByTimev2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId);

    /**
     * v2 收入日统计
     * @param date
     * @param userInfoId
     * @return
     */
    @Sql("SELECT sum( money ) AS money, count( money ) AS moneytimes, type_name, icon FROM hbird_water_order WHERE update_by = :userInfoId AND charge_date = :date AND order_type = 2 AND delflag = 0 GROUP BY user_private_label_id ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForDaysByTimeOfIncomev2(@Param("date") String date,@Param("userInfoId") String userInfoId);

    /**
     * v2 收入周统计
     * @param beginTime
     * @param endTime
     * @param userInfoId
     * @return
     */
    @Sql("SELECT SUM( money ) AS money, COUNT( money ) AS moneytimes, type_name, icon FROM hbird_water_order WHERE update_by=:userInfoId AND order_type = 2 AND delflag = 0 AND charge_date between :beginTime AND :endTime GROUP BY user_private_label_id ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForWeeksByTimeOfIncomev2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId);

    /**
     * v2 收入月统计
     * @param userInfoId
     * @return
     */
    @Sql("SELECT SUM( money ) AS money, COUNT( money ) AS moneytimes, type_name, icon FROM hbird_water_order WHERE update_by = :userInfoId AND charge_date between :beginTime and :endTime AND order_type = 2 AND delflag = 0 GROUP BY user_private_label_id ORDER BY money DESC;")
    List<Map<String,Object>> statisticsForMonthsByTimeOfIncomev2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId);

    /**
     * 记账天数
     * @param userInfoId
     * @return
     */
    @Sql("SELECT count(id) FROM hbird_water_order WHERE update_by = :userInfoId AND delflag = 0 AND charge_date between :begin and :end; ")
    Integer countChargeDaysByChargeDaysv2(@Param("begin") String begin,@Param("end") String end,@Param("userInfoId") String userInfoId);

    /**
     * 统计记账笔数
     * @param userInfoId
     * @return
     */
    @Sql("select count(*) from hbird_water_order where update_by=:userInfoId AND delflag = 0;")
    int chargeTotalv2(@Param("userInfoId")String userInfoId);
}
