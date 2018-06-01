package com.fnjz.front.controller.api.common;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用方法
 * Created by yhang on 2018/6/1.
 */

@Controller
@RequestMapping("/api/v1")
@Api(value = "appcommonmethod", description = "移动端----->公用接口", tags = "appcommonmethod")
public class Common extends BaseController {

    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    @ApiOperation(value = "查询手机号是否注册")
    @RequestMapping(value = "/checkMobile/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkMobile(@PathVariable("type") String type, @RequestBody Map<String, String> map) {

        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        if(StringUtil.isEmpty(map.get("mobile") )){
            rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
            return rb;
        }
        UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
        if (task != null) {
            rb.setFailMsg(ApiResultType.MOBILE_IS_EXISTED);
            return rb;
        }
        rb.setSucResult(ApiResultType.OK);
        return rb;
    }

    @RequestMapping(value = "/checkMobile" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkMobile(@RequestBody Map<String, String> map) {
        return this.checkMobile(null,map);
    }
}
