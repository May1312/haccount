package com.fnjz.front.dao;

import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestDTO;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.SavingEfficiencyRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/7/27.
 */
@MiniDao
public interface AccountBookBudgetRestDao {

    /**
     * 获取当月预算结果集
     *
     * @param budget
     * @return
     */
    @ResultType(AccountBookBudgetRestEntity.class)
    @Sql("select * from hbird_accountbook_budget where account_book_id = :budget.accountBookId and time = DATE_FORMAT(NOW(),'%Y-%m');")
    AccountBookBudgetRestEntity getCurrentBudget(@Param("budget") AccountBookBudgetRestEntity budget);

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
     *
     * @param budget
     * @return
     */
    @ResultType(AccountBookBudgetRestEntity.class)
    @Sql("select * from hbird_accountbook_budget where account_book_id = :budget.accountBookId ORDER BY time DESC LIMIT 1")
    AccountBookBudgetRestEntity getLatelyBudget(@Param("budget") AccountBookBudgetRestEntity budget);

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

    @ResultType(SavingEfficiencyRestDTO.class)
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS spend, SUM( CASE WHEN order_type = 2 THEN money ELSE 0 END ) AS income, DATE_FORMAT( charge_date, '%Y-%m' ) AS time, budget.budget_money, budget.fixed_large_expenditure, budget.fixed_life_expenditure FROM `hbird_water_order` AS wo, (SELECT time, budget_money, fixed_large_expenditure, fixed_life_expenditure FROM `hbird_accountbook_budget` WHERE account_book_id = :accountBookId AND time <= CONCAT( DATE_FORMAT( NOW( ), '%Y-' ), :month ) AND time >= :rangeMonth) AS budget WHERE DATE_FORMAT( charge_date, '%Y-%m' ) IN ( budget.time ) AND wo.account_book_id = :accountBookId  AND wo.delflag = 0 GROUP BY time;")
    List<SavingEfficiencyRestDTO> listStatisticsByMonths(@Param("rangeMonth") String rangeMonth, @Param("month") String month, @Param("accountBookId") Integer accountBookId);

    /**
     * 获取消费结构比
     * @param accountBookId
     * @param month
     * @return
     */
    @ResultType(Map.class)
    @Sql("SELECT SUM( CASE WHEN order_type = 1 THEN money ELSE 0 END ) AS monthSpend, SUM( CASE WHEN type_pid = :foodType THEN money ELSE 0 END ) AS foodSpend, DATE_FORMAT( charge_date, '%Y-%m' ) AS time FROM `hbird_water_order` AS wo WHERE DATE_FORMAT( charge_date, '%Y-%m' ) IN ( DATE_FORMAT(DATE_ADD(CONCAT(DATE_FORMAT( NOW( ), '%Y-' ), :month,'-01'),INTERVAL -1 MONTH),'%Y-%m'),DATE_FORMAT(DATE_ADD(CONCAT(DATE_FORMAT( NOW( ), '%Y-' ), :month,'-01'),INTERVAL -1 YEAR),'%Y-%m'),CONCAT(DATE_FORMAT( NOW( ), '%Y-' ), :month)) AND wo.account_book_id = :accountBookId AND wo.delflag = 0 GROUP BY time DESC;")
    List<Map<String,Object>> getConsumptionStructureRatio(@Param("accountBookId") Integer accountBookId, @Param("month") String month , @Param("foodType") String foodType);
}
