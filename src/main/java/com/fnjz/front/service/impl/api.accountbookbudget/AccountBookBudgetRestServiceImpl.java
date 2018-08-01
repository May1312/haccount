package com.fnjz.front.service.impl.api.accountbookbudget;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.AccountBookBudgetRestDao;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.SavingEfficiencyRestDTO;
import com.fnjz.front.service.api.accountbookbudget.AccountBookBudgetRestServiceI;
import com.fnjz.front.utils.DateUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("accountBookBudgetRestService")
@Transactional
public class AccountBookBudgetRestServiceImpl extends CommonServiceImpl implements AccountBookBudgetRestServiceI {

    @Autowired
    private AccountBookBudgetRestDao accountBookBudgetRestDao;

    /**
     * 设置或更新预算
     * @param budget
     * @param flag  true-->更新流程 false-->新增流程
     * @return
     */
    @Override
    public int saveOrUpdate(AccountBookBudgetRestEntity budget,boolean flag) {
        if(flag){
            //更新流程
            return accountBookBudgetRestDao.update(budget);
        }else{
            //新增流程
            return accountBookBudgetRestDao.insert(budget);
        }
    }

    /**
     * 获取当月预算
     * @param budget
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getCurrentBudget(AccountBookBudgetRestEntity budget) {
        return accountBookBudgetRestDao.getCurrentBudget(budget);
    }

    /**
     * 获取库最新预算，不限制当月
     * @param budget
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getLatelyBudget(AccountBookBudgetRestEntity budget) {
        AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestDao.getLatelyBudget(budget);
        //判断是否为当月，若不为当月 赋值当月预算
        if(budgetResult!=null){
            String currentYearMonth = DateUtils.getCurrentYearMonth();
            if(StringUtils.equalsIgnoreCase(currentYearMonth,budgetResult.getTime())){
                //当月 直接返回
                return budgetResult;
            }else{
                //执行新增
                if(budgetResult.getBudgetMoney()!=null){
                    budget.setBudgetMoney(budgetResult.getBudgetMoney());
                }
                if(budgetResult.getFixedLifeExpenditure()!=null){
                    budget.setFixedLifeExpenditure(budgetResult.getFixedLifeExpenditure());
                }
                if(budgetResult.getFixedLargeExpenditure()!=null){
                    budget.setFixedLargeExpenditure(budgetResult.getFixedLargeExpenditure());
                }
                int insert = accountBookBudgetRestDao.insert(budget);
                if(insert>0){
                    //设置时间
                    budget.setTime(currentYearMonth);
                    return budget;
               }else{
                    return null;
                }
            }
        }
        return budgetResult;
    }

    /**
     * 获取存钱效率
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    @Override
    public List<SavingEfficiencyRestDTO> getSavingEfficiency(Integer accountBookId, String month, String range) {
        String rangeMonth = DateUtils.getRangeMonth(month, Integer.valueOf("-" + range));
        //查询在此区间内的预算值
        List<SavingEfficiencyRestDTO> list = accountBookBudgetRestDao.listStatisticsByMonths(rangeMonth,month,accountBookId);
        return list;
    }

    /**
     * 获取消费结构比
     * @param accountBookId
     * @param month
     * @return
     */
    @Override
    public List<Map<String, BigDecimal>> getConsumptionStructureRatio(Integer accountBookId, String month) {
        List<Map<String, BigDecimal>> list = accountBookBudgetRestDao.getConsumptionStructureRatio(accountBookId,month,RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE);
        System.out.println(ArrayUtils.toString(list));
        return list;
    }
}