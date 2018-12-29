package com.fnjz.front.controller.api.statistics;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.statistics.StatisticsIncomeTopDTO;
import com.fnjz.front.entity.api.statistics.StatisticsParamsRestDTO;
import com.fnjz.front.entity.api.statistics.StatisticsSpendTopAndHappinessDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.enums.StatisticsEnum;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 记账统计功能相关
 * Created by yhang on 2018/6/27.
 */

@Controller
@RequestMapping(RedisPrefix.BASE_URL)
@Api(description = "android/ios", tags = "用户记账统计相关")
public class ChargeStatisticsRestController extends BaseController {

    private static final Logger logger = Logger.getLogger(ChargeStatisticsRestController.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WarterOrderRestServiceI warterOrderRestServiceI;

    @ApiOperation(value = "日/周/月支出统计")
    @RequestMapping(value = "/statisticsForSpend/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpend(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        String useAccountCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountCache, UserAccountBookRestEntity.class);
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计日
            Date beginTime = statisticsParamsRestDTO.getBeginTime();
            Date endTime = statisticsParamsRestDTO.getEndTime();
            if (beginTime != null && endTime != null) {
                beginTime = DateUtils.fetchBeginOfDay(statisticsParamsRestDTO.getBeginTime());
                endTime = DateUtils.fetchEndOfDay(statisticsParamsRestDTO.getEndTime());
                if (beginTime.compareTo(endTime) < 0) {
                    Date i = beginTime;
                    beginTime = endTime;
                    endTime = i;
                }
                try {
                    //1为支出类型
                    int orderType = 1;
                    Map<String, Object> map = warterOrderRestServiceI.statisticsForDays(beginTime, endTime, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_TIME_IS_NULL, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计周
            if (StringUtils.isNotEmpty(statisticsParamsRestDTO.getBeginWeek()) && StringUtils.isNotEmpty(statisticsParamsRestDTO.getEndWeek())) {
                String beginWeek = statisticsParamsRestDTO.getBeginWeek();
                String endWeek = statisticsParamsRestDTO.getEndWeek();
                if (beginWeek.compareTo(endWeek) < 0) {
                    String i = beginWeek;
                    beginWeek = endWeek;
                    endWeek = i;
                }
                if (beginWeek.length() < 2) {
                    beginWeek = "0" + beginWeek;
                }
                if (endWeek.length() < 2) {
                    endWeek = "0" + endWeek;
                }
                try {
                    //1为支出类型
                    int orderType = 1;
                    Map<String, Object> map = warterOrderRestServiceI.statisticsForWeeks(beginWeek, endWeek, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_WEEK_IS_NULL, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计月
            try {
                //1为支出类型
                int orderType = 1;
                Map<String, Object> map = warterOrderRestServiceI.statisticsForMonths(userAccountBookRestEntity.getAccountBookId(), orderType);
                return new ResultBean(ApiResultType.OK, map);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else {
            return new ResultBean(ApiResultType.QUERY_FLAG_IS_ERROR, null);
        }
    }

    @ApiOperation(value = "日/周/月支出排行榜和消费情绪统计")
    @RequestMapping(value = "/statisticsForSpendTopAndHappiness/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpendTopAndHappiness(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        String useAccountCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountCache, UserAccountBookRestEntity.class);
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //日统计支出排行榜和情绪统计
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForDaysTopAndHappiness(statisticsParamsRestDTO.getDayTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //周统计支出排行榜和情绪统计
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForWeeksTopAndHappiness(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //月统计支出排行榜和情绪统计
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            if (!StringUtils.startsWithIgnoreCase(statisticsParamsRestDTO.getTime(), "0") && statisticsParamsRestDTO.getTime().length() < 2) {
                statisticsParamsRestDTO.setTime("0" + statisticsParamsRestDTO.getTime());
            }
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForMonthsTopAndHappiness(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return null;
    }

    @ApiOperation(value = "日/周/月收入统计")
    @RequestMapping(value = "/statisticsForIncome/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncome(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        String useAccountCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountCache, UserAccountBookRestEntity.class);
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计日
            Date beginTime = statisticsParamsRestDTO.getBeginTime();
            Date endTime = statisticsParamsRestDTO.getEndTime();
            if (beginTime != null && endTime != null) {
                beginTime = DateUtils.fetchBeginOfDay(statisticsParamsRestDTO.getBeginTime());
                endTime = DateUtils.fetchEndOfDay(statisticsParamsRestDTO.getEndTime());
                if (beginTime.compareTo(endTime) < 0) {
                    Date i = beginTime;
                    beginTime = endTime;
                    endTime = i;
                }
                try {
                    //2为收入类型
                    int orderType = 2;
                    Map<String, Object> map = warterOrderRestServiceI.statisticsForDays(beginTime, endTime, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_TIME_IS_NULL, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计周
            if (StringUtils.isNotEmpty(statisticsParamsRestDTO.getBeginWeek()) && StringUtils.isNotEmpty(statisticsParamsRestDTO.getEndWeek())) {
                String beginWeek = statisticsParamsRestDTO.getBeginWeek();
                String endWeek = statisticsParamsRestDTO.getEndWeek();
                if (beginWeek.compareTo(endWeek) < 0) {
                    String i = beginWeek;
                    beginWeek = endWeek;
                    endWeek = i;
                }
                if (beginWeek.length() < 2) {
                    beginWeek = "0" + beginWeek;
                }
                if (endWeek.length() < 2) {
                    endWeek = "0" + endWeek;
                }
                try {
                    //2为收入类型
                    int orderType = 2;
                    Map<String, Object> map = warterOrderRestServiceI.statisticsForWeeks(beginWeek, endWeek, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_WEEK_IS_NULL, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计月
            try {
                //2为支出
                int orderType = 2;
                Map<String, Object> map = warterOrderRestServiceI.statisticsForMonths(userAccountBookRestEntity.getAccountBookId(), orderType);
                return new ResultBean(ApiResultType.OK, map);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else {
            return new ResultBean(ApiResultType.QUERY_FLAG_IS_ERROR, null);
        }
    }

    @ApiOperation(value = "日/周/月收入排行榜统计")
    @RequestMapping(value = "/statisticsForIncomeTop/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncomeTop(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        String useAccountCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountCache, UserAccountBookRestEntity.class);
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //日统计收入排行榜
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForDaysTop(statisticsParamsRestDTO.getDayTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //周统计收入排行榜
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForWeeksTop(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //月统计收入排行榜
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            if (!StringUtils.startsWithIgnoreCase(statisticsParamsRestDTO.getTime(), "0") && statisticsParamsRestDTO.getTime().length() < 2) {
                statisticsParamsRestDTO.setTime("0" + statisticsParamsRestDTO.getTime());
            }
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForMonthsTop(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return null;
    }

    /**
     * 日/周/月支出/收入统计
     */
    @RequestMapping(value = {"/statisticsForSpendv2/{type}", "/statisticsForIncomev2/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statistics(@PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        int orderType;
        if (StringUtils.contains(request.getRequestURI(), "/statisticsForSpendv2")) {
            orderType = 1;
        } else {
            orderType = 2;
        }
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计日
            Date beginTime = statisticsParamsRestDTO.getBeginTime();
            Date endTime = statisticsParamsRestDTO.getEndTime();
            if (beginTime != null && endTime != null) {
                beginTime = DateUtils.fetchBeginOfDay(statisticsParamsRestDTO.getBeginTime());
                endTime = DateUtils.fetchEndOfDay(statisticsParamsRestDTO.getEndTime());
                try {
                    //1为支出类型
                    Map<String, Object> map = warterOrderRestServiceI.statisticsForDaysv2(beginTime, endTime, userInfoId, orderType);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_TIME_IS_NULL, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计周
            if (StringUtils.isNotEmpty(statisticsParamsRestDTO.getBeginWeek()) && StringUtils.isNotEmpty(statisticsParamsRestDTO.getEndWeek())) {
                String beginWeek = statisticsParamsRestDTO.getBeginWeek();
                String endWeek = statisticsParamsRestDTO.getEndWeek();
                //周数转时间
                Map<String, String> dateByWeeks = DateUtils.getDateByWeeks(Integer.valueOf(beginWeek));
                Map<String, String> dateByWeeks1 = DateUtils.getDateByWeeks(Integer.valueOf(endWeek));
                try {
                    //1为支出类型
                    Map<String, Object> map = warterOrderRestServiceI.statisticsForWeeksv2(dateByWeeks.get("beginTime"), dateByWeeks1.get("endTime"), userInfoId, orderType);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_WEEK_IS_NULL, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计月
            try {
                //1为支出类型
                Map<String, Object> map = warterOrderRestServiceI.statisticsForMonthsv2(userInfoId, orderType);
                return new ResultBean(ApiResultType.OK, map);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else {
            return new ResultBean(ApiResultType.QUERY_FLAG_IS_ERROR, null);
        }
    }

    /**
     * 日/周/月支出/收入排行榜和消费情绪统计
     */
    @RequestMapping(value = "/statisticsForSpendTopAndHappinessv2/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpendTopAndHappinessv2(@PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //日统计支出排行榜和情绪统计
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForDaysTopAndHappinessv2(statisticsParamsRestDTO.getDayTime(), userInfoId);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //周统计支出排行榜和情绪统计
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForWeeksTopAndHappinessv2(statisticsParamsRestDTO.getTime(), userInfoId);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //月统计支出排行榜和情绪统计
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            if (!StringUtils.startsWithIgnoreCase(statisticsParamsRestDTO.getTime(), "0") && statisticsParamsRestDTO.getTime().length() < 2) {
                statisticsParamsRestDTO.setTime("0" + statisticsParamsRestDTO.getTime());
            }
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForMonthsTopAndHappinessv2(statisticsParamsRestDTO.getTime(), userInfoId);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return null;
    }

    @ApiOperation(value = "日/周/月收入排行榜统计")
    @RequestMapping(value = "/statisticsForIncomeTopv2/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncomeTopv2(@PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb != null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //日统计收入排行榜
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForDaysTopv2(statisticsParamsRestDTO.getDayTime(),userInfoId);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            //周统计收入排行榜
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForWeeksTopv2(statisticsParamsRestDTO.getTime(), userInfoId);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //月统计收入排行榜
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO, StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb != null) {
                return rb;
            }
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForMonthsTopv2(statisticsParamsRestDTO.getTime(), userInfoId);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return null;
    }

    @RequestMapping(value = "/statisticsForSpend", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpend(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statisticsForSpend(null, request, statisticsParamsRestDTO);
    }

    @RequestMapping(value = "/statisticsForSpendTopAndHappiness", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpendTopAndHappiness(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statisticsForSpendTopAndHappiness(null, request, statisticsParamsRestDTO);
    }

    @RequestMapping(value = "/statisticsForIncome", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncome(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statisticsForIncome(null, request, statisticsParamsRestDTO);
    }

    @RequestMapping(value = "/statisticsForIncomeTop", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncomeTop(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statisticsForIncomeTop(null, request, statisticsParamsRestDTO);
    }

    @RequestMapping(value = {"/statisticsForSpendv2", "/statisticsForIncomev2"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statistics(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statistics(null, request, statisticsParamsRestDTO);
    }

    @RequestMapping(value = "/statisticsForSpendTopAndHappinessv2", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpendTopAndHappinessv2(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statisticsForSpendTopAndHappinessv2(null, request, statisticsParamsRestDTO);
    }

    @RequestMapping(value = "/statisticsForIncomeTopv2", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncomeTopv2(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return this.statisticsForIncomeTopv2(null, request, statisticsParamsRestDTO);
    }
}
