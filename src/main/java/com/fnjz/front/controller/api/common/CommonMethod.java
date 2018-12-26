package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ValidateUtils;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 通用方法
 * Created by yhang on 2018/6/1.
 */

@Controller
@RequestMapping(RedisPrefix.BASE_URL)
@Api(description = "android/ios",tags = "公用调用接口")
public class CommonMethod extends BaseController {

    private static final Logger logger = Logger.getLogger(CommonMethod.class);

    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    @ApiOperation(value = "查询手机号是否注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value = "手机号",required = true,dataType = "String")
    })
    @RequestMapping(value = "/checkMobile/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkMobile(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        if(StringUtil.isEmpty(map.get("mobile") )){
            rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
            return rb;
        }
        if(!ValidateUtils.isMobile(map.get("mobile"))){
            rb.setFailMsg(ApiResultType.MOBILE_FORMAT_ERROR);
            return rb;
        }
        try {
            UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
            if (task != null) {
                rb.setFailMsg(ApiResultType.MOBILE_IS_EXISTED);
                return rb;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
        }
        rb.setSucResult(ApiResultType.OK);
        return rb;
    }

    @RequestMapping(value = "/checkMobile" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkMobile(@RequestBody Map<String, String> map) {
        return this.checkMobile(null,map);
    }

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping(value = "/redis" , method = RequestMethod.POST)
    @ResponseBody
    public void redis() {
        List<String> arrays = new ArrayList<>();
        arrays.add("aaaaa");
        arrays.add("bbbbb");
        arrays.add("ccccc");
        arrays.add("dddddd");
        taskExecutor.execute(()->{
            for(int i = 1;i<105;i++){
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                for(int j = 0;j<7;j++){
                    LocalDate localDate = LocalDate.now();
                    localDate = localDate.minusDays(j);
                    DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String time = localDate.format(formatters);
                    redisTemplateUtils.setListRight(RedisPrefix.PREFIX_WXAPPLET_PUSH + uuid + "_" + time, arrays, 1,7L);
                }
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID+i,uuid,7L);
            }
        });
    }
    @RequestMapping(value = "/redis2" , method = RequestMethod.POST)
    @ResponseBody
    public void redis2() {
        List<String> list = new ArrayList<>();
        Map<String,Integer> map1 = new HashMap<>();
        map1.put("7",5);
        Map<String,Integer> map2 = new HashMap<>();
        map2.put("14",10);
        Map<String,Integer> map3 = new HashMap<>();
        map3.put("21",15);
        Map<String,Integer> map4 = new HashMap<>();
        map4.put("28",20);
        list.add(JSON.toJSON(map1).toString());
        list.add(JSON.toJSON(map2).toString());
        list.add(JSON.toJSON(map3).toString());
        list.add(JSON.toJSON(map4).toString());
        redisTemplateUtils.setListRight(RedisPrefix.SYS_INTEGRAL_SIGN_IN_CYCLE_AWARE,list,2,null);
    }
}
