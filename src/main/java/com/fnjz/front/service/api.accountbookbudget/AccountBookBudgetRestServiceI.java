package com.fnjz.front.service.api.accountbookbudget;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.DTO.BudgetCompletionRateDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.ConsumptionStructureRatioDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.SavingEfficiencyDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.StatisticAnalysisDTO;
import com.fnjz.front.entity.api.accountbookbudget.SceneABBudgetRestDTO;
import org.jeecgframework.core.common.service.CommonService;

import java.util.List;

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
     * @param time
     * @param accountBookId
     * @return
     */
    AccountBookBudgetRestEntity getCurrentBudget(String time,Integer accountBookId);

    /**
     * 获取库最新预算，不限制当月
     *
     * @param time
     * @param accountBookId
     * @return
     */
    AccountBookBudgetRestEntity getLatelyBudget(String time,Integer accountBookId);

    /**
     * 获取存钱效率
     *
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    List<SavingEfficiencyDTO> getSavingEfficiency(Integer accountBookId, String month, String range);

    /**
     * 获取消费结构比
     * @param accountBookId
     * @param month
     * @return
     */
    List<ConsumptionStructureRatioDTO> getConsumptionStructureRatio(Integer accountBookId, String month);

    /**
     * 获取预算完成率
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    List<BudgetCompletionRateDTO> getBudgetCompletionRate(Integer accountBookId, String month, String range);

    /**
     * 获取统计-分析数据
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    StatisticAnalysisDTO getStatisticAnalysis(Integer accountBookId, String month, String range);

    /**
     * 获取账本类型
     * @param abId
     * @return
     */
    int getABTypeByABId(Integer abId);

    /**
     * 获取场景账本预算
     * @param abId
     * @return
     */
    SceneABBudgetRestDTO getSceneABBudget(Integer abId);

    /**
     * 获取大额支出
     * @param userInfoId
     * @return
     */
    AccountBookBudgetRestEntity getFixedSpend(String userInfoId);

    /**
     * 获取预算
     * @param time
     * @param abId
     * @return
     */
    AccountBookBudgetRestEntity getLatelyBudgetv2(String time, Integer abId);

    /**
     * 设置固定大额支出
     * @param budget
     * @param userInfoId
     */
    void setFixedSpend(AccountBookBudgetRestEntity budget, String userInfoId);

    /**
     * 获取场景账本预算
     * @param accountBookId
     * @return
     */
    AccountBookBudgetRestEntity getLatelyBudgetv2(Integer accountBookId);

    /**
     * v2 获取存钱效率
     * @param userInfoId
     * @param month
     * @param range
     * @return
     */
    JSONObject getSavingEfficiencyv2(String userInfoId, String month, String range);

    /**
     * v2 多账本
     * 消费结构比查询 Consumption structure ratio
     * @param userInfoId
     * @param month
     * @return
     */
    List<ConsumptionStructureRatioDTO> getConsumptionStructureRatiov2(Integer userInfoId, String month);

    /**
     * v2 日常账本 预算完成率
     * @param userInfoId
     * @param month
     * @param range
     * @return
     */
    JSONArray getBudgetCompletionRatev2(String userInfoId, Integer abId, String month, String range);

    /**
     * 功能描述: 修改预算消息推送通知
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/16 20:08
     */
    void reviseBudgetNotification(Integer userInfoId,AccountBookBudgetRestEntity budget);
    /**
     * v2 获取场景账本
     * @param userInfoId
     * @param abId
     * @param month
     * @param range
     * @return
     */
    JSONArray getBudgetCompletionRatev2ForScene(String userInfoId, Integer abId, String month, String range);
}
