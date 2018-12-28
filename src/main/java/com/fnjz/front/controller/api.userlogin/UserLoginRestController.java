package com.fnjz.front.controller.api.userlogin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.DomainEnum;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.enums.LoginEnum;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.*;
import com.fnjz.front.utils.newWeChat.WXAppletUtils;
import com.fnjz.front.utils.newWeChat.WeChatUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @version V1.0
 * @Title: Controller
 * @Description: 用户登录表相关
 * @date 2018-05-30 22:41:49
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
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
    private RedisTemplateUtils redisTemplateUtils;
    @Autowired
    private WXAppletUtils wxAppletUtils;
    @Autowired
    private WeChatUtils weChatUtils;

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
        ResultBean rb = ParamValidateUtils.checkLogin(map, LoginEnum.LOGIN_BY_PWD);
        //校验用户名或密码错误
        if (rb != null) {
            return rb;
        }
        try {
            //验证用户名密码
            UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
            if (task == null) {
                return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
            } else {
                //判断密码
                if (StringUtil.equals(task.getPassword(), PasswordUtils.getEncryptpwd(map.get("password")))) {
                    return createTokenUtils.loginSuccess(task, ShareCodeUtil.id2sharecode(task.getUserInfoId()));
                } else {
                    return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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
        ResultBean rb = ParamValidateUtils.checkLogin(map, LoginEnum.LOGIN_BY_VERIFYCODE);
        //用户名或验证码错误
        if (rb != null) {
            return rb;
        }
        try {
            //获取验证码
            String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_LOGIN + map.get("mobile"));
            if (StringUtil.isEmpty(code)) {
                //验证码为空
                return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT, null);
            } else {
                if (StringUtil.equals(code, map.get("verifycode"))) {
                    UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                    return createTokenUtils.loginSuccess(task, ShareCodeUtil.id2sharecode(task.getUserInfoId()));
                } else {
                    return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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
        ResultBean rb = ParamValidateUtils.checkeLoginByWechat(map, LoginEnum.LOGIN_BY_WECHAT);
        //请求参数错误
        if (rb != null) {
            return rb;
        }
        JSONObject user = weChatUtils.getUser(map.get("code"));
        if (user == null) {
            return new ResultBean(ApiResultType.WECHAT_LOGIN_ERROR, null);
        }
        logger.info("移动端微信登录:"+user.toString());
        try {
            //查看unionid是否存在
            UserInfoRestEntity task = userLoginRestService.findUniqueByProperty(UserInfoRestEntity.class, "wechatAuth", user.getString("unionid"));
            if (task == null) {
                //注册
                int insert = userInfoRestServiceI.wechatinsert(user, map, type);
                if (insert > 0) {
                    UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionid"));
                    return createTokenUtils.loginSuccess(task2, ShareCodeUtil.id2sharecode(task2.getUserInfoId()));
                } else {
                    return new ResultBean(ApiResultType.REGISTER_IS_ERROR, null);
                }
            } else {
                if (StringUtils.isNotEmpty(task.getAvatarUrl())) {
                    if (!StringUtils.contains(task.getAvatarUrl(), DomainEnum.HEAD_PICTURE_DOMAIN.getDomainUrl())) {
                        if (StringUtils.isEmpty(task.getNickName())) {
                            //更新用户个人信息
                            if (StringUtils.isNotEmpty(user.getString("nickname"))) {
                                String nickName = user.getString("nickname");
                                nickName = FilterCensorWordsUtils.checkWechatNickName(nickName);
                                task.setNickName(nickName);
                            }
                            if (StringUtils.isNotEmpty(user.getString("headimgurl"))) {
                                task.setAvatarUrl(user.getString("headimgurl"));
                            }
                            userInfoRestServiceI.updateUserInfo(task, null);
                        } else {
                            if (StringUtils.isNotEmpty(user.getString("headimgurl"))) {
                                task.setAvatarUrl(user.getString("headimgurl"));
                            }
                            userInfoRestServiceI.updateUserInfo(task, null);
                        }
                    }
                } else {
                    if (StringUtils.isEmpty(task.getNickName())) {
                        //更新用户个人信息
                        if (StringUtils.isNotEmpty(user.getString("nickname"))) {
                            String nickName = user.getString("nickname");
                            nickName = FilterCensorWordsUtils.checkWechatNickName(nickName);
                            task.setNickName(nickName);
                        }
                        if (StringUtils.isNotEmpty(user.getString("headimgurl"))) {
                            task.setAvatarUrl(user.getString("headimgurl"));
                        }
                        userInfoRestServiceI.updateUserInfo(task, null);
                    } else {
                        if (StringUtils.isNotEmpty(user.getString("headimgurl"))) {
                            task.setAvatarUrl(user.getString("headimgurl"));
                        }
                        userInfoRestServiceI.updateUserInfo(task, null);
                    }
                }
                UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionid"));
                return createTokenUtils.loginSuccess(task2, ShareCodeUtil.id2sharecode(task.getId()));
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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
        ResultBean rb = ParamValidateUtils.checkeLoginByWechat(map, LoginEnum.LOGIN_BY_WXAPPLET);
        //code为空
        if (rb != null) {
            return rb;
        }
        try {
            String user = wxAppletUtils.getUser(map.get("code"));
            JSONObject jsonObject = JSONObject.parseObject(user);
            if (jsonObject.getString("errcode") != null) {
                return new ResultBean(ApiResultType.WXAPPLET_LOGIN_ERROR, null);
            } else {
                String unionid = jsonObject.getString("unionid");
                if (StringUtils.isNotEmpty(unionid)) {
                    //获取到unionid
                    UserInfoRestEntity task = userLoginRestService.findUniqueByProperty(UserInfoRestEntity.class, "wechatAuth", unionid);
                    //用户已授权---->1未查询到用户信息 2.查询寻到 更新微信昵称头像
                    if (task != null) {
                        if (StringUtils.isNotEmpty(task.getAvatarUrl())) {
                            if (!StringUtils.contains(task.getAvatarUrl(), DomainEnum.HEAD_PICTURE_DOMAIN.getDomainUrl())) {
                                String sessionKey = jsonObject.getString("session_key");
                                return createTokenUtils.returnKeyToWXApplet(sessionKey);
                            } else {
                                //登录流程
                                UserLoginRestEntity task1 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", unionid);
                                return createTokenUtils.wxappletLoginSuccess(task1, ShareCodeUtil.id2sharecode(task.getId()));
                            }
                        }
                    }
                    String sessionKey = jsonObject.getString("session_key");
                    return createTokenUtils.returnKeyToWXApplet(sessionKey);
                } else {
                    String sessionKey = jsonObject.getString("session_key");
                    return createTokenUtils.returnKeyToWXApplet(sessionKey);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "微信小程序注册")
    @RequestMapping(value = "/registerByWXApplet/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean registerByWXApplet(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkRegisterByWXApplet(map);
        //encryptedData 加密数据
        if (rb != null) {
            return rb;
        }
        try {
            //解密  获取sessionkey
            String key = redisTemplateUtils.getSessionKey(map.get("key"));
            JSONObject user = WXAppletDecodeUtils.getUserInfo(map.get("encryptedData"), key, map.get("iv"));
            logger.info(user.toJSONString());
            if (user == null) {
                return new ResultBean(ApiResultType.WXAPPLET_LOGIN_ERROR, null);
            } else if (StringUtils.isEmpty(user.getString("unionId"))) {
                return new ResultBean(ApiResultType.UNIONID_IS_NULL, null);
            } else {
                //先查询unionId是否存在
                UserInfoRestEntity task = userLoginRestService.findUniqueByProperty(UserInfoRestEntity.class, "wechatAuth", user.getString("unionId"));
                if (task != null) {
                    if (StringUtils.isNotEmpty(task.getAvatarUrl())) {
                        if (!StringUtils.contains(task.getAvatarUrl(), DomainEnum.HEAD_PICTURE_DOMAIN.getDomainUrl())) {
                            if (StringUtils.isEmpty(task.getNickName())) {
                                //更新用户个人信息
                                if (StringUtils.isNotEmpty(user.getString("nickName"))) {
                                    String nickName = user.getString("nickName");
                                    nickName = FilterCensorWordsUtils.checkWechatNickName(nickName);
                                    task.setNickName(nickName);
                                }
                                if (StringUtils.isNotEmpty(user.getString("avatarUrl"))) {
                                    task.setAvatarUrl(user.getString("avatarUrl"));
                                }
                                userInfoRestServiceI.updateUserInfo(task, null);
                            } else {
                                if (StringUtils.isNotEmpty(user.getString("avatarUrl"))) {
                                    task.setAvatarUrl(user.getString("avatarUrl"));
                                }
                                userInfoRestServiceI.updateUserInfo(task, null);
                            }
                        }
                    } else {
                        if (StringUtils.isEmpty(task.getNickName())) {
                            //更新用户个人信息
                            if (StringUtils.isNotEmpty(user.getString("nickName"))) {
                                String nickName = user.getString("nickName");
                                nickName = FilterCensorWordsUtils.checkWechatNickName(nickName);
                                task.setNickName(nickName);
                            }
                            if (StringUtils.isNotEmpty(user.getString("avatarUrl"))) {
                                task.setAvatarUrl(user.getString("avatarUrl"));
                            }
                            userInfoRestServiceI.updateUserInfo(task, null);
                        } else {
                            if (StringUtils.isNotEmpty(user.getString("avatarUrl"))) {
                                task.setAvatarUrl(user.getString("avatarUrl"));
                            }
                            userInfoRestServiceI.updateUserInfo(task, null);
                        }
                    }
                    UserLoginRestEntity task1 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionId"));
                    return createTokenUtils.wxappletLoginSuccess(task1, ShareCodeUtil.id2sharecode(task.getId()));
                }
                //注册
                int insert = userInfoRestServiceI.wechatinsert(user, map, null);
                if (insert > 0) {
                    UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionId"));
                    return createTokenUtils.wxappletLoginSuccess(task2, ShareCodeUtil.id2sharecode(task2.getUserInfoId()));
                } else {
                    return new ResultBean(ApiResultType.REGISTER_IS_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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
    public ResultBean resetpwd(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
                                       type, @RequestBody @ApiIgnore Map<String, String> map) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkResetpwd(map);
        //用户名或验证码或密码错误
        if (rb != null) {
            return rb;
        }
        try {
            ///获取验证码
            String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_RESETPWD + map.get("mobile"));
            if (StringUtil.isEmpty(code)) {
                //验证码为空
                return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT, null);
            } else {
                if (StringUtil.equals(code, map.get("verifycode"))) {
                    //执行更新密码流程
                    int i = userInfoRestServiceI.updatePWDByMobile(map.get("mobile"), PasswordUtils.getEncryptpwd(map.get("password")));
                    if (i < 1) {
                        return new ResultBean(ApiResultType.PASSWORD_UPDATE_ERROR, null);
                    }
                    //返回token  expire
                    UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
                    return createTokenUtils.loginSuccess(task, ShareCodeUtil.id2sharecode(task.getUserInfoId()));
                } else {
                    return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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
    public ResultBean updatepwd(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
                                        type, @RequestBody @ApiIgnore Map<String, String> map, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkUpdatepwd(map);
        //验证密码
        if (rb != null) {
            return rb;
        }
        try {
            String key = (String) request.getAttribute("key");
            String r_user = redisTemplateUtils.getUserCache(key);
            String userInfoId = (String) request.getAttribute("userInfoId");
            //转成对象
            UserLoginRestEntity userLoginRestEntity = JSON.parseObject(r_user, UserLoginRestEntity.class);
            String oldpwd = PasswordUtils.getEncryptpwd(map.get("oldpwd"));
            if (StringUtils.equals(userLoginRestEntity.getPassword(), oldpwd)) {
                //执行更新密码流程
                String newpwd = PasswordUtils.getEncryptpwd(map.get("newpwd"));
                int i = userInfoRestServiceI.updatePWD(Integer.valueOf(userInfoId), newpwd);
                if (i < 1) {
                    return new ResultBean(ApiResultType.PASSWORD_UPDATE_ERROR, null);
                }
                //设置redis缓存 缓存用户信息
                userLoginRestEntity.setPassword(newpwd);
                redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
                return new ResultBean(ApiResultType.OK, null);
            } else {
                return new ResultBean(ApiResultType.PASSWORD_ERROR, null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "退出登录")
    @RequestMapping(value = "/logout/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean logout(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
                                     type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        String key = (String) request.getAttribute("key");
        try {
            redisTemplateUtils.deleteKey(key);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
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

    @RequestMapping(value = "/loginByWeChat", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean loginByWeChat(@RequestBody Map<String, String> map) {
        return this.loginByWeChat(null, map);
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

    @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean updatepwd(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return this.updatepwd(null, map, request);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean logout(HttpServletRequest request) {
        return this.logout(null, request);
    }

    @RequestMapping(value = "/registerByWXApplet", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean registerByWXApplet(@RequestBody @ApiIgnore Map<String, String> map) {
        return this.registerByWXApplet(null, map);
    }


    public ResultBean mobileBindWeChat(JSONObject map) {

        String mobile = map.get("mobile").toString();
        String unionid = map.get("unionId").toString();
        UserLoginRestEntity userLgonMobile = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile", mobile);
        UserInfoRestEntity userInfoMobile = userInfoRestServiceI.findUniqueByProperty(UserInfoRestEntity.class, "mobile", mobile);
        //用户注册表信息表数据是否正常
        if (userLgonMobile != null && userInfoMobile != null) {
            //当前uniond是否已经注册
            UserLoginRestEntity userLoginwechatAuth = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", unionid);
            UserInfoRestEntity userInfowechatAuth = userInfoRestServiceI.findUniqueByProperty(UserInfoRestEntity.class, "wechatAuth", unionid);
            if (userLoginwechatAuth != null || userInfowechatAuth != null) {
                return new ResultBean(ApiResultType.SERVER_ERROR, "当前微信账号已经存在，或者数据异常");

            } else {
                //根据手机号更新uniond
                userInfoMobile.setWechatAuth(unionid);
                userInfoMobile.setSex(map.getString("gender"));
                userInfoMobile.setAvatarUrl( map.getString("avatarUrl"));
                userInfoMobile.setNickName(map.getString("nickName"));
                //login表
                userLgonMobile.setWechatAuth(unionid);
                userLoginRestService.updateEntitie(userLgonMobile);
                userInfoRestServiceI.updateEntitie(userInfoMobile);
                return createTokenUtils.wxappletLoginSuccess(userLgonMobile, ShareCodeUtil.id2sharecode(userLgonMobile.getUserInfoId()));
            }
        } else {
            return new ResultBean(ApiResultType.SERVER_ERROR, "手机号未注册，请求注册接口");
        }

    }

    /**
     * 功能描述: 判断微信是否注册
     *
     * @param: code
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 15:20
     */
    @RequestMapping(value = "/wxUnionidIsExist/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean wxUnionidIsExist(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map) {

        //根据code获取session_key
        String code = wxAppletUtils.getUser(map.get("code"));
        //转换成json对象
        JSONObject jsonObject = JSONObject.parseObject(code);
        if (jsonObject == null || jsonObject.getString("errcode") != null) {
            System.out.println(jsonObject.getString("errmsg"));
            logger.error("wechat授权异常:" + jsonObject.getString("errmsg"));
            return new ResultBean(ApiResultType.WXAPPLET_LOGIN_ERROR, "小程序解码异常");
        }

        String session_key = jsonObject.getString("session_key");
        //根据sessionkey解密unionid
        JSONObject user = WXAppletDecodeUtils.getUserInfo(map.get("encryptedData"), session_key, map.get("iv"));
        //微信解码正常判断是否已经注册
        if (user != null && StringUtils.isNotEmpty(user.getString("unionId"))) {
            String unionId = user.getString("unionId");
            UserLoginRestEntity userEntity = userLoginRestService.wxUnionidIsExist(unionId);
            if (userEntity != null) {
                ResultBean resultBean = createTokenUtils.wxappletLoginSuccess(userEntity, ShareCodeUtil.id2sharecode(userEntity.getUserInfoId()));
                return resultBean;
            } else {
                //用户没有注册，先返回uniond，到注册时候在传过来
                return new ResultBean(ApiResultType.USER_NOT_EXIST, user);
            }
        } else {
            return new ResultBean(ApiResultType.SERVER_ERROR, "unionid解码异常");
        }

    }

    /**
     * 功能描述: 验证手机号
     *
     * @param: mobile 手机号   code  验证码
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/19 17:27
     */
    @RequestMapping(value = "/completeMobile/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean completeMobile(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore JSONObject map) {
        String mobile = map.getString("mobile");
        String verifycode = map.getString("verifycode");
        if (StringUtils.isEmpty(mobile)) {
            return new ResultBean(ApiResultType.USERNAME_OR_PASSWORD_ISNULL, null);
        }
        try {
            //获取验证码
            String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE + map.getString("mobile"));
            if (StringUtil.isEmpty(code)) {
                //验证码为空
                return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT, null);
            } else {
                if (StringUtil.equals(code, verifycode)) {
                    List<UserLoginRestEntity> UserLoginRestEntity = userLoginRestService.findByProperty(UserLoginRestEntity.class, "mobile", mobile);
                    //如果是手机号老用户去调用手机号绑定微信接口
                    if (UserLoginRestEntity.size() > 0) {
                        //绑定
                        ResultBean resultBean = mobileBindWeChat(map);
                        return resultBean;
                    } else {
                        //新用户，调注册接口，多一个参数手机号
                        ResultBean resultBean = accountRegister(map);
                        return resultBean;
                    }

                } else {
                    return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    public ResultBean accountRegister(JSONObject user) {
        //先查询unionId是否存在
        UserInfoRestEntity task = userLoginRestService.findUniqueByProperty(UserInfoRestEntity.class, "wechatAuth", user.getString("unionId"));
        if (task == null) {
            //注册
            int insert = userInfoRestServiceI.wechatinsert(user, new HashMap<>(), null);
            if (insert > 0) {
                UserLoginRestEntity task2 = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionId"));
                return createTokenUtils.wxappletLoginSuccess(task2, ShareCodeUtil.id2sharecode(task2.getUserInfoId()));
            } else {
                return new ResultBean(ApiResultType.REGISTER_IS_ERROR, null);
            }
        } else {
            return new ResultBean(ApiResultType.SERVER_ERROR, "此微信账号已注册");
        }
    }
}
