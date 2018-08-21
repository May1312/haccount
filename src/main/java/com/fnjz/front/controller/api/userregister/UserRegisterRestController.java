package com.fnjz.front.controller.api.userregister;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.*;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 用户手机号 验证码注册接口
 * Created by yhang on 2018/6/1.
 */

@Controller
@RequestMapping(RedisPrefix.BASE_URL)
@Api(description = "android/ios",tags = "用户注册接口")
public class UserRegisterRestController extends BaseController {

    private static final Logger logger = Logger.getLogger(UserRegisterRestController.class);

    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    @Autowired
    private UserInfoRestServiceI userInfoRestService;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

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
        ResultBean rb = ParamValidateUtils.checkResetpwd(map);
        //手机号或验证码或密码为空
        if(rb!=null){
            return rb;
        }
        //校验验证码
        String code = (String) redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_REGISTER+map.get("mobile"));
        //验证码已失效
        if(StringUtils.isEmpty(code)){
            return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT,null);
        }
        try {
            if(StringUtil.equals(code,map.get("verifycode"))){
                //验证码校验通过
                UserInfoRestEntity userInfo = new UserInfoRestEntity();
                userInfo = ParamValidateUtils.checkRegisterParams(userInfo,map,type);
                //执行新增
                int insertId = userInfoRestService.insert(userInfo);
                if(insertId>0){
                    //缓存用户信息  TODO 有必要重新查库？？
                    UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
                    rb = createTokenUtils.loginSuccess(task, ShareCodeUtil.id2sharecode(task.getUserInfoId()));
                    return rb;
                }else{
                    return new ResultBean(ApiResultType.REGISTER_IS_ERROR,null);
                }
            }else{
                //验证错误
                return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR,null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR,null);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(@RequestBody Map<String, String> map) {
        return  this.register(null,map);
    }
}
