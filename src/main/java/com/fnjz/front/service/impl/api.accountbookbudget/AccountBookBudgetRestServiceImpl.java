package com.fnjz.front.service.impl.api.accountbookbudget;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.AccountBookBudgetRestDao;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.DTO.BudgetCompletionRateDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.ConsumptionStructureRatioDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.SavingEfficiencyDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.StatisticAnalysisDTO;
import com.fnjz.front.service.api.accountbookbudget.AccountBookBudgetRestServiceI;
import com.fnjz.front.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
     * @param time
     * @param accountBookId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getCurrentBudget(String time, Integer accountBookId) {
        return accountBookBudgetRestDao.getCurrentBudget(time,accountBookId);
    }

    /**
     * 获取库最新预算，不限制当月
     * @param time
     * @param accountBookId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getLatelyBudget(String time,Integer accountBookId) {
        String currentYearMonth = DateUtils.getCurrentYearMonth();
        AccountBookBudgetRestEntity budgetResult;
        if(StringUtils.equals(currentYearMonth,time)){
            //判断 若查询当月 执行原有sql--->限制time 不能大于当月
            budgetResult = accountBookBudgetRestDao.getLatelyBudget(accountBookId,currentYearMonth);
            if(budgetResult!=null){
                if(StringUtils.equals(currentYearMonth,budgetResult.getTime())){
                    //当月 不能直接返回---->判断用户行为  是否是手动设置的预算而不是沿用上月，这样会造成可支配金额为null
                    if(budgetResult.getBudgetMoney()==null|| (budgetResult.getFixedLargeExpenditure()==null && budgetResult.getFixedLifeExpenditure()==null)){
                        //单独查询上一月预算数据
                        String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                        AccountBookBudgetRestEntity currentBudget = accountBookBudgetRestDao.getBudgetByTime(rangeMonth, accountBookId,currentYearMonth);
                        if(currentBudget!=null){
                            if(currentBudget.getBudgetMoney()!=null&&budgetResult.getBudgetMoney()==null){
                                budgetResult.setBudgetMoney(currentBudget.getBudgetMoney());
                            }
                            if(currentBudget.getFixedLargeExpenditure()!=null&&budgetResult.getFixedLargeExpenditure()==null){
                                budgetResult.setFixedLargeExpenditure(currentBudget.getFixedLargeExpenditure());
                            }
                            if(currentBudget.getFixedLifeExpenditure()!=null&&budgetResult.getFixedLifeExpenditure()==null){
                                budgetResult.setFixedLifeExpenditure(currentBudget.getFixedLifeExpenditure());
                            }
                            this.saveOrUpdate(budgetResult,true);
                        }
                    }
                    return budgetResult;
                }else{
                    //todo  写复杂了，预算值与可支配金额(固定支出)不应该在一张表中
                    //判断月份差额 只允许间隔一个月 并且创建时间也要间隔一个月
                    String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                    if(StringUtils.equals(budgetResult.getTime(),rangeMonth) && StringUtils.equals(DateUtils.getYearMonthByDate(budgetResult.getCreateDate()),rangeMonth)){
                        //执行新增前，增加判断是否 为-1
                        if((budgetResult.getBudgetMoney()!=null && budgetResult.getBudgetMoney().intValue()!=-1) || (budgetResult.getFixedLifeExpenditure()!=null && budgetResult.getFixedLifeExpenditure().intValue()!=-1) || (budgetResult.getFixedLargeExpenditure()!=null && budgetResult.getFixedLargeExpenditure().intValue()!=-1)){
                            //校验通过
                            AccountBookBudgetRestEntity budget = new AccountBookBudgetRestEntity();
                            if(budgetResult.getBudgetMoney()!=null){
                                if(budgetResult.getBudgetMoney().intValue() == -1){
                                    budget.setBudgetMoney(null);
                                }else{
                                    budget.setBudgetMoney(budgetResult.getBudgetMoney());
                                }
                            }
                            if(budgetResult.getFixedLifeExpenditure()!=null){
                                if(budgetResult.getFixedLifeExpenditure().intValue() == -1){
                                    budget.setFixedLifeExpenditure(null);
                                }else{
                                    budget.setFixedLifeExpenditure(budgetResult.getFixedLifeExpenditure());
                                }
                            }
                            if(budgetResult.getFixedLargeExpenditure()!=null){
                                if(budgetResult.getFixedLargeExpenditure().intValue() == -1){
                                    budget.setFixedLargeExpenditure(null);
                                }else{
                                    budget.setFixedLargeExpenditure(budgetResult.getFixedLargeExpenditure());
                                }
                            }
                            if(budgetResult.getCreateBy()!=null){
                                budget.setCreateBy(budgetResult.getCreateBy());
                            }
                            if(budgetResult.getAccountBookId()!=null){
                                budget.setAccountBookId(budgetResult.getAccountBookId());
                            }
                            budget.setTime(currentYearMonth);
                            int insert = accountBookBudgetRestDao.insert(budget);
                            if(insert>0){
                                //设置时间
                                return budget;
                            }
                        }
                    }
                }
            }else{
                return budgetResult;
            }
        }else{
            //根据指定月份查询预算
            return accountBookBudgetRestDao.getCurrentBudget(time,accountBookId);
        }
        return null;
    }

    /**
     * 获取存钱效率
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    @Override
    public List<SavingEfficiencyDTO> getSavingEfficiency(Integer accountBookId, String month, String range) {
        String rangeMonth = DateUtils.getRangeMonth(month, Integer.valueOf("-" + range));
        //查询在此区间内的可支配金额
        List<SavingEfficiencyDTO> rangeSavingEfficiencyStatistics = accountBookBudgetRestDao.getRangeSavingEfficiencyStatistics(rangeMonth, month, accountBookId);
        //无可支配金额  结束
        if(rangeSavingEfficiencyStatistics==null || rangeSavingEfficiencyStatistics.size()==0){
            return rangeSavingEfficiencyStatistics;
        }
        //在区级内查找
        List<SavingEfficiencyDTO> savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonths(rangeMonth, month, accountBookId);
        //有可支配金额  但是没有月记账
        if(savingEfficiencyDTOS==null || savingEfficiencyDTOS.size()==0){
            return rangeSavingEfficiencyStatistics;
        }
        //例 7月有可支配金额无月记账记录   6月有可以支配金额有月记账记录  此时会丢掉7月的记录  需处理
        if(rangeSavingEfficiencyStatistics.size()!=savingEfficiencyDTOS.size()){
            //获取两个list--->无重复并集(根据time属性)
            rangeSavingEfficiencyStatistics.removeAll(savingEfficiencyDTOS);
            savingEfficiencyDTOS.addAll(rangeSavingEfficiencyStatistics);
            return savingEfficiencyDTOS;
        }
        //正常返回
        return savingEfficiencyDTOS;
    }

    /**
     * 获取消费结构比
     * @param accountBookId
     * @param month
     * @return
     */
    @Override
    public List<ConsumptionStructureRatioDTO> getConsumptionStructureRatio(Integer accountBookId, String month) {
        List<ConsumptionStructureRatioDTO> list = accountBookBudgetRestDao.getConsumptionStructureRatio(accountBookId, month, RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE);
        if(list.size()<3){
            //获取传入月份
            String yearMonth = DateUtils.getCurrentYear()+"-"+month;
            //获取前一月
            String frontMonth = DateUtils.getRangeMonth(month,-1);
            //获取前一年月份
            String oldYearMonth = DateUtils.getRangeMonthforYear(month,-1);
            Map containMap = new HashMap<String, Object>();
            containMap.put(yearMonth,null);
            containMap.put(frontMonth,null);
            containMap.put(oldYearMonth,null);
            for(int i = 0 ; i < list.size() ; i ++){
                if(containMap.containsKey(list.get(i).getTime())){
                    containMap.remove(list.get(i).getTime());
                }
            }
            //取出map中剩余的值
            Set set = containMap.keySet();
            Iterator<String> iter = set.iterator() ;
            while(iter.hasNext()){
                ConsumptionStructureRatioDTO csr = new ConsumptionStructureRatioDTO();
                csr.setTime(iter.next());
                list.add(csr);
            }
        }
        Collections.sort(list, new Comparator<ConsumptionStructureRatioDTO>() {
            @Override
            public int compare(ConsumptionStructureRatioDTO o1, ConsumptionStructureRatioDTO o2) {
                int i = o1.getTime().compareTo(o2.getTime());
                if (i > 0) {
                    return -1;
                } else if (i < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

    /**
     * 预算完成率
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    @Override
    public List<BudgetCompletionRateDTO> getBudgetCompletionRate(Integer accountBookId, String month, String range) {
        String rangeMonth = DateUtils.getRangeMonth(month, Integer.valueOf("-" + range));
        List<BudgetCompletionRateDTO> list = accountBookBudgetRestDao.listBudgetCompletionRateStatisticsByMonths(rangeMonth,month,accountBookId);
        return list;
    }

    /**
     *
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    @Override
    public StatisticAnalysisDTO getStatisticAnalysis(Integer accountBookId, String month, String range) {
        //TODO 三条sql是不是可以优化
        //存钱效率
        List<SavingEfficiencyDTO> savingEfficiency = getSavingEfficiency(accountBookId, month, range);
        //消费结构比
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatio(accountBookId, month);
        //预算完成率
        List<BudgetCompletionRateDTO> budgetCompletionRate = getBudgetCompletionRate(accountBookId, month, range);
        StatisticAnalysisDTO statisticAnalysisDTO = new StatisticAnalysisDTO(savingEfficiency,consumptionStructureRatio,budgetCompletionRate);
        return statisticAnalysisDTO;
    }
}