package com.fnjz.front.controller.api.userlogin;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.utils.*;
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
@Api(description = "android/ios", tags = "用户登录接口")
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
    @Autowired
    private UserAccountBookRestServiceI userAccountBookRestServiceI;

    /**
     * 用户登录表相关列表 登陆
     * type标识访问的终端类型  ios/android/wx
     * 手机号密码登录
     *
     * @return
     */
    //@SystemLog
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
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            rb.setFailMsg(ApiResultType.MOBILE_FORMAT_ERROR);
            return rb;
        }
        try {
            //验证用户名密码
            UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
            if (task == null) {
                rb.setFailMsg(ApiResultType.USER_NOT_EXIST);
            } else {
                //判断密码
                if (StringUtil.equals(task.getPassword(), map.get("password"))) {
                    //设置redis缓存 缓存用户信息 30天 毫秒
                    String user = JSON.toJSONString(task);
                    updateCache(user, map.get("mobile"));
                    //缓存用户-账本
                    setAccountBookCache(task.getUserInfoId(), map.get("mobile"));
                    rb.setSucResult(ApiResultType.OK);
                    //返回token  expire
                    Map<String, Object> map2 = new HashMap<>();
                    String token = createTokenUtils.createToken(map.get("mobile"));
                    map2 = SetTokenToAppUtils.getTokenResult(map2, token);
                    rb.setResult(map2);
                } else {
                    rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ERROR);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
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
        if (!ValidateUtils.isMobile(map.get("mobile"))) {
            rb.setFailMsg(ApiResultType.MOBILE_FORMAT_ERROR);
            return rb;
        }
        try {
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
                    Map<String, Object> map2 = new HashMap<>();
                    map2 = SetTokenToAppUtils.getTokenResult(map2, token);
                    //设置redis缓存 缓存用户信息 30天 毫秒
                    String user = JSON.toJSONString(task);
                    //先判断是否存在
                    updateCache(user, map.get("mobile"));
                    //缓存用户-账本
                    setAccountBookCache(task.getUserInfoId(), map.get("mobile"));
                    rb.setResult(map2);
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

    /**
     * app微信授权登录
     *
     * @param type
     * @return
     */
    @ApiOperation(value = "app微信授权登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "state", value = "state域", dataType = "String")
    })
    @RequestMapping(value = "/loginByWeChat/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean loginByWeChat(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //请求参数错误
        if (StringUtils.isEmpty(map.get("code"))) {
            rb.setFailMsg(ApiResultType.WECHAT_CODE_ISNULL);
            return rb;
        }
        JSONObject user = WeChatUtils.getUser(map.get("code"));
        if (user == null) {
            rb.setFailMsg(ApiResultType.WECHAT_LOGIN_ERROR);
            return rb;
        }
        try {
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
                    Map<String, Object> map2 = new HashMap<>();
                    map2 = SetTokenToAppUtils.getTokenResult(map2, token);
                    //设置redis缓存 缓存用户信息 30天 毫秒
                    UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionid"));
                    String userToString2 = JSON.toJSONString(task2);
                    updateCache(userToString2, user.getString("unionid"));
                    //缓存用户-账本
                    setAccountBookCache(task.getUserInfoId(), user.getString("unionid"));
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
                    updateCache(userToString, task.getMobile());
                    //缓存用户-账本
                    setAccountBookCache(task.getUserInfoId(), task.getMobile());
                } else {
                    token = createTokenUtils.createToken(user.getString("unionid"));
                    updateCache(userToString, user.getString("unionid"));
                    //缓存用户-账本
                    setAccountBookCache(task.getUserInfoId(), user.getString("unionid"));
                }
                Map<String, Object> map2 = new HashMap<>();
                map2 = SetTokenToAppUtils.getTokenResult(map2, token);
                rb.setResult(map2);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
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
        try {
            String user = WXAppletUtils.getUser(map.get("code"));
            JSONObject jsonObject = JSONObject.parseObject(user);
            if (jsonObject.getString("errcode") != null) {
                rb.setFailMsg(ApiResultType.WXAPPLET_LOGIN_ERROR);
            } else {
                String unionid = jsonObject.getString("unionid");
                if (StringUtils.isNotEmpty(unionid)) {
                    //获取到unionid
                    UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", unionid);
                    String userToString = JSON.toJSONString(task);
                    if (task == null) {
                        //执行注册流程  TODO 需要移动端传递解密数据获取unionid，id可以从wx获取到--->说明移动端已经微信授权，即已经缓存用户信息
                        // TODO 此种情况不应该出现，如果出现 先只保存unionid吧
                        UserInfoRestEntity uire = new UserInfoRestEntity();
                        uire.setWechatAuth(unionid);
                        int insert = userInfoRestServiceI.insert(uire);
                        if (insert > 0) {
                            rb.setSucResult(ApiResultType.OK);
                            //根据openid生成token  expire
                            String token = createTokenUtils.createToken(unionid);
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("token", token);
                            map2.put("expire", RedisPrefix.USER_EXPIRE_TIME);
                            //设置redis缓存 缓存用户信息 30天 毫秒
                            UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", unionid);
                            String userToString2 = JSON.toJSONString(task2);
                            updateCache(userToString, unionid);
                            rb.setResult(map2);
                        } else {
                            rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
                        }
                    } else {
                        //登录流程 {"session_key":"i2VyPTkFlFNh8bThTGXShg==","openid":"ojYTl5RhdfPo9hKspMa8sfJ3Fvno"}
                        rb.setSucResult(ApiResultType.OK);
                        //根据openid生成token  expire
                        //手机号不为空情况下
                        String token;
                        if (StringUtils.isNotEmpty(task.getMobile())) {
                            token = createTokenUtils.createToken(task.getMobile());
                            updateCache(userToString, task.getMobile());
                        } else {
                            token = createTokenUtils.createToken(unionid);
                            updateCache(userToString, unionid);
                        }
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("token", token);
                        map2.put("expire", RedisPrefix.USER_EXPIRE_TIME);
                        rb.setResult(map2);
                    }
                } else {
                    String session_key = jsonObject.getString("session_key");
                    String sessionKeyPrefix = CommonUtils.getSessionKeyPrefix();
                    //unionid为空的情况下 需要wx提供加密的userinfo,缓存session_key
                    redisTemplate.opsForValue().set(RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY + sessionKeyPrefix, session_key, RedisPrefix.SESSION_KEY_TIME, TimeUnit.MINUTES);
                    //RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY
                    rb.setFailMsg(ApiResultType.UNIONID_IS_NULL);
                    Map<String,String> map2 = new HashMap();
                    map2.put("key",sessionKeyPrefix);
                    rb.setResult(map2);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "微信小程序注册")
    @RequestMapping(value = "/registerByWXApplet/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean registerByWXApplet(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //encryptedData 加密数据
        if (StringUtil.isEmpty(map.get("encryptedData"))) {
            rb.setFailMsg(ApiResultType.encryptedData_IS_NULL);
            return rb;
        }
        if (StringUtil.isEmpty(map.get("iv"))) {
            rb.setFailMsg(ApiResultType.IV_IS_NULL);
            return rb;
        }
        if (StringUtil.isEmpty(map.get("key"))) {
            rb.setFailMsg(ApiResultType.KEY_IS_NULL);
            return rb;
        }
        try {
            //解密  获取sessionkey
            String key = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_WXAPPLET_SESSION_KEY + map.get("key"));
            JSONObject user = WXAppletDecodeUtils.getUserInfo(map.get("encryptedData"), key, map.get("iv"));
            logger.info(user.toJSONString());
            if (user == null) {
                rb.setFailMsg(ApiResultType.WXAPPLET_LOGIN_ERROR);
            } else if(StringUtils.isEmpty(user.getString("unionId"))){
                rb.setFailMsg(ApiResultType.UNIONID_IS_NULL);
            }else {
                //先查询unionId是否存在
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionId"));
                if(task!=null){
                    String userToString = JSON.toJSONString(task);
                    String token;
                    if (StringUtils.isNotEmpty(task.getMobile())) {
                        token = createTokenUtils.createToken(task.getMobile());
                        updateCache(userToString, task.getMobile());
                    } else {
                        token = createTokenUtils.createToken(user.getString("unionId"));
                        updateCache(userToString, user.getString("unionId"));
                    }
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("token", token);
                    map2.put("expire", RedisPrefix.USER_EXPIRE_TIME);
                    rb.setSucResult(ApiResultType.OK);
                    rb.setResult(map2);
                    return rb;
                }

                //注册
                int insert = userInfoRestServiceI.wechatinsert(user);
                if (insert > 0) {
                    rb.setSucResult(ApiResultType.OK);
                    //unionid  expire
                    String token = createTokenUtils.createToken(user.getString("unionId"));
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("token", token);
                    map2.put("expire", RedisPrefix.USER_EXPIRE_TIME);
                    //设置redis缓存 缓存用户信息 30天 毫秒
                    UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionId"));
                    String userToString2 = JSON.toJSONString(task2);
                    updateCache(userToString2, user.getString("unionId"));
                    //缓存用户-账本
                    setAccountBookCache(task2.getUserInfoId(), user.getString("unionId"));
                    rb.setResult(map2);
                }else{
                    rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
                }
                return rb;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
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
        public ResultBean resetpwd (@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
        type, @RequestBody @ApiIgnore Map < String, String > map){
            System.out.println("登录终端：" + type);
            ResultBean rb = new ResultBean();
            //用户名或验证码或密码错误
            if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("password")) || StringUtil.isEmpty(map.get("verifycode"))) {
                rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
                return rb;
            }
            if (!ValidateUtils.isMobile(map.get("mobile"))) {
                rb.setFailMsg(ApiResultType.MOBILE_FORMAT_ERROR);
                return rb;
            }
            try {
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
                        Map<String, Object> map2 = new HashMap<>();
                        map2 = SetTokenToAppUtils.getTokenResult(map2, token);
                        //设置redis缓存 缓存用户信息 30天 毫秒
                        UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                        String user = JSON.toJSONString(task);
                        updateCache(user, map.get("mobile"));
                        rb.setSucResult(ApiResultType.OK);
                        rb.setResult(map2);
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
        public ResultBean updatepwd (@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
        type, @RequestBody @ApiIgnore Map < String, String > map, HttpServletRequest request){
            System.out.println("登录终端：" + type);
            ResultBean rb = new ResultBean();
            //验证密码
            if (StringUtil.isEmpty(map.get("oldpwd")) || StringUtil.isEmpty(map.get("newpwd"))) {
                rb.setFailMsg(ApiResultType.REQ_PARAMS_ERROR);
                return rb;
            }
            try {
                String code = (String) request.getAttribute("code");
                String r_user = getUserCache(code);
                //转成对象
                UserLoginRestEntity userLoginRestEntity = JSON.parseObject(r_user, UserLoginRestEntity.class);
                if (StringUtils.equals(userLoginRestEntity.getPassword(), map.get("oldpwd"))) {
                    //执行更新密码流程
                    int i = userInfoRestServiceI.updatePWD(code, map.get("newpwd"));
                    if (i < 1) {
                        rb.setFailMsg(ApiResultType.PASSWORD_UPDATE_ERROR);
                        return rb;
                    }
                    //设置redis缓存 缓存用户信息
                    userLoginRestEntity.setPassword(map.get("newpwd"));
                    String user = JSON.toJSONString(userLoginRestEntity);
                    updateCache(user, code);
                    rb.setSucResult(ApiResultType.OK);
                } else {
                    rb.setFailMsg(ApiResultType.PASSWORD_ERROR);
                }
            } catch (Exception e) {
                logger.error(e.toString());
                rb.setFailMsg(ApiResultType.SERVER_ERROR);
                return rb;
            }
            return rb;
        }

        @ApiOperation(value = "退出登录")
        @RequestMapping(value = "/logout/{type}", method = RequestMethod.GET)
        @ResponseBody
        public ResultBean logout (@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
        type, HttpServletRequest request){
            System.out.println("登录终端：" + type);
            ResultBean rb = new ResultBean();
            String code = (String) request.getAttribute("code");
            try {
                redisTemplate.delete(code);
                rb.setSucResult(ApiResultType.OK);
            } catch (Exception e) {
                logger.error(e.toString());
                rb.setFailMsg(ApiResultType.SERVER_ERROR);
                return rb;
            }
            return rb;
        }

        //从cache获取用户信息
        private String getUserCache (String code){
            String user = (String) redisTemplate.opsForValue().get(code);
            //为null 重新获取缓存
            if (StringUtils.isEmpty(user)) {
                UserLoginRestEntity task;
                //判断code类型
                if (ValidateUtils.isMobile(code)) {
                    task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", code);
                } else {
                    task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechat_auth", code);
                }
                //设置redis缓存 缓存用户信息 30天 毫秒
                String r_user = JSON.toJSONString(task);
                updateCache(r_user, code);
                return r_user;
            }
            return user;
        }

        //更新redis缓存通用方法
        private void updateCache (String user, String code){
            redisTemplate.opsForValue().set(code, user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
        }

        //通用方法  用户登录之后缓存用户---账本关系表
        private void setAccountBookCache ( int userInfoId, String code){
            UserAccountBookRestEntity task = userAccountBookRestServiceI.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            String userAccountBook = JSON.toJSONString(task);
            if (task != null) {
                redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code, userAccountBook, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            }
        }

        @RequestMapping(value = "/login", method = RequestMethod.POST)
        @ResponseBody
        public ResultBean login (@RequestBody Map < String, String > map){
            return this.login(null, map);
        }

        @RequestMapping(value = "/loginByCode", method = RequestMethod.POST)
        @ResponseBody
        public ResultBean loginByCode (@RequestBody Map < String, String > map){
            return this.loginByCode(null, map);
        }

        @RequestMapping(value = "/loginByWeChat", method = RequestMethod.POST)
        @ResponseBody
        public ResultBean loginByWeChat (@RequestBody Map < String, String > map){
            return this.loginByWeChat(null, map);
        }

        @RequestMapping(value = "/loginByWXApplet", method = RequestMethod.POST)
        @ResponseBody
        public ResultBean loginByWXApplet (@RequestBody Map < String, String > map){
            return this.loginByWXApplet(null, map);
        }

        @RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
        @ResponseBody
        public ResultBean resetpwd (@RequestBody Map < String, String > map){
            return this.resetpwd(null, map);
        }

        @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
        @ResponseBody
        public ResultBean updatepwd (@RequestBody Map < String, String > map, HttpServletRequest request){
            return this.updatepwd(null, map, request);
        }

        @RequestMapping(value = "/logout", method = RequestMethod.GET)
        @ResponseBody
        public ResultBean logout (HttpServletRequest request){
            return this.logout(null, request);
        }

    @RequestMapping(value = "/registerByWXApplet", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean registerByWXApplet (@RequestBody @ApiIgnore Map<String, String> map){
        return this.registerByWXApplet(null, map);
    }
}
