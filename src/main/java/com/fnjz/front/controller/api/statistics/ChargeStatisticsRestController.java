package com.fnjz.front.controller.api.statistics;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.StatisticsDaysRestDTO;
import com.fnjz.front.entity.api.StatisticsParamsRestDTO;
import com.fnjz.front.entity.api.StatisticsWeeksRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
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
import java.util.List;

/**
 * 记账统计功能相关
 * Created by yhang on 2018/6/27.
 */

@Controller
@RequestMapping("/api/v1")
@Api(description = "android/ios",tags = "用户记账统计相关")
public class ChargeStatisticsRestController extends BaseController {

    private static final Logger logger = Logger.getLogger(ChargeStatisticsRestController.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WarterOrderRestServiceI warterOrderRestServiceI;

    @ApiOperation(value = "日/周/月收入统计")
    @RequestMapping(value = "/statisticsForSpend/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpend(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        if (StringUtils.isEmpty(statisticsParamsRestDTO.getFlag())) {
            rb.setFailMsg(ApiResultType.TYPE_FLAG_IS_NULL);
            return rb;
        }
        String userInfoId = (String)request.getAttribute("userInfoId");
        String key = (String)request.getAttribute("key");
        String useAccountrCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), key);
        UserAccountBookRestEntity userAccountBookRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
        if(StringUtils.equals("1",statisticsParamsRestDTO.getFlag())){
            //统计日
            Date beginTime = statisticsParamsRestDTO.getBeginTime();
            Date endTime = statisticsParamsRestDTO.getEndTime();
            if(beginTime!=null && endTime!=null){
                if(beginTime.compareTo(endTime)<0){
                    Date i = beginTime;
                    beginTime = endTime;
                    endTime = i;
                }
                try {
                    List<StatisticsDaysRestDTO> list = warterOrderRestServiceI.statisticsForDays(beginTime, endTime, userAccountBookRestEntity.getAccountBookId());
                    rb.setSucResult(ApiResultType.OK);
                    rb.setResult(list);
                    return rb;
                } catch (Exception e) {
                    logger.error(e.toString());
                    rb.setFailMsg(ApiResultType.SERVER_ERROR);
                    return rb;
                }
            }else{
                rb.setFailMsg(ApiResultType.QUERY_TIME_IS_NULL);
                return rb;
            }
        }else if(StringUtils.equals("2",statisticsParamsRestDTO.getFlag())){
            //统计周
            if(StringUtils.isNotEmpty(statisticsParamsRestDTO.getBeginWeek()) && StringUtils.isNotEmpty(statisticsParamsRestDTO.getEndWeek())){
                String beginWeek = statisticsParamsRestDTO.getBeginWeek();
                String endWeek = statisticsParamsRestDTO.getEndWeek();
                if(beginWeek.compareTo(endWeek)<0){
                    String i = beginWeek;
                    beginWeek = endWeek;
                    endWeek = i;
                }
                List<StatisticsWeeksRestDTO> list = warterOrderRestServiceI.statisticsForWeeks(beginWeek, endWeek, userAccountBookRestEntity.getAccountBookId());
                rb.setSucResult(ApiResultType.OK);
                rb.setResult(list);
                return rb;
            }else{
                rb.setFailMsg(ApiResultType.QUERY_WEEK_IS_NULL);
                return rb;
            }
        }
        return rb;
    }

    @RequestMapping(value = "/statisticsForSpend", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean statisticsForSpend(HttpServletRequest request, @RequestBody StatisticsParamsRestDTO statisticsParamsRestDTO) {
        return  this.statisticsForSpend(null,request,statisticsParamsRestDTO);
    }
}
