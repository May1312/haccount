package com.fnjz.front.entity.api.accountbookbudget.DTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计-分析结果封装对象
 * Created by yhang on 2018/8/3.
 */
public class StatisticAnalysisDTO {

    public StatisticAnalysisDTO(List<SavingEfficiencyDTO> listSavingEfficiency, List<ConsumptionStructureRatioDTO> listConsumptionStructureRatio, List<BudgetCompletionRateDTO> listBudgetCompletionRate) {
        this.listSavingEfficiency = listSavingEfficiency;
        this.listConsumptionStructureRatio = listConsumptionStructureRatio;
        this.listBudgetCompletionRate = listBudgetCompletionRate;
    }

    /**
     *存钱效率
     */
    private List<SavingEfficiencyDTO> listSavingEfficiency = new ArrayList<>();
    /**
     * 消费结构比
     */
    private List<ConsumptionStructureRatioDTO> listConsumptionStructureRatio = new ArrayList<>();
    /**
     * 预算完成率
     */
    private List<BudgetCompletionRateDTO> listBudgetCompletionRate = new ArrayList<>();

    public List<SavingEfficiencyDTO> getListSavingEfficiency() {
        return listSavingEfficiency;
    }

    public void setListSavingEfficiency(List<SavingEfficiencyDTO> listSavingEfficiency) {
        this.listSavingEfficiency = listSavingEfficiency;
    }

    public List<ConsumptionStructureRatioDTO> getListConsumptionStructureRatio() {
        return listConsumptionStructureRatio;
    }

    public void setListConsumptionStructureRatio(List<ConsumptionStructureRatioDTO> listConsumptionStructureRatio) {
        this.listConsumptionStructureRatio = listConsumptionStructureRatio;
    }

    public List<BudgetCompletionRateDTO> getListBudgetCompletionRate() {
        return listBudgetCompletionRate;
    }

    public void setListBudgetCompletionRate(List<BudgetCompletionRateDTO> listBudgetCompletionRate) {
        this.listBudgetCompletionRate = listBudgetCompletionRate;
    }
}
