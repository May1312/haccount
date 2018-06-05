package com.fnjz.front.controller.api.userlogin;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import com.fnjz.front.utils.MD5Utils;
import com.fnjz.front.utils.WXAppletUtils;
import com.fnjz.front.utils.WeChatUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;

import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @version V1.0
 * @Title: Controller
 * @Description: 用户登录表相关
 * @date 2018-05-30 22:41:49
 */
@Controller
@RequestMapping("/api/v1")
@Api(value = "applogin", description = "移动端----->登录接口", tags = "applogin")
public class UserLoginRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserLoginRestController.class);

    @Autowired
    private UserLoginRestServiceI userLoginRestService;
    @Autowired
    private UserInfoRestServiceI userInfoRestServiceI;
    @Autowired
    private CreateTokenUtils createTokenUtils;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户登录表相关列表 登陆
     * type标识访问的终端类型  ios/android/wx
     * 手机号密码登录
     *
     * @return
     */
    @ApiOperation(value = "账号密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码 md5加密", required = true, dataType = "String")
    })
    @RequestMapping(value = "/login/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean login(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //用户名或密码错误
        if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("password"))) {
            rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ISNULL);
            return rb;
        }
        //验证用户名密码
        UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
        if (task == null) {
            rb.setFailMsg(ApiResultType.USER_NOT_EXIST);
        } else {
            //判断密码
            if (StringUtil.equals(task.getPassword(), map.get("password"))) {
                rb.setSucResult(ApiResultType.OK);
                //返回token  expire
                Map<String, Object> map2 = new HashMap<>();
                String token = createTokenUtils.createToken(map.get("mobile"));
                System.out.println("生成的token：" + token);
                map2.put("X-AUTH-TOKEN", token);
                map2.put("expire", 30 * 24 * 60 * 60 * 1000);
                //设置redis缓存 缓存用户信息 30天 毫秒
                String user = JSON.toJSONString(task);
                //先判断是否存在
                if (StringUtils.isNotEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(map.get("mobile"))))) {
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(map.get("mobile")));
                }
                redisTemplate.opsForValue().set(MD5Utils.getMD5(map.get("mobile")), user, 30, TimeUnit.DAYS);
                rb.setResult(map2);
            } else {
                rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ERROR);
            }
        }
        return rb;
    }

    /**
     * 短信验证码登录
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "短信验证码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "verifycode", value = "验证码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/loginByCode/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean loginByCode(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //用户名或验证码错误
        if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("verifycode"))) {
            rb.setFailMsg(ApiResultType.USERNAME_OR_VERIFYCODE_ISNULL);
            return rb;
        }
        ///获取验证码
        String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_LOGIN + map.get("mobile"));
        if (StringUtil.isEmpty(code)) {
            //验证码为空
            rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
        } else {
            if (StringUtil.equals(code, map.get("verifycode"))) {
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                rb.setSucResult(ApiResultType.OK);
                //返回token  expire
                String token = createTokenUtils.createToken(map.get("mobile"));
                System.out.println("生成的token：" + token);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("X-AUTH-TOKEN", token);
                map2.put("expire", 30 * 24 * 60 * 60 * 1000);
                //设置redis缓存 缓存用户信息 30天 毫秒
                String user = JSON.toJSONString(task);
                //先判断是否存在
                if (StringUtil.isNotEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(map.get("mobile"))))) {
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(map.get("mobile")));
                }
                redisTemplate.opsForValue().set(MD5Utils.getMD5(map.get("mobile")), user, 30, TimeUnit.DAYS);
                rb.setResult(map2);
            }
        }
        return rb;
    }

    /**
     * app微信授权登录
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "app微信授权登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "state", value = "state域", required = true, dataType = "String")
    })
    @RequestMapping(value = "/loginByWeChat/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean loginByWeChat(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestParam("code") String code, @RequestParam("state") String state) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //请求参数错误
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state)) {
            rb.setFailMsg(ApiResultType.WECHAT_PARAM_ERROR);
            return rb;
        }
        JSONObject user = WeChatUtils.getUser(code);
        if (user == null) {
            rb.setFailMsg(ApiResultType.WECHAT_LOGIN_ERROR);
            return rb;
        }
        //查看openid是否存在
        UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionid"));
        String userToString = JSON.toJSONString(task);
        if (task == null) {
            //注册
            int insert = userInfoRestServiceI.wechatinsert(user);
            if (insert > 0) {
                rb.setSucResult(ApiResultType.OK);
                //根据openid生成token  expire
                String token = createTokenUtils.createToken(user.getString("unionid"));
                System.out.println("生成的token：" + token);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("X-AUTH-TOKEN", token);
                map2.put("expire", 30 * 24 * 60 * 60 * 1000);
                //设置redis缓存 缓存用户信息 30天 毫秒
                UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionid"));
                String userToString2 = JSON.toJSONString(task2);
                //先判断是否存在
                if (StringUtil.isEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(user.getString("unionid"))))) {
                    redisTemplate.opsForValue().set(MD5Utils.getMD5(user.getString("unionid")), userToString2, 30, TimeUnit.DAYS);
                } else {
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(user.getString("unionid")));
                    redisTemplate.opsForValue().set(MD5Utils.getMD5(user.getString("unionid")), userToString2, 30, TimeUnit.DAYS);
                }
                rb.setResult(map2);
            } else {
                rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
            }
        } else {
            //登录流程
            rb.setSucResult(ApiResultType.OK);
            String token;
            if (StringUtils.isNotEmpty(task.getMobile())) {
                token = createTokenUtils.createToken(task.getMobile());
                //先判断是否存在
                if (StringUtil.isEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(task.getMobile())))) {
                    redisTemplate.opsForValue().set(MD5Utils.getMD5(task.getMobile()), userToString, 30, TimeUnit.DAYS);
                } else {
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(task.getMobile()));
                    redisTemplate.opsForValue().set(MD5Utils.getMD5(task.getMobile()), userToString, 30, TimeUnit.DAYS);
                }
            } else {
                token = createTokenUtils.createToken(user.getString("unionid"));
                //先判断是否存在
                if (StringUtil.isEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(user.getString("unionid"))))) {
                    redisTemplate.opsForValue().set(MD5Utils.getMD5(user.getString("unionid")), userToString, 30, TimeUnit.DAYS);
                }else{
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(user.getString("unionid")));
                    redisTemplate.opsForValue().set(MD5Utils.getMD5(user.getString("unionid")), userToString, 30, TimeUnit.DAYS);
                }
            }
            System.out.println("生成的token：" + token);
            Map<String, Object> map2 = new HashMap<>();
            map2.put("X-AUTH-TOKEN", token);
            map2.put("expire", 30 * 24 * 60 * 60 * 1000);
            rb.setResult(map2);
        }
        return rb;
    }

    /**
     * 微信小程序登录
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "微信小程序登录+注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/loginByWXApplet/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean loginByWXApplet(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //code为空
        if (StringUtil.isEmpty(map.get("code"))) {
            rb.setFailMsg(ApiResultType.WXAPPLET_CODE_ISNULL);
            return rb;
        }
        String user = WXAppletUtils.getUser(map.get("code"));
        JSONObject jsonObject = JSONObject.parseObject(user);
        if (jsonObject.getString("errcode") != null) {
            rb.setFailMsg(ApiResultType.WXAPPLET_LOGIN_ERROR);
        } else {
            String openid = jsonObject.getString("openid");
            //查看openid是否存在
            UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", openid);
            String userToString = JSON.toJSONString(task);
            if (task == null) {
                //执行注册流程  TODO 用户手机号在这一步可以获取到么？  需要移动端传递解密数据获取unionid
                UserInfoRestEntity uire = new UserInfoRestEntity();
                uire.setWechatAuth(openid);
                int insert = userInfoRestServiceI.insert(uire);
                if (insert > 0) {
                    rb.setSucResult(ApiResultType.OK);
                    //根据openid生成token  expire
                    String token = createTokenUtils.createToken(openid);
                    System.out.println("生成的token：" + token);
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("X-AUTH-TOKEN", token);
                    map2.put("expire", 30 * 24 * 60 * 60 * 1000);
                    //设置redis缓存 缓存用户信息 30天 毫秒
                    UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", openid);
                    String userToString2 = JSON.toJSONString(task2);
                    //先判断是否存在
                    if (StringUtil.isEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(openid)))) {
                        redisTemplate.opsForValue().set(MD5Utils.getMD5(openid), userToString2, 30, TimeUnit.DAYS);
                    } else {
                        //执行删除
                        redisTemplate.delete(MD5Utils.getMD5(openid));
                        redisTemplate.opsForValue().set(MD5Utils.getMD5(openid), userToString2, 30, TimeUnit.DAYS);
                    }
                    rb.setResult(map2);
                } else {
                    rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
                }
            } else {
                //登录流程
                //{"session_key":"i2VyPTkFlFNh8bThTGXShg==","openid":"ojYTl5RhdfPo9hKspMa8sfJ3Fvno"}
                rb.setSucResult(ApiResultType.OK);
                //根据openid生成token  expire
                //手机号不为空情况下
                String token;
                if (StringUtils.isNotEmpty(task.getMobile())) {
                    token = createTokenUtils.createToken(task.getMobile());
                    //先判断是否存在
                    if (StringUtil.isEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(task.getMobile())))) {
                        redisTemplate.opsForValue().set(MD5Utils.getMD5(task.getMobile()), userToString, 30, TimeUnit.DAYS);
                    } else {
                        //执行删除
                        redisTemplate.delete(MD5Utils.getMD5(task.getMobile()));
                        redisTemplate.opsForValue().set(MD5Utils.getMD5(task.getMobile()), userToString, 30, TimeUnit.DAYS);
                    }
                } else {
                    token = createTokenUtils.createToken(openid);
                    //先判断是否存在
                    if (StringUtil.isEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(openid)))) {
                        redisTemplate.opsForValue().set(MD5Utils.getMD5(openid), userToString, 30, TimeUnit.DAYS);
                    }else{
                        //执行删除
                        redisTemplate.delete(MD5Utils.getMD5(openid));
                        redisTemplate.opsForValue().set(MD5Utils.getMD5(openid), userToString, 30, TimeUnit.DAYS);
                    }
                }
                System.out.println("生成的token：" + token);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("X-AUTH-TOKEN", token);
                map2.put("expire", 30 * 24 * 60 * 60 * 1000);
                rb.setResult(map2);
            }
        }
        return rb;
    }

    /**
     * 找回密码功能
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "找回密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "verifycode", value = "验证码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/resetpwd/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean resetpwd(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //用户名或验证码或密码错误
        if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("password")) || StringUtil.isEmpty(map.get("verifycode"))) {
            rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
            return rb;
        }
        ///获取验证码
        String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_RESETPWD + map.get("mobile"));
        if (StringUtil.isEmpty(code)) {
            //验证码为空
            rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
        } else {
            if (StringUtil.equals(code, map.get("verifycode"))) {
                //执行更新密码流程
                int i = userInfoRestServiceI.updatePWD(map.get("mobile"), map.get("password"));
                if (i < 1) {
                    rb.setFailMsg(ApiResultType.PASSWORD_UPDATE_ERROR);
                    return rb;
                }
                //返回token  expire
                String token = createTokenUtils.createToken(map.get("mobile"));
                System.out.println("生成的token：" + token);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("X-AUTH-TOKEN", token);
                map2.put("expire", 30 * 24 * 60 * 60 * 1000);
                //设置redis缓存 缓存用户信息 30天 毫秒
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                String user = JSON.toJSONString(task);
                //先判断是否存在
                if (StringUtil.isNotEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(map.get("mobile"))))) {
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(map.get("mobile")));
                }
                redisTemplate.opsForValue().set(MD5Utils.getMD5(map.get("mobile")), user, 30, TimeUnit.DAYS);
                rb.setSucResult(ApiResultType.OK);
                rb.setResult(map2);
            }
        }
        return rb;
    }

    /**
     * 修改密码功能
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldpwd", value = "旧密码", dataType = "String"),
            @ApiImplicitParam(name = "newpwd", value = "新密码 MD5加密", dataType = "String")
    })
    @RequestMapping(value = "/updatepwd/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updatepwd(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //验证密码
        if (StringUtil.isEmpty(map.get("oldpwd")) || StringUtil.isEmpty(map.get("newpwd"))) {
            rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
            return rb;
        }
        String code = (String) request.getAttribute("code");
        String r_user = (String) redisTemplate.opsForValue().get(MD5Utils.getMD5(code));
        //转成对象
        UserLoginRestEntity userLoginRestEntity = JSON.parseObject(r_user, UserLoginRestEntity.class);
        if (StringUtils.equals(userLoginRestEntity.getPassword(), map.get("oldpwd"))) {
            //执行更新密码流程
            int i = userInfoRestServiceI.updatePWD(code, map.get("newpwd"));
            if (i < 1) {
                rb.setFailMsg(ApiResultType.PASSWORD_UPDATE_ERROR);
                return rb;
            }
            //返回token  expire
            String token = createTokenUtils.createToken(code);
            System.out.println("生成的token：" + token);
            Map<String, Object> map2 = new HashMap<>();
            map2.put("X-AUTH-TOKEN", token);
            map2.put("expire", 30 * 24 * 60 * 60 * 1000);
            //设置redis缓存 缓存用户信息 30天 毫秒
            userLoginRestEntity.setPassword(map.get("newpwd"));
            String user = JSON.toJSONString(userLoginRestEntity);
            //先判断是否存在
            if (StringUtil.isNotEmpty((String) redisTemplate.opsForValue().get(MD5Utils.getMD5(code)))) {
                //执行删除
                redisTemplate.delete(MD5Utils.getMD5(code));
            }
            redisTemplate.opsForValue().set(MD5Utils.getMD5(code), user, 30, TimeUnit.DAYS);
            rb.setSucResult(ApiResultType.OK);
            rb.setResult(map2);
        } else {
            rb.setFailMsg(ApiResultType.PASSWORD_ERROR);
        }
        return rb;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean login(@RequestBody Map<String, String> map) {
        return this.login(null, map);
    }

    @RequestMapping(value = "/loginByCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean loginByCode(@RequestBody Map<String, String> map) {
        return this.loginByCode(null, map);
    }

    @RequestMapping(value = "/loginByWeChat", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean loginByWeChat(@RequestParam("code") String code, @RequestParam("state") String state) {
        return this.loginByWeChat(null, code, state);
    }

    @RequestMapping(value = "/loginByWXApplet", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean loginByWXApplet(@RequestBody Map<String, String> map) {
        return this.loginByWXApplet(null, map);
    }

    @RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean resetpwd(@RequestBody Map<String, String> map) {
        return this.resetpwd(null, map);
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public Map login() {
        return null;
    }
}
