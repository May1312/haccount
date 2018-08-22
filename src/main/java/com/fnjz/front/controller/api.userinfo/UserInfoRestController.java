package com.fnjz.front.controller.api.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.DomainEnum;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userinfo.UserInfoRestDTO;
import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.enums.LoginEnum;
import com.fnjz.front.enums.QiNiuEnum;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.*;
import com.fnjz.utils.upload.QiNiuUploadFileUtils;
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
import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 用户详情表相关
 * @date 2018-05-30 14:05:50
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
@Api(description = "android/ios", tags = "账户安全接口")
public class UserInfoRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserInfoRestController.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

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
        try {
            ResultBean rb;
            String key = (String) request.getAttribute("key");
            UserLoginRestEntity userLoginRestEntity = redisTemplateUtils.getUserLoginRestEntityCache(key);
            if (StringUtils.isNotEmpty(userLoginRestEntity.getMobile()) && StringUtils.isNotEmpty(userLoginRestEntity.getPassword())) {
                //判断手机号 验证码
                rb = ParamValidateUtils.checkLogin(map, LoginEnum.LOGIN_BY_VERIFYCODE);
                if (rb != null) {
                    return rb;
                }
                //获取验证码
                String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"));
                if (StringUtils.isEmpty(code)) {
                    return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT, null);
                }
                if (StringUtil.equals(code, map.get("verifycode"))) {
                    //执行更新手机号流程
                    String userInfoId = (String) request.getAttribute("userInfoId");
                    int i = userInfoRestServiceI.updateMobile(userInfoId, map.get("mobile"));
                    if (i < 1) {
                        return new ResultBean(ApiResultType.MOBILE_UPDATE_ERROR, null);
                    }
                    //更新用户缓存
                    userLoginRestEntity.setMobile(map.get("mobile"));
                    redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
                    return new ResultBean(ApiResultType.OK, null);
                } else {
                    return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
                }
            } else {//微信绑定手机号密码
                //判断手机号 密码 验证码
                rb = ParamValidateUtils.checkResetpwd(map);
                if (rb != null) {
                    return rb;
                }
                //获取验证码
                String code = redisTemplateUtils.getVerifyCode(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"));
                if (StringUtils.isEmpty(code)) {
                    //验证码为空
                    return new ResultBean(ApiResultType.VERIFYCODE_TIME_OUT, null);
                }
                if (StringUtils.equals(code, map.get("verifycode"))) {
                    //执行更新手机号 密码 流程
                    String userInfoId = (String) request.getAttribute("userInfoId");
                    String pwd = PasswordUtils.getEncryptpwd(map.get("password"));
                    int i = userInfoRestServiceI.updateMobileAndPWD(userInfoId, map.get("mobile"),pwd);
                    if (i < 1) {
                        return new ResultBean(ApiResultType.BIND_MOBILE_PWD_ERROR, null);
                    }
                    //更新用户缓存
                    userLoginRestEntity.setMobile(map.get("mobile"));
                    //TODO 更新密码？ 是否必要
                    userLoginRestEntity.setPassword(pwd);
                    redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
                    return new ResultBean(ApiResultType.OK, null);
                } else {
                    return new ResultBean(ApiResultType.VERIFYCODE_IS_ERROR, null);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "手机注册用户绑定微信wechat")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code码", required = true, dataType = "String")
    })
    @RequestMapping(value = "/bindWeChat/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean bindWeChat(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkeLoginByWechat(map, LoginEnum.LOGIN_BY_WECHAT);
        //判断CODE
        if (rb != null) {
            return rb;
        }
        try {
            String key = (String) request.getAttribute("key");
            UserLoginRestEntity userLoginRestEntity = redisTemplateUtils.getUserLoginRestEntityCache(key);
            if (StringUtils.isNotEmpty(userLoginRestEntity.getMobile()) && StringUtils.isNotEmpty(userLoginRestEntity.getPassword())) {
                //获取unionid
                JSONObject user = WeChatUtils.getUser(map.get("code"));
                if (user == null) {
                    return new ResultBean(ApiResultType.WECHAT_BIND_ERROR, null);
                }
                //判断db中是否已经存在此unionid
                UserLoginRestEntity task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth", user.getString("unionid"));
                if (task != null) {
                    //微信号已经注册
                    return new ResultBean(ApiResultType.WECHAT_IS_BINDED, null);
                }
                //更新绑定unionid
                int i = userInfoRestServiceI.updateWeChat(userLoginRestEntity.getMobile(), user.getString("unionid"));
                if (i < 1) {
                    return new ResultBean(ApiResultType.WECHAT_BIND_ERROR, null);
                }
                //设置redis缓存 缓存用户信息
                userLoginRestEntity.setWechatAuth(user.getString("unionid"));
                redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
                return new ResultBean(ApiResultType.OK, null);
            } else if (StringUtils.isNotEmpty(userLoginRestEntity.getWechatAuth())) {
                //非手机号注册用户无法绑定微信
                return new ResultBean(ApiResultType.NOT_ALLOW_BIND_WECHAT, null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
        return new ResultBean(ApiResultType.SERVER_ERROR, null);
    }

    @ApiOperation(value = "手机注册用户解绑微信wechat")
    @RequestMapping(value = "/unbindWeChat/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean unbindWeChat(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        try {
            String key = (String) request.getAttribute("key");
            //解绑用户
            UserLoginRestEntity userLoginRestEntity = redisTemplateUtils.getUserLoginRestEntityCache(key);
            //判断是否为手机用户
            if (StringUtils.isEmpty(userLoginRestEntity.getMobile())) {
                return new ResultBean(ApiResultType.NOT_ALLOW_UNBIND_WECHAT, null);
            }
            int i = userInfoRestServiceI.updateWeChat(userLoginRestEntity.getMobile(), null);
            if (i < 1) {
                return new ResultBean(ApiResultType.WECHAT_UNBIND_ERROR, null);
            }
            //设置redis缓存
            userLoginRestEntity.setWechatAuth(null);
            redisTemplateUtils.updateCacheSimple(userLoginRestEntity, key);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "获取用户详情")
    @RequestMapping(value = "/userInfo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean userInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            UserInfoRestDTO task = userInfoRestServiceI.findUniqueByProperty(UserInfoRestDTO.class, "id", Integer.valueOf(userInfoId));
            if (task != null) {
                //设置蜂鸟id
                task.setId(Integer.valueOf(ShareCodeUtil.id2sharecode(task.getId())));
                //转义昵称
                if (StringUtils.isNotEmpty(task.getNickName())) {
                    //task.setNickName(EmojiUtils.aliasToEmoji(task.getNickName()));
                    task.setNickName(task.getNickName());
                }
                if (StringUtils.isNotEmpty(task.getWechatAuth())) {
                    task.setWechatAuth("wechatAuth");
                }
                return new ResultBean(ApiResultType.OK, task);
            } else {
                return new ResultBean(ApiResultType.USER_NOT_EXIST, null);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取七牛云上传鉴权 1为头像 2为反馈
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "/getQiNiuAuth/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean getQiNiuAuth(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody Map<String, String> map) {
        try {
            QiNiuUploadFileUtils qiniu = new QiNiuUploadFileUtils();
            String upToken;
            if (StringUtils.isEmpty(map.get("flag"))) {
                upToken = qiniu.getUpToken(QiNiuEnum.HEAD_PICTURE.getName());
            } else {
                if (StringUtils.equals(map.get("flag"), QiNiuEnum.HEAD_PICTURE.getIndex())) {
                    upToken = qiniu.getUpToken(QiNiuEnum.HEAD_PICTURE.getName());
                } else if (StringUtils.equals(map.get("flag"), QiNiuEnum.FEEDBACK_PICTURE.getIndex())) {
                    upToken = qiniu.getUpToken(QiNiuEnum.FEEDBACK_PICTURE.getName());
                } else {
                    upToken = qiniu.getUpToken(QiNiuEnum.HEAD_PICTURE.getName());
                }
            }
            return CommonUtils.returnQiNiuAuth(upToken);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 编辑用户详情
     * @param type
     * @param userInfoRestEntity
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateUserInfo/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateUserInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody UserInfoRestEntity userInfoRestEntity, HttpServletRequest request) {
        if (userInfoRestEntity == null) {
            return new ResultBean(ApiResultType.MY_PARAMS_ERROR, null);
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            userInfoRestEntity.setId(Integer.valueOf(userInfoId));
            if (StringUtils.isNotEmpty(userInfoRestEntity.getNickName())) {
                if(!FilterCensorWordsUtils.checkNickName(userInfoRestEntity.getNickName())){
                    return new ResultBean(ApiResultType.NICKNAME_NOT_FORMAT,null);
                }
            }
            //校验头像url
            //七牛测试域名（以 clouddn.com/qiniucdn.com/qiniudn.com/qnssl.com/qbox.me 结尾）
            if(StringUtils.isNotEmpty(userInfoRestEntity.getAvatarUrl())){
                if(StringUtils.contains(userInfoRestEntity.getAvatarUrl(),".clouddn.com")){
                    //若为七牛云链接 替换成自定义域名  +1不取/位
                    String fileName = userInfoRestEntity.getAvatarUrl().substring(userInfoRestEntity.getAvatarUrl().lastIndexOf("/")+1);
                    //重新设置url
                    userInfoRestEntity.setAvatarUrl(DomainEnum.HEAD_PICTURE_DOMAIN.getDomainUrl()+fileName);
                }
            }
            userInfoRestServiceI.updateUserInfo(userInfoRestEntity);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = "/bindMobile", method = RequestMethod.POST)
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
    public ResultBean getQiNiuAuth(@RequestBody Map<String, String> map) {
        return this.getQiNiuAuth(null, map);
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateUserInfo(@RequestBody UserInfoRestEntity userInfoRestEntity, HttpServletRequest request) {
        return this.updateUserInfo(null, userInfoRestEntity, request);
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean userInfo(HttpServletRequest request) {
        return this.userInfo(null, request);
    }
}
