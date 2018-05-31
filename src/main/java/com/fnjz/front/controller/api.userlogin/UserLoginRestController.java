package com.fnjz.front.controller.api.userlogin;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.utils.DateUtils;
import com.fnjz.utils.ResdisRestUtils;
import com.fnjz.front.utils.WXAppletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.core.util.MyBeanUtils;

import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.jeecgframework.core.beanvalidator.BeanValidators;

import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

/**   
 * @Title: Controller
 * @Description: 用户登录表相关
 * @date 2018-05-30 22:41:49
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1")
@Api(value = "applogin", description = "移动端登录接口", tags = "applogin")
public class UserLoginRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserLoginRestController.class);

	@Autowired
	private UserLoginRestServiceI userLoginRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;

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
				map2.put("token","111111");
				map2.put("expire", DateUtils.addNDay(30));
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
		String code = ResdisRestUtils.get(ResdisRestUtils.PROFIX_USER_VERIFYCODE_LOGIN + map.get("mobile"), null, 0);
		if(StringUtil.isEmpty(code)){
			//验证码为空
			rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
		}else{
			if(StringUtil.equals(code,map.get("verifycode"))){
				rb.setSucResult(ApiResultType.OK);
				//返回token  expire
				Map<String,Object> map2 = new HashMap<>();
				map2.put("token","111111");
				map2.put("expire", DateUtils.addNDay(30));
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
				map2.put("expire", DateUtils.addNDay(30));
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
	@ApiOperation(value = "微信小程序登录")
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
            //{"session_key":"i2VyPTkFlFNh8bThTGXShg==","openid":"ojYTl5RhdfPo9hKspMa8sfJ3Fvno"}
            rb.setSucResult(ApiResultType.OK);
            //返回token  expire
            Map<String,Object> map2 = new HashMap<>();
            map2.put("token","111111");
            map2.put("expire", DateUtils.addNDay(30));
            rb.setResult(map2);
        }
		return rb;
	}

	@RequestMapping(value = "/login" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean list(@RequestBody Map<String, String> map) {

		return this.login(null,map);
	}



	/**
	 * 添加用户登录表相关
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(UserLoginRestEntity userLoginRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(userLoginRest.getId())) {
			message = "用户登录表相关更新成功";
			UserLoginRestEntity t = userLoginRestService.get(UserLoginRestEntity.class, userLoginRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(userLoginRest, t);
				userLoginRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户登录表相关更新失败";
			}
		} else {
			message = "用户登录表相关添加成功";
			userLoginRestService.save(userLoginRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}


	
	//@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		UserLoginRestEntity task = userLoginRestService.get(UserLoginRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody UserLoginRestEntity userLoginRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserLoginRestEntity>> failures = validator.validate(userLoginRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userLoginRestService.save(userLoginRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = userLoginRest.getId()+"";
		URI uri = uriBuilder.path("/rest/userLoginRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	//@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody UserLoginRestEntity userLoginRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserLoginRestEntity>> failures = validator.validate(userLoginRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userLoginRestService.saveOrUpdate(userLoginRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
