package com.fnjz.front.controller.api.common;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.PasswordUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.utils.CreateVerifyCodeUtils;
import com.fnjz.utils.sms.DySms;
import com.fnjz.utils.sms.TemplateCode;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 超管权限  创建测试用户
 * Created by yhang on 2018/7/24.
 * https://api.fengniaojizhang.com/api/admin?profix=zhijie781&pushnumber=7&robotnumber=10&verifycode=66666
 */

@Controller
@RequestMapping("/api/admin")
public class AdminManageRobot {

    private static final Logger logger = Logger.getLogger(AdminManageRobot.class);

    /**
     * 管理员手机号
     */
    private static final String admin = "13552570975";
    /**
     * 测试用户密码
     */
    private static final String password = "123456";

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;
    @Autowired
    private UserInfoRestServiceI userInfoRestService;

    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean sendVerifyCode(@RequestParam(required = false) String mobile) {
        if (StringUtils.isNotEmpty(mobile) && StringUtils.equals(mobile, admin)) {
            //生成六位随机验证码
            String random = CreateVerifyCodeUtils.createRandom(6);
            SendSmsResponse sendSmsResponse = DySms.sendSms(mobile, TemplateCode.ADMIN_CERTAIN.getTemplateCode(), "{\"code\":\"" + random + "\"}");
            if (StringUtils.equals(sendSmsResponse.getCode(), "OK")) {
                //验证码存放redis
                redisTemplateUtils.cacheVerifyCode(RedisPrefix.ADMIN_CERTAIN + mobile, random);
                return new ResultBean(ApiResultType.OK, null);
            } else if (StringUtils.equals(sendSmsResponse.getCode(), "isv.BUSINESS_LIMIT_CONTROL")) {
                return new ResultBean(ApiResultType.VERIFYCODE_LIMIT, null);
            } else {
                logger.error(JSON.toJSONString(sendSmsResponse));
                return new ResultBean(ApiResultType.SEND_VERIFYCODE_ERROR, null);
            }
        } else {
            return new ResultBean(ApiResultType.MOBILE_IS_VAILD, null);
        }
    }

    /**
     *
     * @param profix  前缀
     * @param pushnumber  追加位数
     * @param robotnumber  生成数量
     * @param verifycode  验证码
     * @param mobile  管理员手机号
     * @return
     */
    @RequestMapping(value = "/createRobot", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean createRobot(@RequestParam(required = false) String profix, @RequestParam(required = false) String pushnumber, @RequestParam(required = false) String robotnumber, @RequestParam(required = false) String verifycode, @RequestParam(required = false) String mobile) {
        if (StringUtils.isNotEmpty(profix) && StringUtils.isNotEmpty(pushnumber) && StringUtils.isNotEmpty(robotnumber) && StringUtils.isNotEmpty(verifycode) && StringUtils.isNotEmpty(mobile)) {
            //校验长度
            //if (profix.length() + Integer.valueOf(pushnumber) >= 13) {
                String code = redisTemplateUtils.getVerifyCode(RedisPrefix.ADMIN_CERTAIN + mobile);
                List<UserInfoRestEntity> list = new ArrayList<>(Integer.valueOf(robotnumber));
                if (StringUtils.equals(code, verifycode)) {
                    String pwd = PasswordUtils.getEncryptpwd(password);
                    for (int i = 0; i < Integer.valueOf(robotnumber); i++) {
                        String random = CreateVerifyCodeUtils.createRandom(Integer.valueOf(pushnumber));
                        //执行新增
                        UserInfoRestEntity userInfo = new UserInfoRestEntity();
                        userInfo.setMobile(profix + random);
                        userInfo.setPassword(pwd);
                        userInfo.setUserType("9");
                        //定义用户类型为9的为测试用户
                        int j = userInfoRestService.insert(userInfo);
                        if (j < 0) {
                            return new ResultBean(ApiResultType.REGISTER_IS_ERROR, null);
                        }
                        list.add(userInfo);
                    }
                    return new ResultBean(ApiResultType.OK, list);
                } else {
                    return new ResultBean(ApiResultType.ROBOT_IS_VAILD, null);
                }

            /*} else {
                return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
            }*/
        } else {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
    }
}
