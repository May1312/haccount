package com.fnjz.front.controller.api.userregister;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 用户手机号 验证码注册接口
 * Created by yhang on 2018/6/1.
 */

@Controller
@RequestMapping("/api/v1")
@Api(value = "appregister", description = "移动端----->注册接口", tags = "appregister")
public class UserRegisterRestController extends BaseController {
    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    @Autowired
    private UserInfoRestServiceI userInfoRestService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "手机号验证码注册")
    @RequestMapping(value = "/register/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(@PathVariable("type") String type, @RequestBody Map<String, String> map) {
        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        //手机号或验证码或密码为空
        if(StringUtil.isEmpty(map.get("mobile") ) || StringUtil.isEmpty(map.get("password")) || StringUtil.isEmpty(map.get("verifycode"))){
            rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
            return rb;
        }
        //验证手机号是否已存在
        UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
        if (task != null) {
            rb.setFailMsg(ApiResultType.MOBILE_IS_EXISTED);
            return rb;
        }
        //校验验证码
        String code = (String)redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_REGISTER+map.get("mobile"));
        //验证码已失效
        if(code==null){
            rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
            return rb;
        }
        if(StringUtil.equals(code,map.get("verifycode"))){
            //验证码校验通过
            //先生成用户详情表获取id--->存入用户登录表
            UserInfoRestEntity userInfo = new UserInfoRestEntity();
            //设置手机号
            userInfo.setMobile(map.get("mobile"));
            //设置密码
            userInfo.setPassword(map.get("password"));
            if(map.get("mobileSystem")!=null){
                //终端系统
                userInfo.setMobileSystem(map.get("mobileSystem"));
            }
            if(map.get("mobileSystemVersion")!=null){
                //系统版本号
                userInfo.setMobileSystemVersion(map.get("mobileSystemVersion"));
            }
            if(map.get("mobileManufacturer")!=null){
                //终端厂商
                userInfo.setMobileManufacturer(map.get("mobileManufacturer"));
            }
            if(map.get("mobileDevice")!=null){
                //终端设备号
                userInfo.setMobileDevice(map.get("mobileDevice"));
            }
            //执行新增
            int insertId = userInfoRestService.insert(userInfo);
            if(insertId>0){
                //删除短信验证码
                redisTemplate.delete(RedisPrefix.PREFIX_USER_VERIFYCODE_REGISTER+map.get("mobile"));
                rb.setSucResult(ApiResultType.OK);
            }else{
                rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
            }
        }else{
            //验证错误
            rb.setFailMsg(ApiResultType.VERIFYCODE_IS_ERROR);
            return rb;
        }
        return rb;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(@RequestBody Map<String, String> map) {
        return  this.register(null,map);
    }
}
