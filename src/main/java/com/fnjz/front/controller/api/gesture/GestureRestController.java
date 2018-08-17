package com.fnjz.front.controller.api.gesture;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 移动端手势密码相关
 * Created by yhang on 2018/6/4.
 */

@Controller
@RequestMapping(RedisPrefix.BASE_URL)
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
        //从缓存中查询开关状态
        try {
            String key = (String) request.getAttribute("key");
            UserLoginRestEntity userLoginRestEntity = redisTemplateUtils.getUserLoginRestEntityCache(key);
            return CommonUtils.returnGesture(userLoginRestEntity);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "修改手势开关状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gesturePwType", value = "开关状态 0：打开 1：关闭", required = true, dataType = "String")
    })
    @RequestMapping(value = "/updateGestureType/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateGestureType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb;
        rb = ParamValidateUtils.checkGesture(map);
        if (rb != null) {
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            int i = userInfoRestServiceI.updateGestureType(userInfoId, map.get("gesturePwType"));
            if (i < 1) {
                return new ResultBean(ApiResultType.GESTURE_UPDATE_ERROR, null);
            }
            //更新redis缓存
            String key = (String) request.getAttribute("key");
            UserLoginRestEntity userLoginRestEntity = redisTemplateUtils.getUserLoginRestEntityCache(key);
            userLoginRestEntity.setGesturePwType(map.get("gesturePwType"));
            redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "上传/修改手势密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gesturePw", value = "手势密码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/updateGesture/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateGesture(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (map.containsKey("gesturePw")) {
            int i = userInfoRestServiceI.updateGesture(userInfoId, map.get("gesturePw"));
            if (i < 1) {
                return new ResultBean(ApiResultType.GESTURE_UPDATE_ERROR, null);
            }
            //更新redis缓存
            String key = (String) request.getAttribute("key");
            UserLoginRestEntity userLoginRestEntity = redisTemplateUtils.getUserLoginRestEntityCache(key);
            userLoginRestEntity.setGesturePw(map.get("gesturePw"));
            redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
            return new ResultBean(ApiResultType.OK, null);
        } else {
            return new ResultBean(ApiResultType.GESTURE_PARAMS_ERROR, null);
        }
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
