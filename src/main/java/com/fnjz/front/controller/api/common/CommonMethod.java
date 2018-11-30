package com.fnjz.front.controller.api.common;

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

import java.util.Map;
import java.util.UUID;

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
        taskExecutor.execute(()->{
            for(int i = 0;i<3000;i++){
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                for(int j = 0;j<10;j++){
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_PUSH+uuid+System.currentTimeMillis(),uuid,7L);
                }
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID+i,uuid,7L);
            }
        });
        taskExecutor.execute(()->{
            for(int i = 3000;i<6000;i++){
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                for(int j = 0;j<10;j++){
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_PUSH+uuid+System.currentTimeMillis(),uuid,7L);
                }
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID+i,uuid,7L);
            }
        });
        taskExecutor.execute(()->{
            for(int i = 6000;i<9000;i++){
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                for(int j = 0;j<10;j++){
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_PUSH+uuid+System.currentTimeMillis(),uuid,7L);
                }
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID+i,uuid,7L);
            }
        });
        taskExecutor.execute(()->{
            for(int i = 9000;i<10000;i++){
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                for(int j = 0;j<10;j++){
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_PUSH+uuid+System.currentTimeMillis(),uuid,7L);
                }
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID+i,uuid,7L);
            }
        });
    }
}
