package com.fnjz.front.controller.api.mycount;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
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

import java.util.Map;

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
            Map map = redisTemplateUtils.getMyCount(shareCode);
            int chargeTotal;
            if (map.size() > 0 && map.containsKey("chargeTotal")) {
                //记账总笔数
                chargeTotal = Integer.valueOf(map.get("chargeTotal") + "");
                //chargeTotal 为空   查询db
                if (chargeTotal < 1) {
                    chargeTotal = warterOrderRestServiceI.chargeTotal(userLoginRestEntity.getAccountBookId());
                    map.put("chargeTotal", chargeTotal + "");
                }
                //打卡天数置为1
                if (!map.containsKey("clockInDays")) {
                    map.put("clockInDays", "1");
                    map.put("clockInTime", (System.currentTimeMillis() + ""));
                }
            } else {
                chargeTotal = warterOrderRestServiceI.chargeTotal(userLoginRestEntity.getAccountBookId());
                map.put("chargeTotal", chargeTotal + "");
                //打卡天数置为1
                if (!map.containsKey("clockInDays")) {
                    map.put("clockInDays", "1");
                    map.put("clockInTime", (System.currentTimeMillis() + ""));
                }
                //重新设置redis javabean转map
                redisTemplateUtils.updateMyCount(shareCode, map);
            }
            map.put("daysCount", daysCount + "/" + DateUtils.getCurrentDay());
            //map 中的chargeTotal已删除 重新赋值
            map.put("chargeTotal", chargeTotal + "");
            return new ResultBean(ApiResultType.OK, map);
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
