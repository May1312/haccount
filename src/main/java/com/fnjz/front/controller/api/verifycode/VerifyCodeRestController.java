package com.fnjz.front.controller.api.verifycode;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.utils.CreateVerifyCodeUtils;
import com.fnjz.utils.ResdisRestUtils;
import com.fnjz.utils.sms.DySms;
import com.fnjz.utils.sms.TemplateCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 发送短信相关接口
 * Created by yhang on 2018/5/31.
 */
@Api(value = "appregister", description = "移动端----->发送验证码接口", tags = "appverifycode")
@Controller
@RequestMapping("/api/v1")
public class VerifyCodeRestController {

    /**
     * 登录验证码获取接口
     */
    @ApiOperation(value = "登录验证码获取")
    @RequestMapping(value = "/verifycodeToLogin/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToLogin(@PathVariable("type") String type, @RequestBody Map<String, String> map) {
        ResultBean rb = new ResultBean();
        if(StringUtil.isEmpty(map.get("mobile"))){
            rb.setFailMsg(ApiResultType.MOBILE_IS_NULL);
            return rb;
        }
        //生成六位随机验证码
        String random = CreateVerifyCodeUtils.createRandom(6);
        SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.LOGIN_CODE.getTemplateCode(), "{\"code\":\""+random+"\"}");
        //防止已经缓存验证码，先执行删除
        ResdisRestUtils.del(ResdisRestUtils.PROFIX_USER_VERIFYCODE_LOGIN+map.get("mobile"),null);
        //验证码存放redis,验证码有效期3分钟
        ResdisRestUtils.set(ResdisRestUtils.PROFIX_USER_VERIFYCODE_LOGIN+map.get("mobile"),random,60*3,null);
        if(StringUtil.equals(sendSmsResponse.getCode(),"OK")){
                rb.setSucResult(ApiResultType.OK);
        }else{
            rb.setFailMsg(ApiResultType.SEND_VERIFYCODE_ERROR);
        }
        return rb;
    }

    /**
     * 注册验证码获取接口
     */
    @ApiOperation(value = "注册验证码获取")
    @RequestMapping(value = "/verifycodeToRegister/{type}" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToRegister(@PathVariable("type") String type, @RequestBody Map<String, String> map) {
        ResultBean rb = new ResultBean();
        if(StringUtil.isEmpty(map.get("mobile"))){
            rb.setFailMsg(ApiResultType.MOBILE_IS_NULL);
            return rb;
        }
        //生成六位随机验证码
        String random = CreateVerifyCodeUtils.createRandom(6);
        SendSmsResponse sendSmsResponse = DySms.sendSms(map.get("mobile"), TemplateCode.LOGIN_CODE.getTemplateCode(), "{\"code\":\""+random+"\"}");
        //防止已经缓存验证码，先执行删除
        ResdisRestUtils.del(ResdisRestUtils.PROFIX_USER_VERIFYCODE_REGISTER+map.get("mobile"),null);
        //验证码存放redis,验证码有效期3分钟
        ResdisRestUtils.set(ResdisRestUtils.PROFIX_USER_VERIFYCODE_LOGIN+map.get("mobile"),random,60*10,null);
        if(StringUtil.equals(sendSmsResponse.getCode(),"OK")){
            rb.setSucResult(ApiResultType.OK);
        }else{
            rb.setFailMsg(ApiResultType.SEND_VERIFYCODE_ERROR);
        }
        return rb;
    }

    @RequestMapping(value = "/verifycodeToLogin" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToLogin(@RequestBody Map<String, String> map) {
        return this.verifycodeToLogin(null,map);
    }

    @RequestMapping(value = "/verifycodeToRegister" , method = RequestMethod.POST)
    @ResponseBody
    public ResultBean verifycodeToRegister(@RequestBody Map<String, String> map) {
        return this.verifycodeToRegister(null,map);
    }
}
