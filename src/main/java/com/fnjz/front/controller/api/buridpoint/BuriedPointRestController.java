package com.fnjz.front.controller.api.buridpoint;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.buriedpointtype.BuriedPointRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.buriedpoint.BuriedPointServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.utils.rabbitmq.RabbitmqUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Buried
 * @Description: 埋点处理controller
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class BuriedPointRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BuriedPointRestController.class);

    @Autowired
    private BuriedPointServiceI checkRestServiceI;

    @Autowired
    private RabbitmqUtils rabbitmqUtils;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 获取埋点类型
     * @param type
     * @return
     */
    @RequestMapping(value = "/getBuriedPointType/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBuriedPointType(@PathVariable("type") String type) {
            //返回所有
        return checkRestServiceI.getBuriedPointType();
    }

    /**
     * 埋点统计上传
     * @param type
     * @return
     */
    @RequestMapping(value = "/uploadbpData/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadbpData(@PathVariable("type") String type,HttpServletRequest request, @RequestBody BuriedPointRestEntity entity) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String key = (String) request.getAttribute("key");
        taskExecutor.execute(()->{
            entity.setUserInfoId(Integer.valueOf(userInfoId));
            entity.setClientId(type);
            if(StringUtils.equals("wxapplet",type)){
                String user = redisTemplateUtils.getUserCache(key);
                UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
                entity.setDeviceNum(userLoginRestEntity.getWechatAuth());
            }
            rabbitmqUtils.publish("point",entity);
        });
        return new ResultBean(ApiResultType.OK,null);
    }

    @RequestMapping(value = "/getBuriedPointType", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getBuriedPointType() {
        return this.getBuriedPointType(null);
    }

    @RequestMapping(value = "/uploadbpData", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean uploadbpData(HttpServletRequest request, @RequestBody BuriedPointRestEntity entity) {
        return this.uploadbpData(null,request,entity);
    }
}
