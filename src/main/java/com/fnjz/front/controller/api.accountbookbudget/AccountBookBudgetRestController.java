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
import com.fnjz.front.entity.api.accountbookbudget.SceneABBudgetRestDTO;
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
                //执行更新流程
                budget.setUpdateBy(userAccountBookRestEntityCache.getUserInfoId());
                budget.setId(budgetResult.getId());
                budget.setCreateBy(userAccountBookRestEntityCache.getUserInfoId());
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
     * 设置或修改 预算
     *
     * @param type
     * @param budget
     * @return
     */
    @RequestMapping(value = "/setbudgetv2/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setbudgetv2(@PathVariable("type") String type, @RequestBody AccountBookBudgetRestEntity budget, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        int status = accountBookBudgetRestService.getABTypeByABId(budget.getAccountBookId());
        AccountBookBudgetRestEntity budgetResult;
        if (status == 1) {
            if (StringUtils.isEmpty(budget.getTime())) {
                budget.setTime(DateUtils.getCurrentYearMonth());
            } else {
                if (!ParamValidateUtils.isValidYearMonthDate(budget.getTime())) {
                    return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
                }
            }
            //判断是否存在预算
            budgetResult = accountBookBudgetRestService.getLatelyBudget(budget.getTime(), budget.getAccountBookId());
        }else{
            budgetResult = accountBookBudgetRestService.getLatelyBudgetv2(budget.getAccountBookId());
        }
        //校验金额
        try {
            if (budgetResult != null) {
                //执行更新流程
                budget.setUpdateBy(Integer.valueOf(userInfoId));
                budget.setId(budgetResult.getId());
                budget.setCreateBy(budgetResult.getCreateBy());
                budget.setCreateDate(budgetResult.getUpdateDate());
                int i = accountBookBudgetRestService.saveOrUpdate(budget, true);
                if (i < 0) {
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }

                //更新流程执行成功之后发送消息推送
                new Thread() {
                    public void run() {
                        accountBookBudgetRestService.reviseBudgetNotification(Integer.parseInt(userInfoId),budget);
                    }
                }.start();

                return new ResultBean(ApiResultType.OK, null);
            } else {
                //执行新增流程 此种情况只适用于第一次设置预算
                budget.setCreateBy(Integer.valueOf(userInfoId));
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
     * 多账本--->首页获取预算接口
     *
     * @return
     */
    @RequestMapping(value = "/getbudgetv2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getbudgetv2(@PathVariable("type") String type, @RequestParam(required = false) String time, @RequestParam(required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        if (abId == null) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
        try {
            //判断预算是否存在
            try {
                time = ParamValidateUtils.formatYearMonthDate(time);
            } catch (RuntimeException e) {
                return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
            }
            //查询当前账本类型  1:普通日常账本 2:场景账本(即需设置预算起始时间)
            int status = accountBookBudgetRestService.getABTypeByABId(abId);
            JSONObject jsonObject = new JSONObject();
            if (status == 1) {
                AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getLatelyBudgetv2(time, abId);
                if (budgetResult != null) {
                    if (budgetResult.getBudgetMoney() != null) {
                        if (budgetResult.getBudgetMoney().intValue() == -1) {
                            budgetResult.setBudgetMoney(null);
                        }
                    }
                    if(budgetResult!=null){
                        jsonObject.put("accountBookId", budgetResult.getAccountBookId());
                        jsonObject.put("budgetMoney", budgetResult.getBudgetMoney());
                        jsonObject.put("time", budgetResult.getTime());
                    }
                }
                return new ResultBean(ApiResultType.OK, jsonObject);
            } else {
                //场景账本
                SceneABBudgetRestDTO budgetResult = accountBookBudgetRestService.getSceneABBudget(abId);
                if (budgetResult != null) {
                    if (budgetResult.getBudgetMoney() != null) {
                        if (budgetResult.getBudgetMoney().intValue() == -1) {
                            budgetResult.setBudgetMoney(null);
                        }
                    }
                }
                if(budgetResult!=null){
                    jsonObject.put("accountBookId", budgetResult.getAccountBookId());
                    jsonObject.put("budgetMoney", budgetResult.getBudgetMoney());
                    jsonObject.put("beginTime", budgetResult.getBeginTime());
                    jsonObject.put("endTime", budgetResult.getEndTime());
                }
                return new ResultBean(ApiResultType.OK, jsonObject);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 多账本--->获取固定大额支出接口
     *
     * @return
     */
    @RequestMapping(value = "/getFixedSpend/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFixedSpend(@PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            JSONObject jsonObject = new JSONObject();
            AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getFixedSpend(userInfoId);
            if (budgetResult != null) {
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
                jsonObject.put("fixedLifeExpenditure", budgetResult.getFixedLifeExpenditure());
                jsonObject.put("fixedLargeExpenditure", budgetResult.getFixedLargeExpenditure());
            }
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 设置固定大额支出
     *
     * @return
     */
    @RequestMapping(value = "/setFixedSpend/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setFixedSpend(@PathVariable("type") String type, @RequestBody AccountBookBudgetRestEntity budget, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            accountBookBudgetRestService.setFixedSpend(budget, userInfoId);
            return new ResultBean(ApiResultType.OK, null);
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
     * v2 多账本 存钱效率查询
     * 存钱效率 = (单月收入-当月总支出)/(当月总支出-当月固定支出)
     *
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/getsavingefficiencyv2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getsavingefficiencyv2(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        System.out.println("登录终端：" + type);
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, range);
            JSONObject list = accountBookBudgetRestService.getSavingEfficiencyv2(userInfoId,jsonObject.getString("month"), jsonObject.getString("range"));
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
     * v2 多账本
     * 消费结构比查询 Consumption structure ratio
     * 非食物支出占总支出的比值
     *
     * @param type
     * @param request
     * @param month
     * @return
     */
    @RequestMapping(value = "/getconsumptionstructureratiov2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getconsumptionstructureratiov2(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month) {
        System.out.println("登录终端：" + type);
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, null);
            List<ConsumptionStructureRatioDTO> list = accountBookBudgetRestService.getConsumptionStructureRatiov2(Integer.valueOf(userInfoId), jsonObject.getString("month"));
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
     * v2版
     * 获取预算完成率接口  Budget completion rate
     * @param type
     * @param request
     * @param month
     * @param range
     * @return
     */
    @RequestMapping(value = "/getbudgetcompletionratev2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getbudgetcompletionratev2(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range, @RequestParam(value = "abId", required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        JSONObject jsonObject = ParamValidateUtils.checkSavingEfficiency(month, range);
        try {
            //查询当前账本类型  1:普通日常账本 2:场景账本(即需设置预算起始时间)
            int status = accountBookBudgetRestService.getABTypeByABId(abId);
            if(status==1){
                List<BudgetCompletionRateDTO> list = accountBookBudgetRestService.getBudgetCompletionRatev2(userInfoId,abId, jsonObject.getString("month"), jsonObject.getString("range"));
                return new ResultBean(ApiResultType.OK, list);
            }else{
                //场景账本
                return null;
            }

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
    public ResultBean getCountByYearMonth(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "time", required = false) String time, @RequestParam(value = "time", required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        try {
            try {
                time = ParamValidateUtils.formatYearMonthDate(time);
            } catch (RuntimeException e) {
                return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
            }
            int daysCount = warterOrderRestServiceI.countChargeDays(time, abId);
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

    /**
     * v2  多账本  首页获取预算+记账天数
     *
     * @param type
     * @param request
     * @param time
     * @return
     */
    @RequestMapping(value = "/getindexdatav2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getindexdatav2(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(value = "time", required = false) String time, @RequestParam(required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        if (abId == null) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
        try {
            try {
                time = ParamValidateUtils.formatYearMonthDate(time);
            } catch (RuntimeException e) {
                return new ResultBean(ApiResultType.TIME_IS_ERROR, null);
            }
            //预算
            ResultBean resultBean = getbudgetv2(time, abId);
            JSONObject jsonObject = (JSONObject) resultBean.getResult();
            //todo   此sql重复调用了  待优化
            int status = accountBookBudgetRestService.getABTypeByABId(abId);
            if(status==2){
                return CommonUtils.returnIndex(jsonObject, new JSONObject());
            }else {
                //记账天数加载
                ResultBean countByYearMonth = getCountByYearMonth(request, time, abId);
                return CommonUtils.returnIndex(jsonObject, (JSONObject) countByYearMonth.getResult());
            }
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

    @RequestMapping(value = "/setbudgetv2", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setbudgetv2(@RequestBody AccountBookBudgetRestEntity budget, HttpServletRequest request) {
        return this.setbudgetv2(null, budget, request);
    }

    @RequestMapping(value = "/getbudget", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBudget(HttpServletRequest request, @RequestParam(required = false) String time) {
        return this.getBudget(null, request, time);
    }

    @RequestMapping(value = "/getbudgetv2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getbudgetv2(@RequestParam(required = false) String time, @RequestParam(required = false) Integer abId) {
        return this.getbudgetv2(null, time, abId);
    }

    @RequestMapping(value = "/getsavingefficiency", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSavingEfficiency(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getSavingEfficiency(null, request, month, range);
    }

    @RequestMapping(value = "/getsavingefficiencyv2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getsavingefficiencyv2(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getsavingefficiencyv2(null, request, month, range);
    }

    @RequestMapping(value = "/getconsumptionstructureratio", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getConsumptionStructureRatio(HttpServletRequest request, @RequestParam(value = "month", required = false) String month) {
        return this.getConsumptionStructureRatio(null, request, month);
    }

    @RequestMapping(value = "/getconsumptionstructureratiov2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getconsumptionstructureratiov2(HttpServletRequest request, @RequestParam(value = "month", required = false) String month) {
        return this.getconsumptionstructureratiov2(null, request, month);
    }

    @RequestMapping(value = "/getbudgetcompletionrate", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBudgetCompletionRate(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getBudgetCompletionRate(null, request, month, range);
    }

    @RequestMapping(value = "/getbudgetcompletionratev2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getbudgetcompletionratev2(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range, @RequestParam(value = "abId", required = false) Integer abId) {
        return this.getbudgetcompletionratev2(null, request, month, range,abId);
    }

    @RequestMapping(value = "/getstatisticanalysis", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getStatisticAnalysis(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getStatisticAnalysis(null, request, month, range);
    }

    @RequestMapping(value = "/getcountbyyearmonth", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getCountByYearMonth(HttpServletRequest request, @RequestParam(value = "time", required = false) String time, @RequestParam(value = "abId", required = false) Integer abId) {
        return this.getCountByYearMonth(null, request, time, abId);
    }

    @RequestMapping(value = "/getindexdata", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIndexData(HttpServletRequest request, @RequestParam(value = "time", required = false) String time) {
        return this.getIndexData(null, request, time);
    }

    @RequestMapping(value = "/getindexdatav2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getindexdatav2(HttpServletRequest request, @RequestParam(value = "time", required = false) String time, @RequestParam(value = "abId", required = false) Integer abId) {
        return this.getindexdatav2(null, request, time, abId);
    }

    @RequestMapping(value = "/getFixedSpend", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFixedSpend(HttpServletRequest request) {
        return this.getFixedSpend(null, request);
    }
}