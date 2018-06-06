package com.fnjz.front.controller.api.gesture;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.MD5Utils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 移动端手势密码相关
 * Created by yhang on 2018/6/4.
 */

@Controller
@RequestMapping("/api/v1")
@Api(description = "android/ios",tags = "手势密码相关")
public class GestureRestController extends BaseController{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserInfoRestServiceI userInfoRestServiceI;

    @ApiOperation(value = "查询手势开关状态及手势密码")
    @RequestMapping(value = "/checkGestureType/{type}" , method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkGestureType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        //从缓存中查询开关状态
        String code = (String) request.getAttribute("code");
        String user = (String) redisTemplate.opsForValue().get(MD5Utils.getMD5(code));
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
        rb.setSucResult(ApiResultType.OK);
        Map<String,String> map = new HashMap<>();
        map.put("gesturePwType",userLoginRestEntity.getGesturePwType());
        map.put("gesturePw",userLoginRestEntity.getGesturePw());
        rb.setResult(map);
        return rb;
    }

    @ApiOperation(value = "修改手势开关状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name="gesturePwType",value = "开关状态 0：打开 1：关闭",required = true,dataType = "String")
    })
    @RequestMapping(value = "/updateGestureType/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateGestureType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,HttpServletRequest request,@RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        if(StringUtils.isEmpty(map.get("gesturePwType"))){
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_ERROR);
            return rb;
        }
        if(map.get("gesturePwType").length()>1){
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_LENGTH_ERROR);
            return rb;
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        int i = userInfoRestServiceI.updateGestureType(userInfoId,map.get("gesturePwType"));
        if(i<1){
            rb.setFailMsg(ApiResultType.GESTURE_UPDATE_ERROR);
            return rb;
        }
        rb.setSucResult(ApiResultType.OK);
        //更新redis缓存
        String code = (String) request.getAttribute("code");
        String user = (String) redisTemplate.opsForValue().get(MD5Utils.getMD5(code));
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
        userLoginRestEntity.setGesturePwType(map.get("gesturePwType"));
        redisTemplate.delete(MD5Utils.getMD5(code));
        String user2 = JSON.toJSONString(userLoginRestEntity);
        redisTemplate.opsForValue().set(MD5Utils.getMD5(code), user2,RedisPrefix.USER_VALID_TIME,  TimeUnit.DAYS);
        return rb;
    }

    @ApiOperation(value = "修改手势密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="gesturePw",value = "手势密码",required = true,dataType = "String")
    })
    @RequestMapping(value = "/updateGesture/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateGesture(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,HttpServletRequest request,@RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        //去掉手势密码为空校验 ，为空表示取消
        /*if(StringUtils.isEmpty(map.get("gesturePw"))){
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_ERROR);
            return rb;
        }*/
        String userInfoId = (String) request.getAttribute("userInfoId");
        int i = userInfoRestServiceI.updateGesture(userInfoId,map.get("gesturePw"));
        if(i<1){
            rb.setFailMsg(ApiResultType.GESTURE_UPDATE_ERROR);
            return rb;
        }
        rb.setSucResult(ApiResultType.OK);
        //更新redis缓存
        String code = (String) request.getAttribute("code");
        String user = (String) redisTemplate.opsForValue().get(MD5Utils.getMD5(code));
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
        userLoginRestEntity.setGesturePw(map.get("gesturePw"));
        redisTemplate.delete(MD5Utils.getMD5(code));
        String user2 = JSON.toJSONString(userLoginRestEntity);
        redisTemplate.opsForValue().set(MD5Utils.getMD5(code), user2,30,  TimeUnit.DAYS);
        return rb;
    }

    @ApiOperation(value = "手势密码登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="gesturePw",value = "手势密码",required = true,dataType = "String")
    })
    @RequestMapping(value = "/gestureToLogin/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean gestureToLogin(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,HttpServletRequest request,@RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端："+type);
        ResultBean rb = new ResultBean();
        if(StringUtils.isEmpty(map.get("gesturePw"))){
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_ERROR);
            return rb;
        }
        //验证手势密码
        String code = (String) request.getAttribute("code");
        String user = (String)redisTemplate.opsForValue().get(MD5Utils.getMD5(code));
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
        if(StringUtils.equals(userLoginRestEntity.getGesturePw(),map.get("gesturePw"))){
            rb.setSucResult(ApiResultType.OK);
        }else{
            rb.setFailMsg(ApiResultType.GESTURE_PASSWORD_IS_ERROR);
        }
        return rb;
    }

    @RequestMapping(value = "/checkGestureType" , method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkGestureType(HttpServletRequest request) {
        return this.checkGestureType(null,request);
    }

    @RequestMapping(value = "/updateGestureType" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateGestureType(HttpServletRequest request,@RequestBody Map<String, String> map) {
        return this.updateGestureType(null,request,map);
    }

    @RequestMapping(value = "/updateGesture" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updateGesture(HttpServletRequest request,@RequestBody Map<String, String> map) {
        return this.updateGesture(null,request,map);
    }

    @RequestMapping(value = "/gestureToLogin" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean gestureToLogin(HttpServletRequest request,@RequestBody Map<String, String> map) {
        return this.gestureToLogin(null,request,map);
    }
}
