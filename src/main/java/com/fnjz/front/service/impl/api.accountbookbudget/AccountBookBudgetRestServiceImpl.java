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
     * ?????????????????????
     *
     * @param budget
     * @param flag   true-->???????????? false-->????????????
     * @return
     */
    @Override
    public int saveOrUpdate(AccountBookBudgetRestEntity budget, boolean flag) {
        //??????????????????????????????
        if (budget.getBeginTime() != null && budget.getEndTime() != null) {
            Integer setSceneType = setSceneType(budget.getBeginTime(), budget.getEndTime());
            budget.setSceneType(setSceneType);
        }
        //??????????????????
        createTokenUtils.integralTask(budget.getUpdateBy() + "", ShareCodeUtil.id2sharecode(budget.getCreateBy()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Setting_up_budget);
        if (flag) {
            return accountBookBudgetRestDao.update(budget);
        } else {
            return accountBookBudgetRestDao.insert(budget);
        }
    }

    /**
     * ????????????: ???????????????????????????????????????
     *
     * @param: userInfoId ??????????????????id
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/16 17:41
     */
    @Override
    public void reviseBudgetNotification(Integer userInfoId, AccountBookBudgetRestEntity budget, BigDecimal preBudgetMoney, String type) {
        int totalMember = accountBookRestDao.getTotalMember(budget.getAccountBookId() + "");
        if (totalMember > 1) {
            //??????????????????
            String ABtypeName = accountBookRestDao.getTypeNameByABId(budget.getAccountBookId());
            //??????????????????
            List<UserAccountBookRestEntity> accountBookId = userAccountBookRestService.findByProperty(UserAccountBookRestEntity.class, "accountBookId", budget.getAccountBookId());
            ArrayList<Integer> integers = new ArrayList<>();
            //List<Integer> strings = userAccountBookRestDao.listForUserInfoIdSByaABId(budget.getAccountBookId());
            for (UserAccountBookRestEntity userAccountBookRestEntity : accountBookId) {
                integers.add(userAccountBookRestEntity.getUserInfoId());
            }
            //???????????????
            String creatName = userInfoRestDao.getUserNameByUserId(userInfoId);
            //??????????????????
            String messageContent = MessageContentFactory.getMessageContent(MessageType.reviseBudgetNotification, ABtypeName, creatName, preBudgetMoney.toString(), budget.getBudgetMoney().toString());
            //???????????????????????????
            messageService.addUserMessage(messageContent, userInfoId, integers, type);
        }
    }

    /**
     * ??????????????????
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
     * ???????????????????????????????????????
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
            //?????? ??????????????? ????????????sql--->??????time ??????????????????
            budgetResult = accountBookBudgetRestDao.getLatelyBudget(accountBookId, currentYearMonth);
            if (budgetResult != null) {
                if (StringUtils.equals(currentYearMonth, budgetResult.getTime())) {
                    //?????? ??????????????????---->??????????????????  ???????????????????????????????????????????????????????????????????????????????????????null
                    if (budgetResult.getBudgetMoney() == null || (budgetResult.getFixedLargeExpenditure() == null && budgetResult.getFixedLifeExpenditure() == null)) {
                        //?????????????????????????????????
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
                    //todo  ??????????????????????????????????????????(????????????)????????????????????????
                    //?????????????????? ???????????????????????? ???????????????????????????????????????
                    String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                    if (StringUtils.equals(budgetResult.getTime(), rangeMonth) && StringUtils.equals(DateUtils.getYearMonthByDate(budgetResult.getCreateDate()), rangeMonth)) {
                        //???????????????????????????????????? ???-1
                        if ((budgetResult.getBudgetMoney() != null && budgetResult.getBudgetMoney().intValue() != -1) || (budgetResult.getFixedLifeExpenditure() != null && budgetResult.getFixedLifeExpenditure().intValue() != -1) || (budgetResult.getFixedLargeExpenditure() != null && budgetResult.getFixedLargeExpenditure().intValue() != -1)) {
                            //????????????
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
                                //????????????
                                return budget;
                            }
                        }
                    }
                }
            } else {
                return budgetResult;
            }
        } else {
            //??????????????????????????????
            return accountBookBudgetRestDao.getCurrentBudget(time, accountBookId);
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param accountBookId
     * @param month
     * @param range
     * @return
     */
    @Override
    public List<SavingEfficiencyDTO> getSavingEfficiency(Integer accountBookId, String month, String range) {
        String rangeMonth = DateUtils.getRangeMonth(month, Integer.valueOf("-" + range));
        //???????????????????????????????????????
        List<SavingEfficiencyDTO> rangeSavingEfficiencyStatistics = accountBookBudgetRestDao.getRangeSavingEfficiencyStatistics(rangeMonth, month, accountBookId);
        //??????????????????  ??????
        if (rangeSavingEfficiencyStatistics == null || rangeSavingEfficiencyStatistics.size() == 0) {
            return rangeSavingEfficiencyStatistics;
        }
        //??????????????????
        List<SavingEfficiencyDTO> savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonths(rangeMonth, month, accountBookId);
        //??????????????????  ?????????????????????
        if (savingEfficiencyDTOS == null || savingEfficiencyDTOS.size() == 0) {
            return rangeSavingEfficiencyStatistics;
        }
        //??? 7???????????????????????????????????????   6??????????????????????????????????????????  ???????????????7????????????  ?????????
        if (rangeSavingEfficiencyStatistics.size() != savingEfficiencyDTOS.size()) {
            //????????????list--->???????????????(??????time??????)
            rangeSavingEfficiencyStatistics.removeAll(savingEfficiencyDTOS);
            savingEfficiencyDTOS.addAll(rangeSavingEfficiencyStatistics);
            return savingEfficiencyDTOS;
        }
        //????????????
        return savingEfficiencyDTOS;
    }

    /**
     * ?????????????????????
     *
     * @param accountBookId
     * @param month
     * @return
     */
    @Override
    public List<ConsumptionStructureRatioDTO> getConsumptionStructureRatio(Integer accountBookId, String month) {
        List<ConsumptionStructureRatioDTO> list = accountBookBudgetRestDao.getConsumptionStructureRatio(accountBookId, month, RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE);
        if (list.size() < 3) {
            //??????????????????
            String yearMonth = DateUtils.getCurrentYear() + "-" + month;
            //???????????????
            String frontMonth = DateUtils.getRangeMonth(month, -1);
            //?????????????????????
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
            //??????map???????????????
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
     * v2 ?????????????????????
     *
     * @param userInfoId
     * @param month
     * @return
     */
    @Override
    public List<ConsumptionStructureRatioDTO> getConsumptionStructureRatiov2(Integer userInfoId, String month, Integer abId) {
        //?????????????????????  ??????  ??????  ????????????
        //??????????????????????????????
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(month), 1);
        //???date??????????????????????????????
        date = date.minusDays(date.getDayOfMonth() - 1);
        //????????????
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        //????????????
        LocalDate lastMonth = date.minusMonths(1);
        //???????????????
        lastMonth = lastMonth.minusDays(lastMonth.getDayOfMonth() - 1);
        //????????????
        LocalDate lastMonthend = lastMonth.with(TemporalAdjusters.lastDayOfMonth());
        //??????????????????
        LocalDate lastYear = date.minusYears(1);
        //????????????
        lastYear = lastYear.minusDays(lastYear.getDayOfMonth() - 1);
        //????????????
        LocalDate lastYearend = lastYear.with(TemporalAdjusters.lastDayOfMonth());
        List<ConsumptionStructureRatioDTO> list;
        if (abId != null) {
            if (abId == -1) {
                list = accountBookBudgetRestDao.getConsumptionStructureRatiov2ForPersonal(userInfoId, date.toString(), end.toString(), lastMonth.toString(), lastMonthend.toString(), lastYear.toString(), lastYearend.toString(), RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE, abId);
            } else {
                list = accountBookBudgetRestDao.getConsumptionStructureRatiov2(userInfoId, date.toString(), end.toString(), lastMonth.toString(), lastMonthend.toString(), lastYear.toString(), lastYearend.toString(), RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE, abId);
            }
        } else {
            list = accountBookBudgetRestDao.getConsumptionStructureRatiov2(userInfoId, date.toString(), end.toString(), lastMonth.toString(), lastMonthend.toString(), lastYear.toString(), lastYearend.toString(), RedisPrefix.CONSUMPTION_STRUCTURE_RATIO_FOOD_TYPE, abId);
        }
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
            //??????map???????????????
            Set set = containMap.keySet();
            Iterator<String> iter = set.iterator();
            while (iter.hasNext()) {
                ConsumptionStructureRatioDTO csr = new ConsumptionStructureRatioDTO();
                csr.setTime(iter.next());
                list.add(csr);
            }
        }
        //??????
        Collections.sort(list, Comparator.comparing(ConsumptionStructureRatioDTO::getTime).reversed());
        return list;
    }

    /**
     * ???????????????
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
        //TODO ??????sql?????????????????????
        //????????????
        List<SavingEfficiencyDTO> savingEfficiency = getSavingEfficiency(accountBookId, month, range);
        //???????????????
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatio(accountBookId, month);
        //???????????????
        List<BudgetCompletionRateDTO> budgetCompletionRate = getBudgetCompletionRate(accountBookId, month, range);
        StatisticAnalysisDTO statisticAnalysisDTO = new StatisticAnalysisDTO(savingEfficiency, consumptionStructureRatio, budgetCompletionRate);
        return statisticAnalysisDTO;
    }

    @Override
    public Map<String, Object> getStatisticAnalysisv2(String userInfoId, Integer abId, String month, String range) {
        //TODO ??????sql?????????????????????
        //????????????
        JSONObject savingEfficiency = getSavingEfficiencyv2(userInfoId, month, range, abId);
        //???????????????
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), month, abId);
        //???????????????  ????????????
        JSONArray budgetCompletionRate = getBudgetCompletionRatev2(userInfoId, abId, month, range);
        Map<String, Object> map = new HashMap<>();
        map.put("listSavingEfficiency", savingEfficiency);
        map.put("listConsumptionStructureRatio", consumptionStructureRatio);
        map.put("listBudgetCompletionRate", budgetCompletionRate);
        return map;
    }

    @Override
    public Map<String, Object> getStatisticAnalysisv2ForScene(String userInfoId, Integer abId, String month, String range, SceneABBudgetRestDTO sceneABBudget) {
        //????????????
        JSONObject savingEfficiency = getSavingEfficiencyv2(userInfoId, month, range, abId);
        //???????????????
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), month, abId);
        //???????????????  ????????????
        Map<String, Object> budgetCompletionRatev2ForScene = getBudgetCompletionRatev2ForScene(userInfoId, abId, sceneABBudget);
        Map<String, Object> map = new HashMap<>();
        map.put("listSavingEfficiency", savingEfficiency);
        map.put("listConsumptionStructureRatio", consumptionStructureRatio);
        map.put("listBudgetCompletionRate", budgetCompletionRatev2ForScene);
        return map;
    }

    @Override
    public Map<String, Object> getStatisticAnalysisv2ForNoScene(String userInfoId, Integer abId, String month, String range, SceneABBudgetRestDTO sceneABBudget) {
        //????????????
        JSONObject savingEfficiency = getSavingEfficiencyv2(userInfoId, month, range, abId);
        //???????????????
        List<ConsumptionStructureRatioDTO> consumptionStructureRatio = getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), month, abId);
        Map<String, Object> map = new HashMap<>();
        map.put("listSavingEfficiency", savingEfficiency);
        map.put("listConsumptionStructureRatio", consumptionStructureRatio);
        map.put("listBudgetCompletionRate", new Map[0]);
        return map;
    }

    /**
     * ??????????????????
     *
     * @param abId
     * @return
     */
    @Override
    public int getABTypeByABId(Integer abId) {
        return accountBookRestDao.getABTypeByABId(abId);
    }

    /**
     * ????????????????????????
     *
     * @param abId
     * @return
     */
    @Override
    public SceneABBudgetRestDTO getSceneABBudget(Integer abId) {
        return accountBookRestDao.getSceneABBudget(abId);
    }

    /**
     * ??????????????????
     *
     * @param userInfoId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getFixedSpend(String userInfoId) {
        return accountBookBudgetRestDao.getFixedSpend(userInfoId);
    }

    /**
     * ????????????   ?????????????????? ??????????????????????????????
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
            //?????? ??????????????? ????????????sql--->??????time ??????????????????
            budgetResult = accountBookBudgetRestDao.getLatelyBudget(abId, currentYearMonth);
            if (budgetResult != null) {
                if (StringUtils.equals(currentYearMonth, budgetResult.getTime())) {
                    //?????????????????????
                    return budgetResult;
                } else {
                    //?????????????????? ???????????????????????? ???????????????????????????????????????
                    String rangeMonth = DateUtils.getRangeMonth(DateUtils.getCurrentMonth(), -1);
                    if (StringUtils.equals(budgetResult.getTime(), rangeMonth) && StringUtils.equals(DateUtils.getYearMonthByDate(budgetResult.getCreateDate()), rangeMonth)) {
                        //???????????????????????????????????? ???-1
                        if ((budgetResult.getBudgetMoney() != null && budgetResult.getBudgetMoney().intValue() != -1) || (budgetResult.getFixedLifeExpenditure() != null && budgetResult.getFixedLifeExpenditure().intValue() != -1) || (budgetResult.getFixedLargeExpenditure() != null && budgetResult.getFixedLargeExpenditure().intValue() != -1)) {
                            //????????????
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
                                //????????????
                                return budget;
                            }
                        }
                    }
                }
            } else {
                return budgetResult;
            }
        } else {
            //??????????????????????????????
            return accountBookBudgetRestDao.getCurrentBudget(time, abId);
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param budget
     * @param userInfoId
     */
    @Override
    public void setFixedSpend(AccountBookBudgetRestEntity budget, String userInfoId) {
        AccountBookBudgetRestEntity fixedSpend = accountBookBudgetRestDao.getFixedSpend(userInfoId);
        //??????????????????
        createTokenUtils.integralTask(budget.getCreateBy() + "", ShareCodeUtil.id2sharecode(budget.getCreateBy()), CategoryOfBehaviorEnum.NewbieTask, AcquisitionModeEnum.Setting_up_savings_efficiency);
        if (fixedSpend != null) {
            //????????????
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
     * ????????????????????????
     *
     * @param accountBookId
     * @return
     */
    @Override
    public AccountBookBudgetRestEntity getLatelyBudgetv2(Integer accountBookId) {
        return accountBookBudgetRestDao.getLatelyBudgetv2(accountBookId);
    }

    /**
     * v2 ??????????????????
     *
     * @param userInfoId
     * @param month
     * @param range
     * @return
     */
    @Override
    public JSONObject getSavingEfficiencyv2(String userInfoId, String month, String range, Integer abId) {
        //???????????????????????????
        AccountBookBudgetRestEntity fixedSpend = accountBookBudgetRestDao.getFixedSpend(userInfoId);
        //??????????????????  ??????
        if (fixedSpend == null) {
            return null;
        }
        if (!((fixedSpend.getFixedLargeExpenditure() != null && fixedSpend.getFixedLargeExpenditure().intValue() != -1) || (fixedSpend.getFixedLifeExpenditure() != null && fixedSpend.getFixedLifeExpenditure().intValue() != -1))) {
            return null;
        }
        LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(month), 1);
        //????????????-1
        LocalDate localDate1 = localDate.minusMonths(Integer.valueOf(range) - 1);
        //???????????????????????????
        LocalDate localDate8 = localDate.with(TemporalAdjusters.lastDayOfMonth());
        JSONObject jsonObject = new JSONObject();
        //??????????????????
        List<SavingEfficiencyDTO> savingEfficiencyDTOS;
        if (abId != null) {
            if (abId == -1) {
                savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonthsv2ForPersonal(localDate1.toString(), localDate8.toString(), userInfoId, abId);
            } else {
                savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonthsv2(localDate1.toString(), localDate8.toString(), userInfoId, abId);
            }
        } else {
            savingEfficiencyDTOS = accountBookBudgetRestDao.listSavingEfficiencyStatisticsByMonthsv2(localDate1.toString(), localDate8.toString(), userInfoId, abId);
        }
        JSONArray jsonArray = new JSONArray();
        savingEfficiencyDTOS.forEach(v -> {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("monthIncome", v.getMonthIncome());
            jsonObject1.put("monthSpend", v.getMonthSpend());
            jsonObject1.put("time", v.getTime());
            jsonArray.add(jsonObject1);
        });
        //????????????
        jsonObject.put("arrays", jsonArray);
        //????????????????????????
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("fixedLargeExpenditure", fixedSpend.getFixedLargeExpenditure());
        jsonObject1.put("fixedLifeExpenditure", fixedSpend.getFixedLifeExpenditure());
        jsonObject.put("arrays", jsonArray);
        jsonObject.put("fixedSpend", jsonObject1);
        return jsonObject;
    }

    /**
     * v2 ???????????????????????????
     *
     * @param userInfoId
     * @param month
     * @param range
     * @return
     */
    @Override
    public JSONArray getBudgetCompletionRatev2(String userInfoId, Integer abId, String month, String range) {
        LocalDate localDate = LocalDate.of(LocalDate.now().getYear(), Integer.valueOf(month), 1);
        //?????????
        LocalDate end = localDate.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate begin = localDate.minusMonths(Integer.valueOf(range) - 1);
        //?????????
        begin = begin.minusDays(begin.getDayOfMonth() - 1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM");
        //?????????????????????
        List<AccountBookBudgetRestEntity> list = accountBookBudgetRestDao.getBudgetByTimeRange(begin.format(formatters), end.format(formatters), abId);
        JSONArray jsonArray = new JSONArray();
        list.forEach(v -> {
            //????????????????????????
            String[] split = StringUtils.split(v.getTime(), "-");
            //????????????
            LocalDate localDate1 = LocalDate.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), 1);
            //??????????????????
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
     * ????????????????????????
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
            //????????????
            sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_DAY.getIndex());
        } else {
            // ??????3???21???   3???~5??????????????????  5??????~24???????????????  24?????????????????????
            if (months <= 5 && years < 1) {
                //????????????
                sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex());
            } else if ((5 < months && months <= 12 && years < 1) || (years == 1 && years < 2 && 0 <= months && months <= 12)) {
                //????????????
                sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex());
            } else {
                //????????????
                sceneType = Integer.valueOf(StatisticsEnum.STATISTICS_FOR_CHART.getIndex());
            }
        }
        return sceneType;
    }

    /**
     * v2 ?????????????????????????????????
     *
     * @param userInfoId
     * @param abId
     * @return
     */
    @Override
    public Map<String, Object> getBudgetCompletionRatev2ForScene(String userInfoId, Integer
            abId, SceneABBudgetRestDTO budget) {
        //??????3???21??? 3???~5??????????????????  5??????~24??????????????? 24?????????????????????
        if (budget.getSceneType() == null) {
            Integer setSceneType = setSceneType(budget.getBeginTime(), budget.getEndTime());
            budget.setSceneType(setSceneType);
        }
        List<SceneBaseDTO> list;
        //?????????????????? ????????????sql
        if (StringUtils.equals(budget.getSceneType() + "", StatisticsEnum.STATISTICS_FOR_DAY.getIndex())) {
            //?????????
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneDays(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());

        } else if (StringUtils.equals(budget.getSceneType() + "", StatisticsEnum.STATISTICS_FOR_WEEK.getIndex())) {
            //?????????
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneWeeks(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
        } else if (StringUtils.equals(budget.getSceneType() + "", StatisticsEnum.STATISTICS_FOR_MONTH.getIndex())) {
            //?????????
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneMonths(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
        } else {
            //?????????
            list = accountBookBudgetRestDao.getBudgetCompletionRatev2ForSceneYears(abId, LocalDateTime.ofInstant(budget.getBeginTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString(), LocalDateTime.ofInstant(budget.getEndTime().toInstant(), ZoneId.systemDefault()).toLocalDate().toString());
        }
        //????????????
        list = sumSpend(list);
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("arrays", list);
        jsonObject.put("sceneBudget", budget);
        return jsonObject;
    }

    private List<SceneBaseDTO> sumSpend(List<SceneBaseDTO> list) {
        for (int i = 1; i < list.size(); i++) {
            //????????????????????????????????????
            list.get(i).setMoney(list.get(i).getMoney().add(list.get(i - 1).getMoney()));
        }
        return list;
    }
}