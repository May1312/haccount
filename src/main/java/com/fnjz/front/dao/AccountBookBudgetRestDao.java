package com.fnjz.front.dao;

import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestDTO;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.DTO.BudgetCompletionRateDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.ConsumptionStructureRatioDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.SavingEfficiencyDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.SceneBaseDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/7/27.
 */
@MiniDao
public interface AccountBookBudgetRestDao {

    /**
     * 获取指定时间预算结果集
     *
     * @return
     */
    @ResultType(AccountBookBudgetRestEntity.class)
    @Sql("select * from hbird_accountbook_budget where account_book_id = :accountBookId and time = :time limit 0,1;")
    AccountBookBudgetRestEntity getCurrentBudget(@Param("time") String time,@Param("accountBookId") Integer accountBookId);

    /**
     * 获取指定时间预算结果集
     *
     * @return
     */
    @ResultType(AccountBookBudgetRestEntity.class)
    @Sql("select * from hbird_accountbook_budget where account_book_id = :accountBookId and time=:time ;")
    AccountBookBudgetRestEntity getBudget(@Param("accountBookId") Integer accountBookId);

    /**
     * 获取指定时间预算结果集并且限制记录创建时间不能为当前月
     *
     * @return
     */
    @ResultType(AccountBookBudgetRestEntity.class)
    @Sql("select * from hbird_accountbook_budget where account_book_id = :accountBookId and time = :time and create_date < :currentTime ;")
    AccountBookBudgetRestEntity getBudgetByTime(@Param("time") String time,@Param("accountBookId") Integer accountBookId,@Param("currentTime") String currentTime);

    /**
     * 更新预算/固定支出
     *
     * @param budget
     * @return
     */
    int update(@Param("budget") AccountBookBudgetRestEntity budget);

    /**
     * 新增预算/固定支出
     *
     * @param budget
     * @return
     */
    int insert(@Param("budget") AccountBookBudgetRestEntity budget);

    /**
     * 获取库中最新预算结果
     * @param accountBookId
     * @return
     */
    @ResultType(AccountBookBudgetRestEntity.class)
    @Sql("select * from hbird_accountbook_budget where account_book_id = :accountBookId and time<= :time ORDER BY time DESC LIMIT 0,1")
    AccountBookBudgetRestEntity getLatelyBudget(@Param("accountBookId") Integer accountBookId,@Param("time") String time);

    /**
     * 获取存钱效率
     *
     * @param rangeMonth
     * @param month
     * @param accountBookId
     * @return
     */
    @ResultType(AccountBookBudgetRestDTO.class)
    @Sql("select time from hbird_accountbook_budget where account_book_id = :accountBookId and time <= CONCAT(DATE_FORMAT(NOW(),'%Y-'),:month) and time >= :rangeMonth")
    List<AccountBookBudgetRestDTO> listBudgetByRange(@Param("rangeMonth") String rangeMonth, @Param("month") String month, @Param("accountBookId") Integer accountBookId);

    /**
     * 获取指定范围内的预算值/固定支出
     * @param rangeMonth
     * @param month
     * @param accountBookId
     * @return
     */
    @ResultType(SavingEfficiencyDTO.class)
    @Sql("SELECT  time, CASE budget_money WHEN -1 THEN null ELSE budget_money END as budget_money, CASE fixed_large_expenditure WHEN -1 THEN null ELSE fixed_large_expenditure END as fixed_large_expenditure, CASE fixed_life_expenditure WHEN -1 THEN null ELSE fixed_life_expenditure END as fixed_life_expenditure FROM `hbird_accountbook_budget` WHERE account_book_id = :accountBookId AND time <= CONCAT( DATE_FORMAT( NOW( ), '%Y-' ), :month ) AND time >= :rangeMonth AND (fixed_large_expenditure is not null OR fixed_life_expenditure is not null) AND (round(fixed_large_expenditure,0) !=-1 OR round(fixed_life_expenditure,0) !=-1);")
    List<SavingEfficiencyDTO> getRangeSavingEfficiencyStatistics(@Param("rangeMonth") String rangeMonth, @Param("month") String month, @Param("accountBookId") Integer accountBookId);

    /**
     * 获取存钱效率---->设置固定支出，但当月未记账时  会返回null
     * @param rangeMonth
     * @param month
     * @param accountBookId
     * @return
     */
    @ResultType(SavingEfficiencyDTO.class)
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS monthSpend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS monthIncome, DATE_FORMAT( charge_date, '%Y-%m' ) AS time, CASE budget.budget_money WHEN -1 THEN null ELSE budget.budget_money END as budgetMoney, CASE budget.fixed_large_expenditure WHEN -1 THEN null ELSE budget.fixed_large_expenditure END as fixedLargeExpenditure, CASE budget.fixed_life_expenditure WHEN -1 THEN null ELSE budget.fixed_life_expenditure END as fixedLifeExpenditure FROM `hbird_water_order` AS wo, (SELECT time, budget_money, fixed_large_expenditure, fixed_life_expenditure FROM `hbird_accountbook_budget` WHERE account_book_id = :accountBookId AND time <= CONCAT( DATE_FORMAT( NOW( ), '%Y-' ), :month ) AND time >= :rangeMonth AND (fixed_large_expenditure is not null OR fixed_life_expenditure is not null) AND (fixed_large_expenditure !=-1 OR fixed_life_expenditure !=-1)) AS budget WHERE DATE_FORMAT( charge_date, '%Y-%m' ) IN ( budget.time ) AND wo.account_book_id = :accountBookId  AND wo.delflag = 0 GROUP BY time;")
    List<SavingEfficiencyDTO> listSavingEfficiencyStatisticsByMonths(@Param("rangeMonth") String rangeMonth, @Param("month") String month, @Param("accountBookId") Integer accountBookId);
    
    /**
     * 获取消费结构比
     * @param accountBookId
     * @param month
     * @return
     */
    @ResultType(ConsumptionStructureRatioDTO.class)
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS monthSpend, SUM( CASE WHEN type_pid = :foodType THEN money ELSE NULL END ) AS foodSpend, DATE_FORMAT( charge_date, '%Y-%m' ) AS time FROM `hbird_water_order` AS wo WHERE DATE_FORMAT( charge_date, '%Y-%m' ) IN ( DATE_FORMAT(DATE_ADD(CONCAT(DATE_FORMAT( NOW( ), '%Y-' ), :month,'-01'),INTERVAL -1 MONTH),'%Y-%m'),DATE_FORMAT(DATE_ADD(CONCAT(DATE_FORMAT( NOW( ), '%Y-' ), :month,'-01'),INTERVAL -1 YEAR),'%Y-%m'),CONCAT(DATE_FORMAT( NOW( ), '%Y-' ), :month)) AND wo.account_book_id = :accountBookId AND wo.delflag = 0 GROUP BY time DESC;")
    List<ConsumptionStructureRatioDTO> getConsumptionStructureRatio(@Param("accountBookId") Integer accountBookId, @Param("month") String month , @Param("foodType") String foodType);

    /**
     * 获取预算完成率
     * @param rangeMonth
     * @param month
     * @param accountBookId
     * @return
     */
    @ResultType(BudgetCompletionRateDTO.class)
    @Sql("SELECT SUM(money) AS monthSpend, DATE_FORMAT( charge_date, '%Y-%m' ) AS time, CASE budget.budget_money WHEN -1 THEN null ELSE budget.budget_money END AS budgetMoney FROM `hbird_water_order` AS wo, (SELECT time, budget_money FROM `hbird_accountbook_budget` WHERE account_book_id = :accountBookId AND time <= CONCAT( DATE_FORMAT( NOW( ), '%Y-' ), :month ) AND time >= :rangeMonth AND budget_money!=-1) AS budget WHERE DATE_FORMAT( charge_date, '%Y-%m' ) IN ( budget.time ) AND wo.account_book_id = :accountBookId  AND wo.delflag = 0 AND order_type = 1 GROUP BY time;")
    List<BudgetCompletionRateDTO> listBudgetCompletionRateStatisticsByMonths(@Param("rangeMonth") String rangeMonth, @Param("month") String month, @Param("accountBookId") Integer accountBookId);

    @Sql("select * from hbird_accountbook_budget where create_by=:userInfoId and account_book_id is null order by create_date desc LIMIT 0,1;")
    AccountBookBudgetRestEntity getFixedSpend(@Param("userInfoId") String userInfoId);

    /**
     * 获取场景账本预算
     * @param accountBookId
     * @return
     */
    @Sql("select * from hbird_accountbook_budget where account_book_id=:abId;")
    AccountBookBudgetRestEntity getLatelyBudgetv2(@Param("abId") Integer accountBookId);

    /**
     * 按传入月份 统计月支出 收入
     * @param userInfoId
     * @return
     */
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS monthSpend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS monthIncome, DATE_FORMAT( charge_date, '%Y-%m' ) AS time FROM `hbird_water_order` WHERE update_by = :userInfoId and charge_date between :beginTime and :endTime AND delflag = 0 and if(:abId is null,1=1,account_book_id=:abId) GROUP BY time;")
    List<SavingEfficiencyDTO> listSavingEfficiencyStatisticsByMonthsv2(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId,@Param("abId")Integer abId);

    /**
     * v2 获取消费结构比
     * @param userInfoId
     * @param consumptionStructureRatioFoodType
     * @return
     */
    @Sql("SELECT SUM( money) AS monthSpend, SUM( CASE WHEN type_pid = :foodType THEN money ELSE 0 END ) AS foodSpend, DATE_FORMAT( charge_date, '%Y-%m' ) AS time FROM `hbird_water_order` WHERE update_by = :userInfoId AND ((charge_date between :monthBegin and :monthEnd) or (charge_date between :lastMonthBegin and :lastMonthEnd) or (charge_date between :lastYearBegin and :lastYearEnd)) and order_type = 1 AND delflag = 0 and if(:abId is null,1=1,account_book_id=:abId) GROUP BY time DESC;")
    List<ConsumptionStructureRatioDTO> getConsumptionStructureRatiov2(@Param("userInfoId") Integer userInfoId,@Param("monthBegin") String s,@Param("monthEnd") String s1,@Param("lastMonthBegin") String s2,@Param("lastMonthEnd") String s3,@Param("lastYearBegin") String s4,@Param("lastYearEnd") String s5,@Param("foodType") String consumptionStructureRatioFoodType,@Param("abId")Integer abId);

    /**
     * v2 日常账本 预算完成率
     * @param s
     * @param s1
     * @return
     */
    @Sql("SELECT SUM( money ) AS monthSpend FROM `hbird_water_order` WHERE account_book_id=:abId and charge_date between :begin and :end AND delflag = 0 AND order_type = 1;")
    String listBudgetCompletionRateStatisticsByMonthsv2(@Param("begin") String s,@Param("end") String s1,@Param("abId") Integer abId);

    /**
     * 获取范围预算
     * @param s
     * @param s1
     * @param abId
     * @return
     */
    @Sql("SELECT time, budget_money FROM `hbird_accountbook_budget` WHERE account_book_id = :abId AND time between :begin and :end AND budget_money !=- 1 ")
    List<AccountBookBudgetRestEntity> getBudgetByTimeRange(@Param("begin") String s,@Param("end") String s1,@Param("abId") Integer abId);

    /**
     * 按日统计预算完成率  场景账本
     * @param beginTime
     * @param endTime
     * @return
     */
    @Sql("SELECT sum( money ) AS money, charge_date AS time FROM hbird_water_order WHERE account_book_id=:abId AND charge_date BETWEEN :beginTime AND :endTime AND order_type = 1 AND delflag = 0 GROUP BY charge_date ORDER BY charge_date;")
    List<SceneBaseDTO> getBudgetCompletionRatev2ForSceneDays(@Param("abId") Integer abId, @Param("beginTime")String beginTime, @Param("endTime") String endTime);

    /**
     * 按周统计预算完成率  场景账本
     * @param abId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Sql("SELECT sum( wo.money ) AS money, DATE_FORMAT( wo.charge_date, '%u' ) AS time, DATE_FORMAT( wo.charge_date, '%Y-%u' ) as weekTime FROM hbird_water_order AS wo WHERE wo.account_book_id = :abId AND wo.charge_date BETWEEN :beginTime AND :endTime AND wo.order_type = 1 AND wo.delflag = 0 GROUP BY weekTime ORDER BY weekTime;")
    List<SceneBaseDTO> getBudgetCompletionRatev2ForSceneWeeks(@Param("abId") Integer abId, @Param("beginTime")String beginTime, @Param("endTime") String endTime);

    /**
     * 按月统计预算完成率  场景账本
     * @param abId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Sql("SELECT sum( money ) AS money, DATE_FORMAT( wo.charge_date, '%Y-%m' ) AS time, DATE_FORMAT( wo.charge_date, '%Y-%m' ) AS yearmonth FROM hbird_water_order AS wo WHERE wo.account_book_id = :abId AND wo.charge_date BETWEEN :beginTime AND :endTime AND wo.order_type = 1 AND wo.delflag = 0 GROUP BY yearmonth ORDER BY yearmonth;")
    List<SceneBaseDTO> getBudgetCompletionRatev2ForSceneMonths(@Param("abId") Integer abId, @Param("beginTime")String beginTime, @Param("endTime") String endTime);

    /**
     * 按年统计预算完成率  场景账本
     * @param abId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Sql("SELECT sum( money ) AS money, DATE_FORMAT( wo.charge_date, '%Y' ) AS time FROM hbird_water_order AS wo WHERE wo.account_book_id = :abId AND wo.charge_date BETWEEN :beginTime AND :endTime AND wo.order_type = 1 AND wo.delflag = 0 GROUP BY time ORDER BY time;")
    List<SceneBaseDTO> getBudgetCompletionRatev2ForSceneYears(@Param("abId") Integer abId, @Param("beginTime")String beginTime, @Param("endTime") String endTime);

    /**
     * 消费结构比  v2--->获取全部账本中的
     * @param userInfoId
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param s5
     * @param consumptionStructureRatioFoodType
     * @param abId
     * @return
     */
    @Sql("SELECT SUM( money ) AS monthSpend, SUM( CASE WHEN type_pid = :foodType THEN money ELSE 0 END ) AS foodSpend, DATE_FORMAT( charge_date, '%Y-%m' ) AS time FROM hbird_user_account_book as base1 INNER JOIN hbird_water_order as base2 on base1.account_book_id=base2.account_book_id WHERE base1.user_info_id=:userInfoId and base1.delflag=0 AND ( ( charge_date BETWEEN :monthBegin AND :monthEnd ) OR ( charge_date BETWEEN :lastMonthBegin AND :lastMonthEnd ) OR ( charge_date BETWEEN :lastYearBegin AND :lastYearEnd ) ) AND order_type = 1 AND base2.delflag = 0 GROUP BY time DESC;")
    List<ConsumptionStructureRatioDTO> getConsumptionStructureRatiov2ForPersonal(@Param("userInfoId") Integer userInfoId,@Param("monthBegin") String s,@Param("monthEnd") String s1,@Param("lastMonthBegin") String s2,@Param("lastMonthEnd") String s3,@Param("lastYearBegin") String s4,@Param("lastYearEnd") String s5,@Param("foodType") String consumptionStructureRatioFoodType,@Param("abId")Integer abId);

    /**
     * 存钱效率  v2--->获取全部账本中的
     * @param userInfoId
     * @param abId
     * @return
     */
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS monthSpend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS monthIncome, DATE_FORMAT( charge_date, '%Y-%m' ) AS time FROM hbird_user_account_book as base1 INNER JOIN `hbird_water_order` as base2 on base1.account_book_id=base2.account_book_id WHERE base1.user_info_id=:userInfoId and base1.delflag=0 AND charge_date BETWEEN :beginTime AND :endTime AND base2.delflag = 0 GROUP BY time;")
    List<SavingEfficiencyDTO> listSavingEfficiencyStatisticsByMonthsv2ForPersonal(@Param("beginTime") String beginTime,@Param("endTime") String endTime,@Param("userInfoId") String userInfoId,@Param("abId")Integer abId);
}
