package com.fnjz.front.controller.api.userintegral;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**   
 * @Title: Controller
 * @Description: 用户积分流水表相关
 * @author zhangdaihao
 * @date 2018-10-12 11:31:58
 * @version V1.0   
 *
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserIntegralRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserIntegralRestController.class);

	@Autowired
	private UserIntegralRestServiceI userIntegralRestServiceI;

	/**
	 * 领取签到积分接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value = {"/signInIntegral", "/signInIntegral/{type}"}, method = RequestMethod.POST)
	@ResponseBody
	public ResultBean integral(HttpServletRequest request, @RequestBody Map<String,String> map) {
		String userInfoId = (String) request.getAttribute("userInfoId");
		String shareCode = (String) request.getAttribute("shareCode");
		//类型不为null情况下
		if(map.size()>0){
			try {
				userIntegralRestServiceI.integral(userInfoId, map);
			} catch (Exception e) {
				logger.error(e.toString());
				return new ResultBean(ApiResultType.SERVER_ERROR, null);
			}
		}
		return new ResultBean(ApiResultType.OK, null);
	}

}
