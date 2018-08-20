package com.fnjz.front.controller.api.accountbookbudget;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestDTO;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.DTO.BudgetCompletionRateDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.ConsumptionStructureRatioDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.SavingEfficiencyDTO;
import com.fnjz.front.entity.api.accountbookbudget.DTO.StatisticAnalysisDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.accountbookbudget.AccountBookBudgetRestServiceI;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 账本-预算相关
 * @date 2018-07-26 16:14:37
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class AccountBookBudgetRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AccountBookBudgetRestController.class);

    @Autowired
    private AccountBookBudgetRestServiceI accountBookBudgetRestService;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WarterOrderRestServiceI warterOrderRestServiceI;

    /**
     * 设置或修改 预算/固定支出
     *
     * @param type
     * @param budget
     * @return
     */
    @RequestMapping(value = "/setbudget/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setBudget(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody AccountBookBudgetRestEntity budget, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            budget.setAccountBookId(userAccountBookRestEntityCache.getAccountBookId());
            if (StringUtils.isEmpty(budget.getTime())) {
                budget.setTime(DateUtils.getCurrentYearMonth());
            } else {
                if (!ParamValidateUtils.isValidYearMonthDate(budget.getTime())) {
                    return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
                }
                //格式化日期
                budget.setTime(DateUtils.checkYearMonth(budget.getTime()));
            }
            //判断预算是否存在
            AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getCurrentBudget(budget.getTime(), budget.getAccountBookId());
            //校验金额
            if (budgetResult != null) {
                //修改预算情况
                    /*if (budget.getBudgetMoney() != null && (budgetResult.getFixedLifeExpenditure() != null || budgetResult.getFixedLargeExpenditure() != null)) {
                        BigDecimal fixedLifeExpenditure = budgetResult.getFixedLifeExpenditure();
                        BigDecimal fixedLargeExpenditure = budgetResult.getFixedLargeExpenditure();
                        if (fixedLifeExpenditure == null) {
                            fixedLifeExpenditure = new BigDecimal(0);
                        }
                        if (fixedLargeExpenditure == null) {
                            fixedLargeExpenditure = new BigDecimal(0);
                        }
                        BigDecimal sum = fixedLifeExpenditure.add(fixedLargeExpenditure);
                        if (budget.getBudgetMoney().compareTo(sum) == -1) {
                            return new ResultBean(ApiResultType.BUDGET_MONEY_IS_SMALL, null);
                        }
                    }//修改固定支出情况
                    else if (budgetResult.getBudgetMoney() != null && (budget.getFixedLargeExpenditure() != null || budget.getFixedLifeExpenditure() != null)) {
                        if (budget.getFixedLargeExpenditure() != null && budget.getFixedLifeExpenditure() != null) {
                            BigDecimal fixedLifeExpenditure = budget.getFixedLifeExpenditure();
                            BigDecimal fixedLargeExpenditure = budget.getFixedLargeExpenditure();
                            BigDecimal sum = fixedLifeExpenditure.add(fixedLargeExpenditure);
                            if (sum.compareTo(budgetResult.getBudgetMoney()) == 1) {
                                return new ResultBean(ApiResultType.FIXED_EXPENDITURE_IS_LARGE, null);
                            }
                        } else {
                            if (budget.getFixedLargeExpenditure() != null) {
                                if (budget.getFixedLargeExpenditure().compareTo(budgetResult.getBudgetMoney()) == 1) {
                                    return new ResultBean(ApiResultType.FIXED_EXPENDITURE_IS_LARGE, null);
                                }
                            }
                            if (budget.getFixedLifeExpenditure() != null) {
                                if (budget.getFixedLifeExpenditure().compareTo(budgetResult.getBudgetMoney()) == 1) {
                                    return new ResultBean(ApiResultType.FIXED_EXPENDITURE_IS_LARGE, null);
                                }
                            }
                        }
                    }*/
                //执行更新流程
                budget.setUpdateBy(userAccountBookRestEntityCache.getUserInfoId());
                budget.setId(budgetResult.getId());
                int i = accountBookBudgetRestService.saveOrUpdate(budget, true);
                if (i < 0) {
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
                return new ResultBean(ApiResultType.OK, null);
            } else {
                //执行新增流程 此种情况只适用于第一次设置预算
                budget.setCreateBy(userAccountBookRestEntityCache.getUserInfoId());
                int i = accountBookBudgetRestService.saveOrUpdate(budget, false);
                if (i < 0) {
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
                return new ResultBean(ApiResultType.OK, null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 首页获取预算接口
     *
     * @return
     */
    @RequestMapping(value = "/getbudget/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBudget(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(required = false) String time) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            //判断预算是否存在
            try {
                time = ParamValidateUtils.formatYearMonthDate(time);
            } catch (RuntimeException e) {
                return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
            }
            AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getLatelyBudget(time, userAccountBookRestEntityCache.getAccountBookId());
            AccountBookBudgetRestDTO dto = null;
            if (budgetResult != null) {
                if (budgetResult.getBudgetMoney() != null) {
                    if (budgetResult.getBudgetMoney().intValue() == -1) {
                        budgetResult.setBudgetMoney(null);
                    }
                }
                if (budgetResult.getFixedLargeExpenditure() != null) {
                    if (budgetResult.getFixedLargeExpenditure().intValue() == -1) {
                        budgetResult.setFixedLargeExpenditure(null);
                    }
                }
                if (budgetResult.getFixedLifeExpenditure() != null) {
                    if (budgetResult.getFixedLifeExpenditure().intValue() == -1) {
                        budgetResult.setFixedLifeExpenditure(null);
                    }
                }
                dto = new AccountBookBudgetRestDTO();
                BeanUtils.copyProperties(budgetResult, dto);
            }
            return new ResultBean(ApiResultType.OK, dto);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 存钱效率查询
     * 存钱效率 = (单月收入-当月总支出)/(当月总支出-当月固定支出)
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/getsavingefficiency/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSavingEfficiency(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, range);
            List<SavingEfficiencyDTO> list = accountBookBudgetRestService.getSavingEfficiency(userAccountBookRestEntityCache.getAccountBookId(), jsonObject.getString("month"), jsonObject.getString("range"));
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 消费结构比查询 Consumption structure ratio
     * 非食物支出占总支出的比值
     *
     * @param type
     * @param request
     * @param month
     * @return
     */
    @RequestMapping(value = "/getconsumptionstructureratio/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getConsumptionStructureRatio(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, null);
            List<ConsumptionStructureRatioDTO> list = accountBookBudgetRestService.getConsumptionStructureRatio(userAccountBookRestEntityCache.getAccountBookId(), jsonObject.getString("month"));
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取预算完成率接口  Budget completion rate
     *
     * @param type
     * @param request
     * @param month
     * @param range
     * @return
     */
    @RequestMapping(value = "/getbudgetcompletionrate/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBudgetCompletionRate(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, range);
            List<BudgetCompletionRateDTO> list = accountBookBudgetRestService.getBudgetCompletionRate(userAccountBookRestEntityCache.getAccountBookId(), jsonObject.getString("month"), jsonObject.getString("range"));
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取统计-分析接口 包含以上三个接口数据
     *
     * @param type
     * @param request
     * @param month
     * @param range
     * @return
     */
    @RequestMapping(value = "/getstatisticanalysis /{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getStatisticAnalysis(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, range);
            StatisticAnalysisDTO all = accountBookBudgetRestService.getStatisticAnalysis(userAccountBookRestEntityCache.getAccountBookId(), jsonObject.getString("month"), jsonObject.getString("range"));
            return new ResultBean(ApiResultType.OK, all);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取统计-本月记账天数获取
     *
     * @param type
     * @param request
     * @param time
     * @return
     */
    @RequestMapping(value = "/getcountbyyearmonth/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getCountByYearMonth(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "time", required = false) String time) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            try {
                time = ParamValidateUtils.formatYearMonthDate(time);
            } catch (RuntimeException e) {
                return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
            }
            int daysCount = warterOrderRestServiceI.countChargeDays(time, userAccountBookRestEntityCache.getAccountBookId());
            int monthDaysByYearMonth = DateUtils.getMonthDaysByYearMonth(time);
            JSONObject jo = new JSONObject();
            jo.put("chargeDays", daysCount);
            jo.put("monthDays", monthDaysByYearMonth);
            jo.put("time", time);
            return new ResultBean(ApiResultType.OK, jo);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 首页数据加载 统一接口 预算+记账天数
     *
     * @param type
     * @param request
     * @param time
     * @return
     */
    @RequestMapping(value = "/getindexdata/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIndexData(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "time", required = false) String time) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            try {
                time = ParamValidateUtils.formatYearMonthDate(time);
            } catch (RuntimeException e) {
                return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
            }
            //首页预算数据加载
            AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getLatelyBudget(time, userAccountBookRestEntityCache.getAccountBookId());
            AccountBookBudgetRestDTO dto = null;
            if (budgetResult != null) {
                if (budgetResult.getBudgetMoney() != null) {
                    if (budgetResult.getBudgetMoney().intValue() == -1) {
                        budgetResult.setBudgetMoney(null);
                    }
                }
                if (budgetResult.getFixedLargeExpenditure() != null) {
                    if (budgetResult.getFixedLargeExpenditure().intValue() == -1) {
                        budgetResult.setFixedLargeExpenditure(null);
                    }
                }
                if (budgetResult.getFixedLifeExpenditure() != null) {
                    if (budgetResult.getFixedLifeExpenditure().intValue() == -1) {
                        budgetResult.setFixedLifeExpenditure(null);
                    }
                }
                dto = new AccountBookBudgetRestDTO();
                BeanUtils.copyProperties(budgetResult, dto);
            }
            //记账天数加载
            int daysCount = warterOrderRestServiceI.countChargeDaysByChargeDays(time, userAccountBookRestEntityCache.getAccountBookId());
            int monthDaysByYearMonth = DateUtils.getMonthDaysByYearMonth(time);
            JSONObject jo = new JSONObject();
            jo.put("chargeDays", daysCount);
            jo.put("monthDays", monthDaysByYearMonth);
            jo.put("time", time);
            return CommonUtils.returnIndex(dto, jo);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = "/setbudget", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setBudget(@RequestBody AccountBookBudgetRestEntity budget, HttpServletRequest request) {
        return this.setBudget(null, budget, request);
    }

    @RequestMapping(value = "/getbudget", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBudget(HttpServletRequest request, @RequestParam(required = false) String time) {
        return this.getBudget(null, request, time);
    }

    @RequestMapping(value = "/getsavingefficiency", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSavingEfficiency(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getSavingEfficiency(null, request, month, range);
    }

    @RequestMapping(value = "/getconsumptionstructureratio", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getConsumptionStructureRatio(HttpServletRequest request, @RequestParam(value = "month", required = false) String month) {
        return this.getConsumptionStructureRatio(null, request, month);
    }

    @RequestMapping(value = "/getbudgetcompletionrate", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBudgetCompletionRate(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getBudgetCompletionRate(null, request, month, range);
    }

    @RequestMapping(value = "/getstatisticanalysis", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getStatisticAnalysis(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getStatisticAnalysis(null, request, month, range);
    }

    @RequestMapping(value = "/getcountbyyearmonth", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getCountByYearMonth(HttpServletRequest request, @RequestParam(value = "time", required = false) String time) {
        return this.getCountByYearMonth(null, request, time);
    }

    @RequestMapping(value = "/getindexdata", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIndexData(HttpServletRequest request, @RequestParam(value = "time", required = false) String time) {
        return this.getIndexData(null, request, time);
    }
}