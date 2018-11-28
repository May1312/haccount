package com.fnjz.front.service.impl.api.accountbookbudget;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.controller.api.message.MessageContentFactory;
import com.fnjz.front.controller.api.message.MessageType;
import com.fnjz.front.dao.*;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.DTO.*;
import com.fnjz.front.entity.api.accountbookbudget.SceneABBudgetRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.enums.StatisticsEnum;
import com.fnjz.front.service.api.accountbookbudget.AccountBookBudgetRestServiceI;
import com.fnjz.front.service.api.message.MessageServiceI;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service("accountBookBudgetRestService")
@Transactional
public class AccountBookBudgetRestServiceImpl extends CommonServiceImpl implements AccountBookBudgetRestServiceI {

    @Autowired
    private AccountBookBudgetRestDao accountBookBudgetRestDao;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @Autowired
    private AccountBookRestDao accountBookRestDao;

    @Autowired
    private UserPrivateLabelRestDao userPrivateLabelRestDao;

    @Autowired
    private UserAccountBookRestDao userAccountBookRestDao;

    @Autowired
    private MessageServiceI messageService;

    @Autowired
    private UserInfoRestDao userInfoRestDao;

    @Autowired
    private UserAccountBookRestServiceI userAccountBookRestService;

    @Test
    public void run() {
        LocalDate localDate = LocalDate.of(2018, 10, 1);
        LocalDate localDate2 = LocalDate.of(2018, 11, 3);
        Period period = Period.between(localDate, localDate2);
        int days = period.getDays();
        int months = period.getMonths();
        int years = period.getYears();
        System.out.println(days + "--" + months + "--" + years + "--");
    }

    /**
     * 设置或更新预算
     *
     * @param budget
     * @param flag   true-->更新流程 false-->新增流程
     * @return
     */
    @Override
    public int saveOrUpdate(AccountBookBudgetRestEntity budget, boolean flag) {
        //场景账本计算时间间隔
        if (budget.getBeginTime() != null && budget.getEndTime() != null) {
            Integer setSceneType = setSceneType(budget.getBeginTime(), budget.getEndTime());
            budget.setSceneType(setSceneType);
        }
        //引入新手任务
        createTokenUtils.integralTask(budget.getCreateBy() + "", ShareCodeUtil.id2sharecode(budget.getCreateBy()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Setting_up_budget);
        if (flag) {
            return accountBookBudgetRestDao.update(budget);
        } else {
            return accountBookBudgetRestDao.insert(budget);
        }
    }

    /**
     * 功能描述: 修改预算之后给成员发送通知
     *
     * @param: userInfoId 当前修改用户id
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/16 17:41
     */
    @Override
    public void reviseBudgetNotification(Integer userInfoId, AccountBookBudgetRestEntity budget, BigDecimal preBudgetMoney) {
        int totalMember = accountBookRestDao.getTotalMember(budget.getAccountBookId() + "");
        if (totalMember > 1) {
            //修改账本名称
            String ABtypeName = accountBookRestDao.getTypeNameByABId(budget.getAccountBookId());
            //通知人数集合
            List<UserAccountBookRestEntity> accountBookId = userAccountBookRestService.findByProperty(UserAccountBookRestEntity.class, "accountBookId", budget.getAccountBookId());
            ArrayList<Integer> integers = new ArrayList<>();
            //List<Integer> strings = userAccountBookRestDao.listForUserInfoIdSByaABId(budget.getAccountBookId());
            for (UserAccountBookRestEntity userAccountBookRestEntity : accountBookId) {
                integers.add(userAccountBookRestEntity.getUserInfoId());
            }
            //创建者姓名
            String creatName = userInfoRestDao.getUserNameByUserId(userInfoId);
            //组合消息内容
            String messageContent = MessageContentFactory.getMessageContent(MessageType.reviseBudgetNotification, ABtypeName, creatName, preBudgetMoney.toString(), budget.getBudgetMoney().toString());
            //添加消息，发送通知
            messageService.addUserMessage(messageContent, userInfoId, integers);
        }
    }

    /**
     * 获取当月预算
     *
     * @param time
     * @param accountBookId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getCurrentBudget(String time, Integer accountBookId) {
        return accountBookBudgetRestDao.getCurrentBudget(time, accountBookId);
    }

    /**
     * 获取库最新预算，不限制当月
     *
     * @param time
     * @param accountBookId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getLatelyBudget(String time, Integer accountBookId) {
        String currentYearMonth = DateUtils.getCurrentYearMonth();
        AccountBookBudgetRestEntity budgetResult;
        if (StringUtils.equals(currentYearMonth, time)) {
            //判断 若查询当月 执行原有sql--->限制time 不能大于当月
            budgetResult = accountBookBudgetRestDao.getLatelyBudget(accountBookId, currentYearMonth);
            if (budgetResult != null) {
                if (StringUtils.equals(currentYearMonth, budgetResult.getTime())) {
                    //当月 不能直接返回---->判断用户行为  是否是手动设置的预算而不是沿用上月，这样会造成可支配金额为null
                    if (budgetResult.getBudgetMoney() == null || (budgetResult.getFixedLargeExpenditure() == null && budgetResult.getFixedLifeExpenditure() == null)) {
                        //单独查询上一月预算数据
                        String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                        AccountBookBudgetRestEntity currentBudget = accountBookBudgetRestDao.getBudgetByTime(rangeMonth, accountBookId, currentYearMonth);
                        if (currentBudget != null) {
                            if (currentBudget.getBudgetMoney() != null && budgetResult.getBudgetMoney() == null) {
                                budgetResult.setBudgetMoney(currentBudget.getBudgetMoney());
                            }
                            if (currentBudget.getFixedLargeExpenditure() != null && budgetResult.getFixedLargeExpenditure() == null) {
                                budgetResult.setFixedLargeExpenditure(currentBudget.getFixedLargeExpenditure());
                            }
                            if (currentBudget.getFixedLifeExpenditure() != null && budgetResult.getFixedLifeExpenditure() == null) {
                                budgetResult.setFixedLifeExpenditure(currentBudget.getFixedLifeExpenditure());
                            }
                            this.saveOrUpdate(budgetResult, true);
                        }
                    }
                    return budgetResult;
                } else {
                    //todo  写复杂了，预算值与可支配金额(固定支出)不应该在一张表中
                    //判断月份差额 只允许间隔一个月 并且创建时间也要间隔一个月
                    String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                    if (StringUtils.equals(budgetResult.getTime(), rangeMonth) && StringUtils.equals(DateUtils.getYearMonthByDate(budgetResult.getCreateDate()), rangeMonth)) {
                        //执行新增前，增加判断是否 为-1
                        if ((budgetResult.getBudgetMoney() != null && budgetResult.getBudgetMoney().intValue() != -1) || (budgetResult.getFixedLifeExpenditure() != null && budgetResult.getFixedLifeExpenditure().intValue() != -1) || (budgetResult.getFixedLargeExpenditure() != null && budgetResult.getFixedLargeExpenditure().intValue() != -1)) {
                            //校验通过
                            AccountBookBudgetRestEntity budget = new AccountBookBudgetRestEntity();
                            if (budgetResult.getBudgetMoney() != null) {
                                if (budgetResult.getBudgetMoney().intValue() == -1) {
                                    budget.setBudgetMoney(null);
                                } else {
                                    budget.setBudgetMoney(budgetResult.getBudgetMoney());
                                }
                            }
                            if (budgetResult.getFixedLifeExpenditure() != null) {
                                if (budgetResult.getFixedLifeExpenditure().intValue() == -1) {
                                    budget.setFixedLifeExpenditure(null);
                                } else {
                                    budget.setFixedLifeExpenditure(budgetResult.getFixedLifeExpenditure());
                                }
                            }
                            if (budgetResult.getFixedLargeExpenditure() != null) {
                                if (budgetResult.getFixedLargeExpenditure().intValue() == -1) {
                                    budget.setFixedLargeExpenditure(null);
                                } else {
                                    budget.setFixedLargeExpenditure(budgetResult.getFixedLargeExpenditure());
                                }
                            }
                            if (budgetResult.getCreateBy() != null) {
                                budget.setCreateBy(budgetResult.getCreateBy());
                            }
                            if (budgetResult.getAccountBookId() != null) {
                                budget.setAccountBookId(budgetResult.getAccountBookId());
                            }
                            budget.setTime(currentYearMonth);
                            int insert = accountBookBudgetRestDao.insert(budget);
                            if (insert > 0) {
                                //设置时间
                                return budget;
                            }
                        }
                    }
                }
            } else {
                return budgetResult;
            }
        } else {
            //根据指定月份查询预算
            return accountBookBudgetRestDao.getCurrentBudget(time, accountBookId);
        }
        return null;
    }

    /**
     * 获取存钱效率
     *
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
        if (rangeSavingEfficiencyStatistics == null || rangeSavingEfficiencyStatistics.size() == 0) {
            return rangeSavingEfficiencyStatistics;
        }
        //在区级内查找
        List<SavingEfficiencyDTO> savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonths(rangeMonth, month, accountBookId);
        //有可支配金额  但是没有月记账
        if (savingEfficiencyDTOS == null || savingEfficiencyDTOS.size() == 0) {
            return rangeSavingEfficiencyStatistics;
        }
        //例 7月有可支配金额无月记账记录   6月有可以支配金额有月记账记录  此时会丢掉7月的记录  需处理
        if (rangeSavingEfficiencyStatistics.size() != savingEfficiencyDTOS.size()) {
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
     *
     * @param accountBookId
     * @param month
     * @return
     */
    @Override
    public List<ConsumptionStructureRatioDTO> getConsumptionStructureRatio(Integer accountBookId, String month) {
        List<ConsumptionStructureRatioDTO> list = accountBookBudgetRestDao.getConsumptionStructureRatio(accountBookId, month, RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE);
        if (list.size() < 3) {
            //获取传入月份
            String yearMonth = DateUtils.getCurrentYear() + "-" + month;
            //获取前一月
            String frontMonth = DateUtils.getRangeMonth(month, -1);
            //获取前一年月份
            String oldYearMonth = DateUtils.getRangeMonthforYear(month, -1);
            Map containMap = new HashMap<String, Object>();
            containMap.put(yearMonth, null);
            containMap.put(frontMonth, null);
            containMap.put(oldYearMonth, null);
            for (int i = 0; i < list.size(); i++) {
                if (containMap.containsKey(list.get(i).getTime())) {
                    containMap.remove(list.get(i).getTime());
                }
            }
            //取出map中剩余的值
            Set set = containMap.keySet();
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {
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
     * v2 获取消费结构比
     *
     * @param userInfoId
     * @param month
     * @return
     */
    @Override
    public List<ConsumptionStructureRatioDTO> getConsumptionStructureRatiov2(Integer userInfoId, String month) {
        //传入三对时间段  当月  上月  去年同月
        //获取传入月份初末范围
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(month), 1);
        //将date设置为这个月的第一天
        date = date.minusDays(date.getDayOfMonth() - 1);
        //月末一天
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        //获取上月
        LocalDate lastMonth = date.minusMonths(1);
        //月的第一天
        lastMonth = lastMonth.minusDays(lastMonth.getDayOfMonth() - 1);
        //月末一天
        LocalDate lastMonthend = lastMonth.with(TemporalAdjusters.lastDayOfMonth());
        //获取去年同期
        LocalDate lastYear = date.minusYears(1);
        //月第一天
        lastYear = lastYear.minusDays(lastYear.getDayOfMonth() - 1);
        //月末一天
        LocalDate lastYearend = lastYear.with(TemporalAdjusters.lastDayOfMonth());
        List<ConsumptionStructureRatioDTO> list = accountBookBudgetRestDao.getConsumptionStructureRatiov2(userInfoId, date.toString(), end.toString(), lastMonth.toString(), lastMonthend.toString(), lastYear.toString(), lastYearend.toString(), RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE);
        if (list.size() < 3) {
            Map containMap = new HashMap<String, Object>();
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM");
            containMap.put(date.format(formatters), null);
            containMap.put(lastMonth.format(formatters), null);
            containMap.put(lastYear.format(formatters), null);
            for (int i = 0; i < list.size(); i++) {
                if (containMap.containsKey(list.get(i).getTime())) {
                    containMap.remove(list.get(i).getTime());
                }
            }
            //取出map中剩余的值
            Set set = containMap.keySet();
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {
                ConsumptionStructureRatioDTO csr = new ConsumptionStructureRatioDTO();
                csr.setTime(iter.next());
                list.add(csr);
            }
        }
        //倒序
        Collections.sort(list, Comparator.comparing(ConsumptionStructureRatioDTO::getTime).reversed());
        return list;
    }

    /**
     * 预算完成率
     *
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    @Override
    public List<BudgetCompletionRateDTO> getBudgetCompletionRate(Integer accountBookId, String month, String range) {
        String rangeMonth = DateUtils.getRangeMonth(month, Integer.valueOf("-" + range));
        List<BudgetCompletionRateDTO> list = accountBookBudgetRestDao.listBudgetCompletionRateStatisticsByMonths(rangeMonth, month, accountBookId);
        return list;
    }

    /**
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
        StatisticAnalysisDTO statisticAnalysisDTO = new StatisticAnalysisDTO(savingEfficiency, consumptionStructureRatio, budgetCompletionRate);
        return statisticAnalysisDTO;
    }

    @Override
    public Map<String, Object> getStatisticAnalysisv2(String userInfoId, Integer abId, String month, String range) {
        //TODO 三条sql是不是可以优化
        //存钱效率
        JSONObject savingEfficiency = getSavingEfficiencyv2(userInfoId, month, range);
        //消费结构比
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), month);
        //预算完成率  日常账本
        JSONArray budgetCompletionRate = getBudgetCompletionRatev2(userInfoId, abId, month, range);
        Map<String, Object> map = new HashMap<>();
        map.put("listSavingEfficiency", savingEfficiency);
        map.put("listConsumptionStructureRatio", consumptionStructureRatio);
        map.put("listBudgetCompletionRate", budgetCompletionRate);
        return map;
    }

    @Override
    public Map<String, Object> getStatisticAnalysisv2ForScene(String userInfoId, Integer abId, String month, String range, SceneABBudgetRestDTO sceneABBudget) {
        //存钱效率
        JSONObject savingEfficiency = getSavingEfficiencyv2(userInfoId, month, range);
        //消费结构比
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), month);
        //预算完成率  场景账本
        Map<String, Object> budgetCompletionRatev2ForScene = getBudgetCompletionRatev2ForScene(userInfoId, abId, sceneABBudget);
        Map<String, Object> map = new HashMap<>();
        map.put("listSavingEfficiency", savingEfficiency);
        map.put("listConsumptionStructureRatio", consumptionStructureRatio);
        map.put("listBudgetCompletionRate", budgetCompletionRatev2ForScene);
        return map;
    }

    @Override
    public Map<String, Object> getStatisticAnalysisv2ForNoScene(String userInfoId, Integer abId, String month, String range, SceneABBudgetRestDTO sceneABBudget) {
        //存钱效率
        JSONObject savingEfficiency = getSavingEfficiencyv2(userInfoId, month, range);
        //消费结构比
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), month);
        Map<String, Object> map = new HashMap<>();
        map.put("listSavingEfficiency", savingEfficiency);
        map.put("listConsumptionStructureRatio", consumptionStructureRatio);
        map.put("listBudgetCompletionRate", new Map[0]);
        return map;
    }

    /**
     * 获取账本类型
     *
     * @param abId
     * @return
     */
    @Override
    public int getABTypeByABId(Integer abId) {
        return accountBookRestDao.getABTypeByABId(abId);
    }

    /**
     * 获取场景账本预算
     *
     * @param abId
     * @return
     */
    @Override
    public SceneABBudgetRestDTO getSceneABBudget(Integer abId) {
        return accountBookRestDao.getSceneABBudget(abId);
    }

    /**
     * 获取大额支出
     *
     * @param userInfoId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getFixedSpend(String userInfoId) {
        return accountBookBudgetRestDao.getFixedSpend(userInfoId);
    }

    /**
     * 获取预算   查询当月预算 前提是沿用上一月预算
     *
     * @param time
     * @param abId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getLatelyBudgetv2(String time, Integer abId) {
        String currentYearMonth = DateUtils.getCurrentYearMonth();
        AccountBookBudgetRestEntity budgetResult;
        if (StringUtils.equals(currentYearMonth, time)) {
            //判断 若查询当月 执行原有sql--->限制time 不能大于当月
            budgetResult = accountBookBudgetRestDao.getLatelyBudget(abId, currentYearMonth);
            if (budgetResult != null) {
                if (StringUtils.equals(currentYearMonth, budgetResult.getTime())) {
                    //查询到当月预算
                    return budgetResult;
                } else {
                    //判断月份差额 只允许间隔一个月 并且创建时间也要间隔一个月
                    String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                    if (StringUtils.equals(budgetResult.getTime(), rangeMonth) && StringUtils.equals(DateUtils.getYearMonthByDate(budgetResult.getCreateDate()), rangeMonth)) {
                        //执行新增前，增加判断是否 为-1
                        if ((budgetResult.getBudgetMoney() != null && budgetResult.getBudgetMoney().intValue() != -1) || (budgetResult.getFixedLifeExpenditure() != null && budgetResult.getFixedLifeExpenditure().intValue() != -1) || (budgetResult.getFixedLargeExpenditure() != null && budgetResult.getFixedLargeExpenditure().intValue() != -1)) {
                            //校验通过
                            AccountBookBudgetRestEntity budget = new AccountBookBudgetRestEntity();
                            if (budgetResult.getBudgetMoney() != null) {
                                if (budgetResult.getBudgetMoney().intValue() == -1) {
                                    budget.setBudgetMoney(null);
                                } else {
                                    budget.setBudgetMoney(budgetResult.getBudgetMoney());
                                }
                            }
                            if (budgetResult.getCreateBy() != null) {
                                budget.setCreateBy(budgetResult.getCreateBy());
                            }
                            if (budgetResult.getAccountBookId() != null) {
                                budget.setAccountBookId(budgetResult.getAccountBookId());
                            }
                            budget.setTime(currentYearMonth);
                            int insert = accountBookBudgetRestDao.insert(budget);
                            if (insert > 0) {
                                //设置时间
                                return budget;
                            }
                        }
                    }
                }
            } else {
                return budgetResult;
            }
        } else {
            //根据指定月份查询预算
            return accountBookBudgetRestDao.getCurrentBudget(time, abId);
        }
        return null;
    }

    /**
     * 设置固定大额支出
     *
     * @param budget
     * @param userInfoId
     */
    @Override
    public void setFixedSpend(AccountBookBudgetRestEntity budget, String userInfoId) {
        AccountBookBudgetRestEntity fixedSpend = accountBookBudgetRestDao.getFixedSpend(userInfoId);
        //引入新手任务
        createTokenUtils.integralTask(budget.getCreateBy() + "", ShareCodeUtil.id2sharecode(budget.getCreateBy()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Setting_up_savings_efficiency);
        if (fixedSpend != null) {
            //更新流程
            budget.setId(fixedSpend.getId());
            budget.setUpdateBy(Integer.valueOf(userInfoId));
            budget.setUpdateDate(new Date());
            budget.setCreateBy(Integer.valueOf(userInfoId));
            budget.setCreateDate(fixedSpend.getCreateDate());
            this.saveOrUpdate(budget);
        } else {
            budget.setCreateBy(Integer.valueOf(userInfoId));
            budget.setCreateDate(new Date());
            this.saveOrUpdate(budget);
        }
    }

    /**
     * 获取场景账本预算
     *
     * @param accountBookId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getLatelyBudgetv2(Integer accountBookId) {
        return accountBookBudgetRestDao.getLatelyBudgetv2(accountBookId);
    }

    /**
     * v2 获取存钱效率
     *
     * @param userInfoId
     * @param month
     * @param range
     * @return
     */
    @Override
    public JSONObject getSavingEfficiencyv2(String userInfoId, String month, String range) {
        //获取到固定大额支出
        AccountBookBudgetRestEntity fixedSpend = accountBookBudgetRestDao.getFixedSpend(userInfoId);
        //无可支配金额  结束
        if (fixedSpend == null) {
            return null;
        }
        if (!((fixedSpend.getFixedLargeExpenditure() != null && fixedSpend.getFixedLargeExpenditure().intValue() != -1) || (fixedSpend.getFixedLifeExpenditure() != null && fixedSpend.getFixedLifeExpenditure().intValue() != -1))) {
            return null;
        }
        LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(month), 1);
        //时间间隔-1
        LocalDate localDate1 = localDate.minusMonths(Integer.valueOf(range) - 1);
        //获取查询的时间范围
        LocalDate localDate8 = localDate.with(TemporalAdjusters.lastDayOfMonth());
        JSONObject jsonObject = new JSONObject();
        //在区级内查找
        List<SavingEfficiencyDTO> savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonthsv2(localDate1.toString(), localDate8.toString(), userInfoId);

        JSONArray jsonArray = new JSONArray();
        savingEfficiencyDTOS.forEach(v -> {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("monthIncome", v.getMonthIncome());
            jsonObject1.put("monthSpend", v.getMonthSpend());
            jsonObject1.put("time", v.getTime());
            jsonArray.add(jsonObject1);
        });
        //正常返回
        jsonObject.put("arrays", jsonArray);
        //封装固定大额支出
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("fixedLargeExpenditure", fixedSpend.getFixedLargeExpenditure());
        jsonObject1.put("fixedLifeExpenditure", fixedSpend.getFixedLifeExpenditure());
        jsonObject.put("arrays", jsonArray);
        jsonObject.put("fixedSpend", jsonObject1);
        return jsonObject;
    }

    /**
     * v2 日常账本预算完成率
     *
     * @param userInfoId
     * @param month
     * @param range
     * @return
     */
    @Override
    public JSONArray getBudgetCompletionRatev2(String userInfoId, Integer abId, String month, String range) {
        LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(month), 1);
        //取月末
        LocalDate end = localDate.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate begin = localDate.minusMonths(Integer.valueOf(range) - 1);
        //取月初
        begin = begin.minusDays(begin.getDayOfMonth() - 1);
        //获取范围内预算
        List<AccountBookBudgetRestEntity> list = accountBookBudgetRestDao.getBudgetByTimeRange(begin.toString(), end.toString(), abId);
        JSONArray jsonArray = new JSONArray();
        list.forEach(v -> {
            //遍历预算期内数据
            String[] split = StringUtils.split(v.getTime(), "-");
            //月初时间
            LocalDate localDate1 = LocalDate.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), 1);
            //获取月末时间
            LocalDate localDate2 = localDate1.with(TemporalAdjusters.lastDayOfMonth());
            String monthSpend = accountBookBudgetRestDao.listBudgetCompletionRateStatisticsByMonthsv2(localDate1.toString(), localDate2.toString(), abId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("monthSpend", monthSpend == null ? 0 : new BigDecimal(monthSpend));
            jsonObject.put("time", v.getTime());
            jsonObject.put("budgetMoney", v.getBudgetMoney());
            jsonArray.add(jsonObject);
        });
        return jsonArray;
    }

    /**
     * 设置预算统计类型
     *
     * @param begin1
     * @param end1
     * @return
     */
    private Integer setSceneType(Date begin1, Date end1) {
        Integer sceneType;
        LocalDate begin = LocalDateTime.ofInstant(begin1.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocalDate end = LocalDateTime.ofInstant(end1.toInstant(), ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(begin, end);
        int days = period.getDays();
        int months = period.getMonths();
        int years = period.getYears();
        if ((days + 1) <= 21 && months < 1 && years < 1 && months < 1) {
            //按天统计
            sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_DAY.getIndex());
        } else {
            // 按照3周21天   3周~5个月按照每周  5个月~24个月按照月  24个月以上按照年
            if (months <= 5 && years < 1) {
                //按周统计
                sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex());
            } else if ((5 < months && months <= 12 && years < 1) || (years == 1 && years < 2 && 0 <= months && months <= 12)) {
                //按月统计
                sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex());
            } else {
                //按年统计
                sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_CHART.getIndex());
            }
        }
        return sceneType;
    }

    /**
     * v2 场景账本预算完成率获取
     *
     * @param userInfoId
     * @param abId
     * @return
     */
    @Override
    public Map<String, Object> getBudgetCompletionRatev2ForScene(String userInfoId, Integer abId, SceneABBudgetRestDTO budget) {
        //按照3周21天 3周~5个月按照每周  5个月~24个月按照月 24个月以上按照年
        if (budget.getSceneType() == null) {
            Integer setSceneType = setSceneType(budget.getBeginTime(), budget.getEndTime());
            budget.setSceneType(setSceneType);
        }
        List<SceneBaseDTO> list;
        //根据统计类型 执行不同sql
        if (StringUtils.equals(budget.getSceneType() + "", StatisticsEnum.STATISTICS_FOR_DAY.getIndex())) {
            //日统计
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneDays(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());

        } else if (StringUtils.equals(budget.getSceneType() + "", StatisticsEnum.STATISTICS_FOR_WEEK.getIndex())) {
            //统计周
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneWeeks(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
        } else if (StringUtils.equals(budget.getSceneType() + "", StatisticsEnum.STATISTICS_FOR_MONTH.getIndex())) {
            //统计月
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneMonths(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
        } else {
            //统计年
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneYears(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
        }
        //数据累加
        list = sumSpend(list);
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("arrays", list);
        jsonObject.put("sceneBudget", budget);
        return jsonObject;
    }

    private List<SceneBaseDTO> sumSpend(List<SceneBaseDTO> list) {
        for (int i = 1; i < list.size(); i++) {
            //第二位元素追加前一位数据
            list.get(i).setMoney(list.get(i).getMoney().add(list.get(i - 1).getMoney()));
        }
        return list;
    }
}