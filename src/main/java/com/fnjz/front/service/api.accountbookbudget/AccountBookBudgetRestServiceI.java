package com.fnjz.front.service.api.accountbookbudget;

import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.SavingEfficiencyRestDTO;
import org.jeecgframework.core.common.service.CommonService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AccountBookBudgetRestServiceI extends CommonService {

    /**
     * 设置或更新预算
     *
     * @param budget
     * @param flag   true-->更新流程 false-->新增流程
     * @return
     */
    int saveOrUpdate(AccountBookBudgetRestEntity budget, boolean flag);

    /**
     * 获取当月预算
     *
     * @param budget
     * @return
     */
    AccountBookBudgetRestEntity getCurrentBudget(AccountBookBudgetRestEntity budget);

    /**
     * 获取库最新预算，不限制当月
     *
     * @param budget
     * @return
     */
    AccountBookBudgetRestEntity getLatelyBudget(AccountBookBudgetRestEntity budget);

    /**
     * 获取存钱效率
     *
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    List<SavingEfficiencyRestDTO> getSavingEfficiency(Integer accountBookId, String month, String range);

    /**
     * 获取消费结构比
     * @param accountBookId
     * @param month
     * @return
     */
    List<Map<String,BigDecimal>> getConsumptionStructureRatio(Integer accountBookId, String month);
}
