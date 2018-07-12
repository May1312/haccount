package com.fnjz.front.controller.api.mycount;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.MyCountRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 我的 记账天数  连续打卡天数  记账总笔数统计
 * Created by yhang on 2018/6/22.
 */

@Controller
@RequestMapping("/api/v1")
public class MyCountRestController extends BaseController {

    private static final Logger logger = Logger.getLogger(MyCountRestController.class);

    @Autowired
    private WarterOrderRestServiceI warterOrderRestServiceI;
    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @ApiOperation(value = "获取我的页面数据统计")
    @RequestMapping(value = "/getMyCount/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getMyCount(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        try {
            //获取当前年月
            String currentYearMonth = DateUtils.getCurrentYearMonth();
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserAccountBookRestEntity userLoginRestEntity = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            int daysCount = warterOrderRestServiceI.countChargeDays(currentYearMonth, userLoginRestEntity.getAccountBookId());
            //获取连续打卡+记账总笔数
            MyCountRestDTO myCountRestDTO = redisTemplateUtils.getMyCountRestDTOCache(shareCode);
            int chargeTotal;
            if (myCountRestDTO != null) {
                //记账总笔数
                chargeTotal = myCountRestDTO.getChargeTotal();
                //chargeTotal 为空   查询db
                if (chargeTotal < 1) {
                    chargeTotal = warterOrderRestServiceI.chargeTotal(userLoginRestEntity.getAccountBookId());
                    myCountRestDTO.setChargeTotal(chargeTotal);
                }
            } else {
                myCountRestDTO = new MyCountRestDTO();
                chargeTotal = warterOrderRestServiceI.chargeTotal(userLoginRestEntity.getAccountBookId());
                myCountRestDTO.setChargeTotal(chargeTotal);
                //重新设置redis
                String json = JSON.toJSONString(myCountRestDTO);
                redisTemplateUtils.updateMyCount(shareCode, json);
            }
            myCountRestDTO.setDaysCount(daysCount + "/" + DateUtils.getCurrentDay());
            return new ResultBean(ApiResultType.OK, myCountRestDTO);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = "/getMyCount", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getMyCount(HttpServletRequest request) {
        return this.getMyCount(null, request);
    }
}
