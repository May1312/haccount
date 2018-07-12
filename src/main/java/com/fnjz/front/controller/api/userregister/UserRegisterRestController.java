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
@RequestMapping("/api/v1")
@Api(description = "android/ios",tags = "用户注册接口")
public class UserRegisterRestController extends BaseController {

    private static final Logger logger = Logger.getLogger(UserRegisterRestController.class);

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
        if(ParamValidateUtils.checkResetpwd(map)!=null){
            return ParamValidateUtils.checkResetpwd(map);
        }
        //验证手机号是否已存在
        UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
        if (task != null) {
            rb.setFailMsg(ApiResultType.MOBILE_IS_EXISTED);
            return rb;
        }
        //校验验证码
        String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_REGISTER+map.get("mobile"));
        //验证码已失效
        if(code==null){
            rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
            return rb;
        }
        try {
            if(StringUtil.equals(code,map.get("verifycode"))){
                //验证码校验通过
                //先生成用户详情表获取id--->存入用户登录表
                UserInfoRestEntity userInfo = new UserInfoRestEntity();
                userInfo = ParamValidateUtils.checkRegisterParams(userInfo,map);
                //执行新增
                int insertId = userInfoRestService.insert(userInfo);
                if(insertId>0){
                    //缓存用户信息  TODO 有必要重新查库？？
                    task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
                    rb = createTokenUtils.loginSuccess(task, ShareCodeUtil.id2sharecode(task.getUserInfoId()));
                    return rb;
                }else{
                    rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
                }
            }else{
                //验证错误
                rb.setFailMsg(ApiResultType.VERIFYCODE_IS_ERROR);
                return rb;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean register(@RequestBody Map<String, String> map) {
        return  this.register(null,map);
    }
}
