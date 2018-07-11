package com.fnjz.front.controller.api.verifycode;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.enums.LoginEnum;
import com.fnjz.front.enums.VerifyCodeEnum;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.ValidateUtils;
import com.fnjz.utils.CreateVerifyCodeUtils;
import com.fnjz.utils.sms.DySms;
import com.fnjz.utils.sms.TemplateCode;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 发送短信相关接口
 * Created by yhang on 2018/5/31.
 */
@Api(description = "android/ios", tags = "验证码相关接口")
@Controller
@RequestMapping("/api/v1")
public class VerifyCodeRestController {

    private static final Logger logger = Logger.getLogger(VerifyCodeRestController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    /**
     * 登录验证码获取接口
     */
    @ApiOperation(value = "登录验证码获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String")
    })
    @RequestMapping(value = "/verifycodeToLogin/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToLogin(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        return this.sendVerifyCode(map,VerifyCodeEnum.VERIFYCODE_LOGIN);
    }

    /**
     * 注册验证码获取接口
     */
    @ApiOperation(value = "注册验证码获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String")
    })
    @RequestMapping(value = "/verifycodeToRegister/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToRegister(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        return this.sendVerifyCode(map,VerifyCodeEnum.VERIFYCODE_REGISTER);
    }

    /**
     * 找回密码验证码获取接口
     */
    @ApiOperation(value = "找回密码验证码获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String")
    })
    @RequestMapping(value = "/verifycodeToResetpwd/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToResetpwd(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        return this.sendVerifyCode(map,VerifyCodeEnum.VERIFYCODE_RESETPWD);
    }

    /**
     * 绑定手机号获取验证码接口
     */
    @ApiOperation(value = "绑定手机号验证码获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String")
    })
    @RequestMapping(value = "/verifycodeToBind/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToBind(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        return this.sendVerifyCode(map,VerifyCodeEnum.VERIFYCODE_BIND_MOBILE);
    }

    /**
     * 修改手机号--->旧手机号--->获取验证码接口
     */
    @ApiOperation(value = "更换手机号验证码获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String")
    })
    @RequestMapping(value = "/verifycodeToChange/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToChange(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        return this.sendVerifyCode(map,VerifyCodeEnum.VERIFYCODE_OLD_MOBILE);
    }

    @ApiOperation(value = "更换手机号---->校验原手机号验证码接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "verifycode", value = "验证码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/checkOldMobile/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkOldMobile(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {

        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("verifycode"))) {
            rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
            return rb;
        }
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            rb.setFailMsg(ApiResultType.MOBILE_FORMAT_ERROR);
            return rb;
        }
        //校验验证码
        try {
            String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_CHANGE_MOBILE + map.get("mobile"));
            if (StringUtils.isEmpty(code)) {
                rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
                return rb;
            }
            if (StringUtils.equals(code, map.get("verifycode"))) {
                rb.setSucResult(ApiResultType.OK);
            } else {
                rb.setFailMsg(ApiResultType.VERIFYCODE_IS_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    /**
     * 公共方法抽取
     *
     * @param verifycode
     * @return
     */
    private ResultBean sendVerifyCode(Map<String, String> map, VerifyCodeEnum verifycode) {
        try {
            if (ParamValidateUtils.checkeMobile(map) != null) {
                return ParamValidateUtils.checkeMobile(map);
            }
            //生成六位随机验证码
            String random = CreateVerifyCodeUtils.createRandom(6);
            //登录验证码
            if (verifycode.getIndex() == 1) {
                //验证手机号是否存在
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                if (task == null) {
                    return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.LOGIN_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis,验证码有效期3分钟    定义验证码
                    redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_VERIFYCODE_LOGIN + map.get("mobile"), random, RedisPrefix.VERIFYCODE_VALID_TIME, TimeUnit.MINUTES);
                    logger.info("生成登录验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            }else if(verifycode.getIndex() == 2){
                //验证手机号是否存在
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                if (task != null) {
                    return new ResultBean(ApiResultType.MOBILE_IS_EXISTED, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.REGISTER_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis,验证码有效期3分钟    定义验证码
                    redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_VERIFYCODE_REGISTER + map.get("mobile"), random, RedisPrefix.VERIFYCODE_VALID_TIME, TimeUnit.MINUTES);
                    logger.info("生成登录验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            }else if(verifycode.getIndex() == 3){
                //验证手机号是否存在
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                if (task == null) {
                    return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.RESETPWD_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis,验证码有效期3分钟    定义验证码
                    redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_VERIFYCODE_RESETPWD + map.get("mobile"), random, RedisPrefix.VERIFYCODE_VALID_TIME, TimeUnit.MINUTES);
                    logger.info("生成登录验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            } else if(verifycode.getIndex() == 4){
                //验证手机号是否存在
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                if (task != null) {
                    return new ResultBean(ApiResultType.MOBILE_IS_EXISTED, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.BIND_MOBILE_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis,验证码有效期3分钟    定义验证码
                    redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"), random, RedisPrefix.VERIFYCODE_VALID_TIME, TimeUnit.MINUTES);
                    logger.info("生成登录验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            }else if(verifycode.getIndex() == 5){
                //验证手机号是否存在
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                if (task == null) {
                    return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.BIND_MOBILE_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis,验证码有效期3分钟    定义验证码
                    redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_VERIFYCODE_CHANGE_MOBILE + map.get("mobile"), random, RedisPrefix.VERIFYCODE_VALID_TIME, TimeUnit.MINUTES);
                    logger.info("生成登录验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
        return null;
    }


    @RequestMapping(value = "/verifycodeToLogin", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToLogin(@RequestBody Map<String, String> map) {
        return this.verifycodeToLogin(null, map);
    }

    @RequestMapping(value = "/verifycodeToRegister", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToRegister(@RequestBody Map<String, String> map) {
        return this.verifycodeToRegister(null, map);
    }

    @RequestMapping(value = "/verifycodeToResetpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToResetpwd(@RequestBody Map<String, String> map) {
        return this.verifycodeToResetpwd(null, map);
    }

    @RequestMapping(value = "/verifycodeToBind", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToBind(@RequestBody Map<String, String> map) {
        return this.verifycodeToBind(null, map);
    }

    @RequestMapping(value = "/verifycodeToChange", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToChange(@RequestBody Map<String, String> map) {
        return this.verifycodeToChange(null, map);
    }

    @RequestMapping(value = "/checkOldMobile", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkOldMobile(@RequestBody Map<String, String> map) {
        return this.checkOldMobile(null, map);
    }
}
