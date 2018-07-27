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
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.utils.CreateVerifyCodeUtils;
import com.fnjz.utils.sms.DySms;
import com.fnjz.utils.sms.TemplateCode;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import java.util.Map;

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
    private RedisTemplateUtils redisTemplateUtils;

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
        return this.sendVerifyCode(map, VerifyCodeEnum.VERIFYCODE_LOGIN);
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
        return this.sendVerifyCode(map, VerifyCodeEnum.VERIFYCODE_REGISTER);
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
        return this.sendVerifyCode(map, VerifyCodeEnum.VERIFYCODE_RESETPWD);
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
        return this.sendVerifyCode(map, VerifyCodeEnum.VERIFYCODE_BIND_MOBILE);
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
        return this.sendVerifyCode(map, VerifyCodeEnum.VERIFYCODE_OLD_MOBILE);
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
        ResultBean rb = ParamValidateUtils.checkLogin(map, LoginEnum.LOGIN_BY_VERIFYCODE);
        if (rb != null) {
            return rb;
        }
        //校验验证码
        try {
            String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_CHANGE_MOBILE + map.get("mobile"));
            rb = ParamValidateUtils.checkVerifycode(map, code);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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
            //验证手机号是否存在
            UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
            //登录验证码
            if (verifycode.getIndex() == 1) {
                if (task == null) {
                    return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.LOGIN_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis
                    redisTemplateUtils.cacheVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_LOGIN + map.get("mobile"),random);
                    logger.info("生成登录验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            } else if (verifycode.getIndex() == 2) {
                if (task != null) {
                    return new ResultBean(ApiResultType.MOBILE_IS_EXISTED, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.REGISTER_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis
                    redisTemplateUtils.cacheVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_REGISTER + map.get("mobile"),random);
                    logger.info("生成注册验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            } else if (verifycode.getIndex() == 3) {
                if (task == null) {
                    return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.RESETPWD_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis
                    redisTemplateUtils.cacheVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_RESETPWD + map.get("mobile"),random);
                    logger.info("生成重置密码验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            } else if (verifycode.getIndex() == 4) {
                if (task != null) {
                    return new ResultBean(ApiResultType.MOBILE_IS_EXISTED, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.BIND_MOBILE_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis
                    redisTemplateUtils.cacheVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"),random);
                    logger.info("生成绑定手机号验证码:" + random);
                    return new ResultBean(ApiResultType.OK, null);
                } else if (StringUtil.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                    return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
                } else {
                    logger.error(JSON.toJSONString(sendSmsResponse));
                    return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
                }
            } else if (verifycode.getIndex() == 5) {
                if (task == null) {
                    return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
                }
                SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.BIND_MOBILE_CODE.getTemplateCode(), "{\"code\":\"" + random + "\"}");
                if (StringUtil.equals(sendSmsResponse.getCode(), "OK")) {
                    //验证码存放redis
                    redisTemplateUtils.cacheVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_CHANGE_MOBILE + map.get("mobile"),random);
                    logger.info("生成修改手机号验证码:" + random);
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
