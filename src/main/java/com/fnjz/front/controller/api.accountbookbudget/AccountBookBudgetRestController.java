package com.fnjz.front.controller.api.accountbookbudget;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestDTO;
import com.fnjz.front.entity.api.accountbookbudget.AccountBookBudgetRestEntity;
import com.fnjz.front.entity.api.accountbookbudget.SavingEfficiencyRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.accountbookbudget.AccountBookBudgetRestServiceI;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 账本-预算相关
 * @date 2018-07-26 16:14:37
 */
@Controller
@RequestMapping("/api/v2")
public class AccountBookBudgetRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AccountBookBudgetRestController.class);

    @Autowired
    private AccountBookBudgetRestServiceI accountBookBudgetRestService;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 设置或修改 当月预算/固定支出
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
            if (budget.getBudgetMoney() != null || budget.getFixedLargeExpenditure() != null || budget.getFixedLifeExpenditure() != null) {
                String shareCode = (String) request.getAttribute("shareCode");
                String userInfoId = (String) request.getAttribute("userInfoId");
                UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
                budget.setAccountBookId(userAccountBookRestEntityCache.getAccountBookId());
                //判断预算是否存在
                AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getCurrentBudget(budget);
                //校验金额
                if (budgetResult != null) {
                    //修改预算情况
                    if (budget.getBudgetMoney() != null && (budgetResult.getFixedLifeExpenditure() != null || budgetResult.getFixedLargeExpenditure() != null)) {
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
                    }
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
            } else {
                return new ResultBean(ApiResultType.MY_PARAMS_ERROR, null);
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
    public ResultBean getBudget(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            AccountBookBudgetRestEntity budget = new AccountBookBudgetRestEntity();
            budget.setAccountBookId(userAccountBookRestEntityCache.getAccountBookId());
            //判断预算是否存在 lately
            AccountBookBudgetRestEntity budgetResult = accountBookBudgetRestService.getLatelyBudget(budget);
            AccountBookBudgetRestDTO dto = null;
            if (budgetResult != null) {
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
            List<SavingEfficiencyRestDTO> list = accountBookBudgetRestService.getSavingEfficiency(userAccountBookRestEntityCache.getAccountBookId(),jsonObject.getString("month"),jsonObject.getString("range"));
            return new ResultBean(ApiResultType.OK,list);
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
    public ResultBean getBudget(HttpServletRequest request) {
        return this.getBudget(null, request);
    }

    @RequestMapping(value = "/getsavingefficiency", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSavingEfficiency(HttpServletRequest request, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "range", required = false) String range) {
        return this.getSavingEfficiency(null, request,month,range);
    }
}