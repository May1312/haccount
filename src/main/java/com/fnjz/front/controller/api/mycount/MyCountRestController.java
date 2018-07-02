package com.fnjz.front.controller.api.mycount;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.MyCountRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "获取我的页面数据统计")
    @RequestMapping(value = "/getMyCount/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getMyCount(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        try {
            //本月记账天数
            //获取当前年月
            String currentYearMonth = DateUtils.getCurrentYearMonth();
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            int daysCount = warterOrderRestServiceI.countChargeDays(currentYearMonth,userLoginRestEntity.getAccountBookId());
            //获取连续打卡+记账总笔数
            String s =(String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_MY_COUNT + shareCode);
            MyCountRestDTO myCountRestDTO = JSON.parseObject(s, MyCountRestDTO.class);
            int chargeTotal;
            if(myCountRestDTO!=null){
                //记账总笔数
                chargeTotal = myCountRestDTO.getChargeTotal();
                //chargeTotal 为空   查询db
                if(chargeTotal<1){
                    chargeTotal = warterOrderRestServiceI.chargeTotal(userLoginRestEntity.getAccountBookId());
                    myCountRestDTO.setChargeTotal(chargeTotal);
                }
            }else{
                myCountRestDTO = new MyCountRestDTO();
                chargeTotal = warterOrderRestServiceI.chargeTotal(userLoginRestEntity.getAccountBookId());
                myCountRestDTO.setChargeTotal(chargeTotal);
                //重新设置redis
                String json = JSON.toJSONString(myCountRestDTO);
                redisTemplate.opsForValue().set(RedisPrefix.PREFIX_MY_COUNT + shareCode,json,RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            }
            myCountRestDTO.setDaysCount(daysCount+"/"+DateUtils.getCurrentDay());
            rb.setSucResult(ApiResultType.OK);
            rb.setResult(myCountRestDTO);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    /**
     * 从cache获取用户账本信息通用方法
     */
    private String getUseAccountCache(int userInfoId, String shareCode) {
        String user_account = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + shareCode);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user_account)) {
            UserAccountBookRestEntity task = warterOrderRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            //设置redis缓存 缓存用户账本信息 30天
            String r_user_account = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + shareCode, r_user_account, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return r_user_account;
        }
        return user_account;
    }

    @RequestMapping(value = "/getMyCount", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getMyCount(HttpServletRequest request) {
        return this.getMyCount(null, request);
    }
}
