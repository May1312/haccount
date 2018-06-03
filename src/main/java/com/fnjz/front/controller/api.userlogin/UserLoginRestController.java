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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**   
 * @Title: Controller
 * @Description: 用户登录表相关
 * @date 2018-05-30 22:41:49
 * @version V1.0   
 *
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
	 * @return
	 */
	@ApiOperation(value = "账号密码登录")
	@RequestMapping(value = "/login/{type}" , method = RequestMethod.POST)
    @ResponseBody
	public ResultBean login(@PathVariable("type") String type,@RequestBody Map<String, String> map) {
		System.out.println("登录终端："+type);
		ResultBean rb = new ResultBean();
		//用户名或密码错误
		if(StringUtil.isEmpty(map.get("mobile") ) || StringUtil.isEmpty(map.get("password"))){
			rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ISNULL);
			return rb;
		}
		//验证用户名密码
		UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
		if (task == null) {
			rb.setFailMsg(ApiResultType.USER_NOT_EXIST);
		}else{
			//判断密码
			if(StringUtil.equals(task.getPassword(),map.get("password"))){
				rb.setSucResult(ApiResultType.OK);
				//返回token  expire
				Map<String,Object> map2 = new HashMap<>();
                String token = createTokenUtils.createToken(map.get("mobile"));
                System.out.println("生成的token："+token);
                map2.put("token",token);
				map2.put("expire", 30*24*6060*1000);
				//设置redis缓存 缓存用户信息 30天 毫秒
                String user = JSON.toJSONString(task);
                //先判断是否存在
                if(StringUtil.isEmpty((String)redisTemplate.opsForValue().get(MD5Utils.getMD5(map.get("mobile"))))){
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(map.get("mobile")));
                }
                redisTemplate.opsForValue().set(MD5Utils.getMD5(map.get("mobile")), user,30,  TimeUnit.DAYS);
                rb.setResult(map2);
			}else{
				rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ERROR);
			}
		}
		return rb;
	}

	/**
	 * 短信验证码登录
	 * @param type
	 * @return
	 */
	@ApiOperation(value = "短信验证码登录")
	@RequestMapping(value = "/loginByCode/{type}" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean loginByCode(@PathVariable("type") String type,@RequestBody Map<String, String> map) {
		System.out.println("登录终端："+type);
		ResultBean rb = new ResultBean();
		//用户名或验证码错误
		if(StringUtil.isEmpty(map.get("mobile") ) || StringUtil.isEmpty(map.get("verifycode"))){
			rb.setFailMsg(ApiResultType.USERNAME_OR_VERIFYCODE_ISNULL);
			return rb;
		}
		///获取验证码
        String code = (String)redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_LOGIN+map.get("mobile"));
		if(StringUtil.isEmpty(code)){
			//验证码为空
			rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
		}else{
			if(StringUtil.equals(code,map.get("verifycode"))){
                UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
                rb.setSucResult(ApiResultType.OK);
				//返回token  expire
				Map<String,Object> map2 = new HashMap<>();
				map2.put("token","111111");
				map2.put("expire", 30*24*6060*1000);
                //设置redis缓存 缓存用户信息 30天 毫秒
                String user = JSON.toJSONString(task);
                //先判断是否存在
                if(StringUtil.isEmpty((String)redisTemplate.opsForValue().get(MD5Utils.getMD5(map.get("mobile"))))){
                    //执行删除
                    redisTemplate.delete(MD5Utils.getMD5(map.get("mobile")));
                }
                redisTemplate.opsForValue().set(MD5Utils.getMD5(map.get("mobile")), user,30,  TimeUnit.DAYS);
                rb.setResult(map2);
			}
		}
		return rb;
	}

	/**
	 * app微信授权登录
	 * @param type
	 * @return
	 */
	@ApiOperation(value = "app微信授权登录")
	@RequestMapping(value = "/loginByWeChat/{type}" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean loginByWeChat(@PathVariable("type") String type,@RequestBody Map<String, String> map) {
		System.out.println("登录终端："+type);
		ResultBean rb = new ResultBean();
		//用户名或密码错误
		if(StringUtil.isEmpty(map.get("mobile") ) || StringUtil.isEmpty(map.get("password"))){
			rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ISNULL);
			return rb;
		}
		//验证用户名密码
		UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "mobile",map.get("mobile"));
		if (task == null) {
			rb.setFailMsg(ApiResultType.USER_NOT_EXIST);
		}else{
			//判断密码
			if(StringUtil.equals(task.getPassword(),map.get("password"))){
				rb.setSucResult(ApiResultType.OK);
				//返回token  expire
				Map<String,Object> map2 = new HashMap<>();
				map2.put("token","111111");
				map2.put("expire", 30*24*6060*1000);
				rb.setResult(map2);
			}else{
				rb.setFailMsg(ApiResultType.USERNAME_OR_PASSWORD_ERROR);
			}
		}
		return rb;
	}

	/**
	 * 微信小程序登录
	 * @param type
	 * @return
	 */
	@ApiOperation(value = "微信小程序登录+注册")
	@RequestMapping(value = "/loginByWXApplet/{type}" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean loginByWXApplet(@PathVariable("type") String type,@RequestBody Map<String, String> map) {
		System.out.println("登录终端："+type);
		ResultBean rb = new ResultBean();
		//code为空
		if(StringUtil.isEmpty(map.get("code"))){
			rb.setFailMsg(ApiResultType.WXAPPLET_CODE_ISNULL);
			return rb;
		}
        String user = WXAppletUtils.getUser(map.get("code"));
        JSONObject jsonObject = JSONObject.parseObject(user);
        if(jsonObject.getString("errcode")!=null){
            rb.setFailMsg(ApiResultType.WXAPPLET_LOGIN_ERROR);
        }else{
            //openid 存库  缓存redis  待做
			String openid = jsonObject.getString("openid");
			//查看openid是否存在
			UserLoginRestEntity task = userLoginRestService.findUniqueByProperty(UserLoginRestEntity.class, "wechatAuth",openid);
			if(task==null){
				//执行注册流程
				UserInfoRestEntity uire = new UserInfoRestEntity();
				uire.setWechatAuth(openid);
				int insert = userInfoRestServiceI.insert(uire);
				if(insert>0){
					rb.setSucResult(ApiResultType.OK);
					//返回token  expire
					Map<String,Object> map2 = new HashMap<>();
					map2.put("token","111111");
					map2.put("expire", 30*24*6060*1000);
					rb.setResult(map2);
				}else{
					rb.setFailMsg(ApiResultType.REGISTER_IS_ERROR);
				}
			}else {
				//{"session_key":"i2VyPTkFlFNh8bThTGXShg==","openid":"ojYTl5RhdfPo9hKspMa8sfJ3Fvno"}
				rb.setSucResult(ApiResultType.OK);
				//返回token  expire
				Map<String, Object> map2 = new HashMap<>();
				map2.put("token", "111111");
				map2.put("expire", 30*24*6060*1000);
				rb.setResult(map2);
			}
        }
		return rb;
	}

	@RequestMapping(value = "/login" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean login(@RequestBody Map<String, String> map) {
		return this.login(null,map);
	}

	@RequestMapping(value = "/loginByCode" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean loginByCode(@RequestBody Map<String, String> map) {
		return this.loginByCode(null,map);
	}

	@RequestMapping(value = "/loginByWeChat" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean loginByWeChat(@RequestBody Map<String, String> map) {
		return this.loginByWeChat(null,map);
	}

	@RequestMapping(value = "/loginByWXApplet" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean loginByWXApplet(@RequestBody Map<String, String> map) {
		return this.loginByWXApplet(null,map);
	}

    @RequestMapping(value = "/test" , method = RequestMethod.POST)
    @ResponseBody
    public Map login() {
	    return null;
    }
}
