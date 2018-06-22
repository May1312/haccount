package com.fnjz.front.controller.api.userinfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.ValidateUtils;
import com.fnjz.front.utils.WeChatUtils;
import com.fnjz.utils.upload.QiNiuUploadFileUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 用户详情表相关
 * @date 2018-05-30 14:05:50
 */
@Controller
@RequestMapping("/api/v1")
@Api(description = "android/ios", tags = "账户安全接口")
public class UserInfoRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserInfoRestController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserInfoRestServiceI userInfoRestServiceI;

    @Autowired
    private UserLoginRestServiceI userLoginRestServiceI;

    @ApiOperation(value = "绑定/更换手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "verifycode", value = "验证码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "微信用户首次绑定手机号时需设置密码（MD5加密）", dataType = "String")
    })
    @RequestMapping(value = "/bindMobile/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean bindMobile(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        try {
            String r_redis = (String) request.getAttribute("code");
            String r_user = getUserCache(r_redis);
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(r_user, UserLoginRestEntity.class);
            if (StringUtils.isNotEmpty(userLoginRestEntity.getMobile()) && StringUtils.isNotEmpty(userLoginRestEntity.getPassword())) {
                //判断手机号 验证码
                if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("verifycode"))) {
                    rb.setFailMsg(ApiResultType.USERNAME_OR_VERIFYCODE_ISNULL);
                    return rb;
                }
                if (!ValidateUtils.isMobile(map.get("mobile"))) {
                    rb.setFailMsg(ApiResultType.MOBILE_FORMAT_ERROR);
                    return rb;
                }
                //获取验证码
                String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"));
                if (StringUtils.isEmpty(code)) {
                    //验证码为空
                    rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
                    return rb;
                }
                if (StringUtil.equals(code, map.get("verifycode"))) {
                    //执行更新手机号流程
                    String userInfoId = (String) request.getAttribute("userInfoId");
                    int i = userInfoRestServiceI.updateMobile(userInfoId, map.get("mobile"));
                    if (i < 1) {
                        rb.setFailMsg(ApiResultType.MOBILE_UPDATE_ERROR);
                        return rb;
                    }
                    //更新用户缓存
                    userLoginRestEntity.setMobile(map.get("mobile"));
                    String user = JSON.toJSONString(userLoginRestEntity);
                    updateCache(user, map.get("mobile"));
                    rb.setSucResult(ApiResultType.OK);
                } else {
                    rb.setFailMsg(ApiResultType.VERIFYCODE_IS_ERROR);
                }
            } else {//微信绑定手机号密码
                //判断手机号 密码 验证码
                if (StringUtils.isEmpty(map.get("mobile")) || StringUtils.isEmpty(map.get("verifycode")) || StringUtils.isEmpty(map.get("password"))) {
                    rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
                    return rb;
                }
                //获取验证码
                String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"));
                if (StringUtils.isEmpty(code)) {
                    //验证码为空
                    rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
                    return rb;
                }
                if (StringUtils.equals(code, map.get("verifycode"))) {
                    //执行更新手机号 密码 流程
                    String userInfoId = (String) request.getAttribute("userInfoId");
                    int i = userInfoRestServiceI.updateMobileAndPWD(userInfoId, map.get("mobile"), map.get("password"));
                    if (i < 1) {
                        rb.setFailMsg(ApiResultType.BIND_MOBILE_PWD_ERROR);
                        return rb;
                    }
                    //更新用户缓存
                    userLoginRestEntity.setMobile(map.get("mobile"));
                    userLoginRestEntity.setPassword(map.get("password"));
                    String user = JSON.toJSONString(userLoginRestEntity);
                    updateCache(user, map.get("mobile"));
                    rb.setSucResult(ApiResultType.OK);
                } else {
                    rb.setFailMsg(ApiResultType.VERIFYCODE_IS_ERROR);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "手机注册用户绑定微信wechat")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/bindWeChat/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean bindWeChat(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //判断CODE
        if (StringUtil.isEmpty(map.get("code"))) {
            rb.setFailMsg(ApiResultType.WECHAT_CODE_ISNULL);
            return rb;
        }
        try {
            String r_redis = (String) request.getAttribute("code");
            String r_user = getUserCache(r_redis);
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(r_user, UserLoginRestEntity.class);
            if (StringUtils.isNotEmpty(userLoginRestEntity.getMobile()) && StringUtils.isNotEmpty(userLoginRestEntity.getPassword())) {
                //获取unionid  user.getString("unionid")
                JSONObject user = WeChatUtils.getUser(map.get("code"));
                if (user == null) {
                    rb.setFailMsg(ApiResultType.WECHAT_BIND_ERROR);
                    return rb;
                }
                //判断db中是否已经存在此unionid
                UserLoginRestEntity task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "wechat_auth", user.getString("unionid"));
                if (task != null) {
                    //微信号已经注册
                    rb.setFailMsg(ApiResultType.WECHAT_IS_BINDED);
                    return rb;
                }
                //更新绑定unionid
                int i = userInfoRestServiceI.updateWeChat(userLoginRestEntity.getMobile(), user.getString("unionid"));
                if (i < 1) {
                    rb.setFailMsg(ApiResultType.WECHAT_BIND_ERROR);
                    return rb;
                }
                //设置redis缓存 缓存用户信息
                userLoginRestEntity.setWechatAuth(user.getString("unionid"));
                String userToString = JSON.toJSONString(userLoginRestEntity);
                updateCache(userToString, r_redis);
                rb.setSucResult(ApiResultType.OK);
            } else if (StringUtils.isNotEmpty(userLoginRestEntity.getWechatAuth())) {
                //非手机号注册用户无法绑定微信
                rb.setFailMsg(ApiResultType.NOT_ALLOW_BIND_WECHAT);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "手机注册用户解绑微信wechat")
    @RequestMapping(value = "/unbindWeChat/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean unbindWeChat(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        try {
            String r_redis = (String) request.getAttribute("code");
            //解绑用户--->将wechat_auth置为null
            String r_user = getUserCache(r_redis);
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(r_user, UserLoginRestEntity.class);
            //判断是否为手机用户
            if (StringUtils.isEmpty(userLoginRestEntity.getMobile())) {
                rb.setFailMsg(ApiResultType.NOT_ALLOW_UNBIND_WECHAT);
                return rb;
            }
            //置空 wechat_auth
            int i = userInfoRestServiceI.updateWeChat(userLoginRestEntity.getMobile(), null);
            if (i < 1) {
                rb.setFailMsg(ApiResultType.WECHAT_UNBIND_ERROR);
                return rb;
            }
            //设置redis缓存 缓存用户信息
            userLoginRestEntity.setWechatAuth(null);
            String userToString = JSON.toJSONString(userLoginRestEntity);
            updateCache(userToString, r_redis);
            rb.setSucResult(ApiResultType.OK);
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "获取用户详情")
    @RequestMapping(value = "/userInfo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean userinfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserInfoRestEntity task = userInfoRestServiceI.findUniqueByProperty(UserInfoRestEntity.class, "id", Integer.valueOf(userInfoId));
            if(task!=null){
                rb.setSucResult(ApiResultType.OK);
                rb.setResult(task);
                return rb;
            }else{
                rb.setFailMsg(ApiResultType.USER_NOT_EXIST);
                return rb;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    //从cache获取用户信息通用方法
    private String getUserCache(String code) {
        String user = (String) redisTemplate.opsForValue().get(code);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user)) {
            UserLoginRestEntity task;
            //判断code类型
            if (ValidateUtils.isMobile(code)) {
                task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "mobile", code);
            } else {
                task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "wechat_auth", code);
            }
            //设置redis缓存 缓存用户信息 30天 毫秒
            String r_user = JSON.toJSONString(task);
            updateCache(r_user, code);
            return r_user;
        }
        return user;
    }

    //更新redis缓存通用方法
    private void updateCache(String user, String code) {
        redisTemplate.opsForValue().set(code, user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    /**
     * 获取七牛云上传鉴权 1为头像   2为反馈
     * @param type
     * @return
     */
    @RequestMapping(value = "/getQiNiuAuth/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean getQiNiuAuth(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,@RequestBody Map<String,String> map) {
        ResultBean rb = new ResultBean();
        try {
            QiNiuUploadFileUtils qiniu = new QiNiuUploadFileUtils();
            String upToken = null;
            if(StringUtils.isEmpty(map.get("flag"))){
                //为空默认类型1 head-picture头像上传
                upToken = qiniu.getUpToken("head-picture");
            }else{
                if(StringUtils.equals(map.get("flag"),"1")){
                    upToken = qiniu.getUpToken("head-picture");
                }else if(StringUtils.equals(map.get("flag"),"2")){
                    //2为信息反馈图片
                    upToken = qiniu.getUpToken("feedback-picture");
                }else{
                    upToken = qiniu.getUpToken("head-picture");
                }
            }
            rb.setSucResult(ApiResultType.OK);
            Map<String,String> map2 = new HashMap<>();
            map2.put("auth",upToken);
            rb.setResult(map2);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @RequestMapping(value = "/updateUserInfo/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateUserInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,@RequestBody UserInfoRestEntity userInfoRestEntity,HttpServletRequest request) {
        ResultBean rb = new ResultBean();
        if(userInfoRestEntity==null){
            rb.setFailMsg(ApiResultType.MY_PARAMS_ERROR);
            return rb;
        }
        try {
            String  userInfoId = (String) request.getAttribute("userInfoId");
            userInfoRestEntity.setId(Integer.valueOf(userInfoId));
            userInfoRestServiceI.updateUserInfo(userInfoRestEntity);
            rb.setSucResult(ApiResultType.OK);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @RequestMapping(value = "/bindMobile", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean bindMobile(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return this.bindMobile(null, map, request);
    }

    @RequestMapping(value = "/bindWeChat", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean bindWeChat(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return this.bindWeChat(null, map, request);
    }

    @RequestMapping(value = "/unbindWeChat", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean unbindWeChat(HttpServletRequest request) {
        return this.unbindWeChat(null, request);
    }

    @RequestMapping(value = "/getQiNiuAuth", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean getQiNiuAuth(@RequestBody Map<String,String> map) {
        return this.getQiNiuAuth(null, map);
    }
}
