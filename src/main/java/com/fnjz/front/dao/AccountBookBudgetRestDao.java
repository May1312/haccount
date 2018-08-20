package com.fnjz.front.dao;

import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestDTO;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.DTO.BudgetCompletionRateDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.ConsumptionStructureRatioDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.SavingEfficiencyDTO;
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
    @Sql("select * from hbird_accountbook_budget where account_book_id = :accountBookId and time = :time;")
    AccountBookBudgetRestEntity getCurrentBudget(@Param("time") String time,@Param("accountBookId") Integer accountBookId);

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
    @Sql("select * from hbird_accountbook_budget where account_book_id = :accountBookId and time<= :time ORDER BY time DESC LIMIT 1")
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
     * 获取存钱效率
     * sql写的过于复杂了，问题in :查不到数据时如何在执行一次 sql
     * @param rangeMonth
     * @param month
     * @param accountBookId
     * @return
     */
    @ResultType(SavingEfficiencyDTO.class)
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS monthSpend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS monthIncome, DATE_FORMAT( charge_date, '%Y-%m' ) AS time, budget.budget_money as budgetMoney, budget.fixed_large_expenditure as fixedLargeExpenditure, budget.fixed_life_expenditure as fixedLifeExpenditure FROM `hbird_water_order` AS wo, (SELECT time, budget_money, fixed_large_expenditure, fixed_life_expenditure FROM `hbird_accountbook_budget` WHERE account_book_id = :accountBookId AND time <= CONCAT( DATE_FORMAT( NOW( ), '%Y-' ), :month ) AND time >= :rangeMonth AND (fixed_large_expenditure is not null OR fixed_life_expenditure is not null) AND (fixed_large_expenditure !=-1 OR fixed_life_expenditure !=-1)) AS budget WHERE DATE_FORMAT( charge_date, '%Y-%m' ) IN ( budget.time ) AND wo.account_book_id = :accountBookId  AND wo.delflag = 0 GROUP BY time;")
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

}
