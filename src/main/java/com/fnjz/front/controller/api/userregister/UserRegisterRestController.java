package com.fnjz.front.controller.api.userregister;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.MD5Utils;
import io.swagger.annotations.*;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户手机号 验证码注册接口
 * Created by yhang on 2018/6/1.
 */

@Controller
@RequestMapping("/api/v1")
@Api(description = "android/ios",tags = "用户注册接口")
public class UserRegisterRestController extends BaseController {
    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    @Autowired
    private UserInfoRestServiceI userInfoRestService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    @ApiOperation(value = "手机号验证码注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value = "手机号",required = true,dataType = "String"),
            @ApiImplicitParam(name="password",value = "密码",required = true,dataType = "String"),
            @ApiImplicitParam(name="verifycode",value = "验证码",required = true,dataType = "String"),
            @ApiImplicitParam(name="mobileSystem",value = "终端系统",required = false,dataType = "String"),
            @ApiImplicitParam(name="mobileSystemVersion",value = "系统版本号",required = false,dataType = "String"),
            @ApiImplicitParam(name="mobileManufacturer",value = "终端厂商",required = false,dataType = "String"),
            @ApiImplicitParam(name="mobileDevice",value = "终端设备号",required = false,dataType = "String")
    })
    @RequestMapping(value = "/register/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
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
                //缓存用户信息
                task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
                String user = JSON.toJSONString(task);
                updateCache(user,map.get("mobile"));
                rb.setSucResult(ApiResultType.OK);
                Map<String, Object> map2 = new HashMap<>();
                String token = createTokenUtils.createToken(map.get("mobile"));
                map2.put("X-AUTH-TOKEN", token);
                map2.put("expire", RedisPrefix.USER_EXPIRE_TIME);
                rb.setResult(map2);
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

    //更新redis缓存通用方法
    private void updateCache(String user,String code){
        //先判断是否存在
        if (StringUtil.isNotEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(code)))) {
            //执行删除
            redisTemplate.delete(MD5Utils.getMD5(code));
        }
        redisTemplate.opsForValue().set(MD5Utils.getMD5(code), user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(@RequestBody Map<String, String> map) {
        return  this.register(null,map);
    }
}
