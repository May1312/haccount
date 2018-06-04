package com.fnjz.front.controller.api.userinfo;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.util.StringUtil;
import com.fnjz.front.service.api.userinfo.UserInfoRestServiceI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Title: Controller
 * @Description: 用户详情表相关
 * @date 2018-05-30 14:05:50
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1")
@Api(value = "app_user_info", description = "移动端----->账户安全相关接口", tags = "app_user_info")
public class UserInfoRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserInfoRestController.class);

	@Autowired
	private UserInfoRestServiceI userInfoRestService;

	@Autowired
	private UserLoginRestServiceI userLoginRestServiceI;

	@Autowired
	private RedisTemplate redisTemplate;

	@ApiOperation(value = "绑定手机号")
	@RequestMapping(value = "/bindMobile/{type}" , method = RequestMethod.POST)
	@ResponseBody
	public ResultBean login(@PathVariable("type") String type, @RequestBody Map<String, String> map, HttpServletRequest request) {
		System.out.println("登录终端：" + type);
		ResultBean rb = new ResultBean();
		//判断手机号
		if (StringUtil.isEmpty(map.get("mobile")) || StringUtil.isEmpty(map.get("verifycode"))) {
			rb.setFailMsg(ApiResultType.USERNAME_OR_VERIFYCODE_ISNULL);
			return rb;
		}
		//获取验证码
		String code = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_VERIFYCODE_BIND_MOBILE + map.get("mobile"));
		if (StringUtils.isEmpty(code)) {
			//验证码为空
			rb.setFailMsg(ApiResultType.VERIFYCODE_TIME_OUT);
			return rb;
		}
		if (StringUtil.equals(code, map.get("verifycode"))) {
			UserLoginRestEntity task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "mobile", map.get("mobile"));
			//执行更新手机号流程
			String userInfoId = (String) request.getAttribute("userInfoId");
			int i = userInfoRestService.updateMobile(userInfoId, map.get("mobile"));
			if (i < 1) {
				rb.setFailMsg(ApiResultType.PASSWORD_UPDATE_ERROR);
				return rb;
			}
			rb.setSucResult(ApiResultType.OK);
		}
		return rb;
	}
}
