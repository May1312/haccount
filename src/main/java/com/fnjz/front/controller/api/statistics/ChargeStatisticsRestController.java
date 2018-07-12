package com.fnjz.front.controller.api.statistics;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.statistics.*;
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
@RequestMapping("/api/v1")
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
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb!=null) {
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
                    Map<String,Object> map = warterOrderRestServiceI.statisticsForDays(beginTime, endTime, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK,map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR,null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_TIME_IS_NULL,null);
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
                try {
                    //1为支出类型
                    int orderType = 1;
                    Map<String,Object> map = warterOrderRestServiceI.statisticsForWeeks(beginWeek, endWeek, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK,map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR,null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_WEEK_IS_NULL,null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计月
            try {
                //1为支出类型
                int orderType = 1;
                Map<String,Object> map = warterOrderRestServiceI.statisticsForMonths(userAccountBookRestEntity.getAccountBookId(), orderType);
                return new ResultBean(ApiResultType.OK,map);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
            }
        } else {
            rb.setFailMsg(ApiResultType.QUERY_FLAG_IS_ERROR);
            return new ResultBean(ApiResultType.QUERY_FLAG_IS_ERROR,null);
        }
    }

    @ApiOperation(value = "日/周/月支出排行榜和消费情绪统计")
    @RequestMapping(value = "/statisticsForSpendTopAndHappiness/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpendTopAndHappiness(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
        if (rb!=null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        String useAccountCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountCache, UserAccountBookRestEntity.class);
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb!=null) {
                return rb;
            }
            //日统计支出排行榜和情绪统计
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForDaysTopAndHappiness(statisticsParamsRestDTO.getDayTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK,list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb!=null) {
                return rb;
            }
            //周统计支出排行榜和情绪统计
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForWeeksTopAndHappiness(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK,list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //月统计支出排行榜和情绪统计
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
            if(rb!=null){
                return rb;
            }
            if (!StringUtils.startsWithIgnoreCase(statisticsParamsRestDTO.getTime(), "0") && statisticsParamsRestDTO.getTime().length() < 2) {
                statisticsParamsRestDTO.setTime("0" + statisticsParamsRestDTO.getTime());
            }
            try {
                StatisticsSpendTopAndHappinessDTO list = warterOrderRestServiceI.statisticsForMonthsTopAndHappiness(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK,list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
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
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb!=null) {
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
                    Map<String,Object> map = warterOrderRestServiceI.statisticsForDays(beginTime, endTime, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK,map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR,null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_TIME_IS_NULL,null);
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
                try {
                    //2为收入类型
                    int orderType = 2;
                    Map<String,Object> map = warterOrderRestServiceI.statisticsForWeeks(beginWeek, endWeek, userAccountBookRestEntity.getAccountBookId(), orderType);
                    return new ResultBean(ApiResultType.OK,map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR,null);
                }
            } else {
                return new ResultBean(ApiResultType.QUERY_WEEK_IS_NULL,null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //统计月
            try {
                //2为支出
                int orderType = 2;
                Map<String,Object> map = warterOrderRestServiceI.statisticsForMonths(userAccountBookRestEntity.getAccountBookId(), orderType);
                return new ResultBean(ApiResultType.OK,map);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
            }
        } else {
            return new ResultBean(ApiResultType.QUERY_FLAG_IS_ERROR,null);
        }
    }

    @ApiOperation(value = "日/周/月收入排行榜统计")
    @RequestMapping(value = "/statisticsForIncomeTop/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForIncomeTop(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_CHART);
        if (rb!=null) {
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        String useAccountCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountCache, UserAccountBookRestEntity.class);
        if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_DAY.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb!=null) {
                return rb;
            }
            //日统计收入排行榜
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForDaysTop(statisticsParamsRestDTO.getDayTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK,list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_WEEK.getIndex(), statisticsParamsRestDTO.getFlag())) {
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb!=null) {
                return rb;
            }
            //周统计收入排行榜
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForWeeksTop(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK,list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
            }
        } else if (StringUtils.equals(StatisticsEnum.STATISTICS_FOR_MONTH.getIndex(), statisticsParamsRestDTO.getFlag())) {
            //月统计收入排行榜
            rb = ParamValidateUtils.checkStatistics(statisticsParamsRestDTO,StatisticsEnum.STATISTICS_FOR_TOP);
            if (rb!=null) {
                return rb;
            }
            if (!StringUtils.startsWithIgnoreCase(statisticsParamsRestDTO.getTime(), "0") && statisticsParamsRestDTO.getTime().length() < 2) {
                statisticsParamsRestDTO.setTime("0" + statisticsParamsRestDTO.getTime());
            }
            try {
                StatisticsIncomeTopDTO list = warterOrderRestServiceI.statisticsForMonthsTop(statisticsParamsRestDTO.getTime(), userAccountBookRestEntity.getAccountBookId());
                return new ResultBean(ApiResultType.OK,list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR,null);
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
}
