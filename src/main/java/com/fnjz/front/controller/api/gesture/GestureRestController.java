package com.fnjz.front.controller.api.gesture;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 移动端手势密码相关
 * Created by yhang on 2018/6/4.
 */

@Controller
@RequestMapping("/api/v1")
@Api(description = "android/ios", tags = "手势密码相关")
public class GestureRestController extends BaseController {

    private static final Logger logger = Logger.getLogger(GestureRestController.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserInfoRestServiceI userInfoRestServiceI;

    @ApiOperation(value = "查询手势开关状态及手势密码")
    @RequestMapping(value = "/checkGestureType/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkGestureType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //从缓存中查询开关状态
        try {
            String key = (String) request.getAttribute("key");
            String user = redisTemplateUtils.getUserCache(key);
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
            rb.setSucResult(ApiResultType.OK);
            Map<String, String> map = new HashMap<>();
            map.put("gesturePwType", userLoginRestEntity.getGesturePwType());
            map.put("gesturePw", userLoginRestEntity.getGesturePw());
            rb.setResult(map);
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "修改手势开关状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gesturePwType", value = "开关状态 0：打开 1：关闭", required = true, dataType = "String")
    })
    @RequestMapping(value = "/updateGestureType/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateGestureType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        if (StringUtils.isEmpty(map.get("gesturePwType"))) {
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_ERROR);
            return rb;
        }
        if (map.get("gesturePwType").length() > 1) {
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_LENGTH_ERROR);
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            int i = userInfoRestServiceI.updateGestureType(userInfoId, map.get("gesturePwType"));
            if (i < 1) {
                rb.setFailMsg(ApiResultType.GESTURE_UPDATE_ERROR);
                return rb;
            }
            //更新redis缓存
            String key = (String) request.getAttribute("key");
            String user = redisTemplateUtils.getUserCache(key);
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
            userLoginRestEntity.setGesturePwType(map.get("gesturePwType"));
            String user2 = JSON.toJSONString(userLoginRestEntity);
            redisTemplateUtils.updateCache(user2, key);
            rb.setSucResult(ApiResultType.OK);
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "上传/修改手势密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gesturePw", value = "手势密码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/updateGesture/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateGesture(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (map.containsKey("gesturePw")) {
            int i = userInfoRestServiceI.updateGesture(userInfoId, map.get("gesturePw"));
            if (i < 1) {
                rb.setFailMsg(ApiResultType.GESTURE_UPDATE_ERROR);
                return rb;
            }
            //更新redis缓存
            String key = (String) request.getAttribute("key");
            String user = redisTemplateUtils.getUserCache(key);
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(user, UserLoginRestEntity.class);
            userLoginRestEntity.setGesturePw(map.get("gesturePw"));
            String user2 = JSON.toJSONString(userLoginRestEntity);
            redisTemplateUtils.updateCache(user2, key);
            rb.setSucResult(ApiResultType.OK);
        } else {
            rb.setFailMsg(ApiResultType.GESTURE_PARAMS_ERROR);
        }
        return rb;
    }

    @RequestMapping(value = "/checkGestureType", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkGestureType(HttpServletRequest request) {
        return this.checkGestureType(null, request);
    }

    @RequestMapping(value = "/updateGestureType", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateGestureType(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return this.updateGestureType(null, request, map);
    }

    @RequestMapping(value = "/updateGesture", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateGesture(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return this.updateGesture(null, request, map);
    }
}
