package com.fnjz.front.controller.api.userfeedback;

import javax.servlet.http.HttpServletRequest;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.userfeedback.UserFeedBackRestEntity;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.web.system.service.SystemService;
import com.fnjz.front.service.api.userfeedback.UserFeedBackRestServiceI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**   
 * @Title: Controller
 * @Description: 用户反馈表相关
 * @date 2018-06-26 10:32:33
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1/")
public class UserFeedBackRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserFeedBackRestController.class);

	@Autowired
	private UserFeedBackRestServiceI userFeedBackRestService;
	@Autowired
	private SystemService systemService;

	@ApiOperation(value = "用户上传反馈意见")
	@RequestMapping(value = "/uploadFeedBack/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean uploadFeedBack(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody UserFeedBackRestEntity userFeedBackRestEntity) {
		System.out.println("登录终端：" + type);
		ResultBean rb = new ResultBean();
		if(StringUtils.isEmpty(userFeedBackRestEntity.getContent())){
			rb.setFailMsg(ApiResultType.CONTENT_IS_NULL);
			return rb;
		}
		//获取用户详情
		String userInfoId = (String) request.getAttribute("userInfoId");
		if(StringUtils.isNotEmpty(userInfoId)){
			userFeedBackRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
		}
		userFeedBackRestEntity.setStatus("0");//未处理状态
		userFeedBackRestEntity.setCreateDate(new Date());
		try {
			userFeedBackRestService.save(userFeedBackRestEntity);
			rb.setSucResult(ApiResultType.OK);
			return rb;
		} catch (Exception e) {
			logger.error(e.toString());
			rb.setFailMsg(ApiResultType.SERVER_ERROR);
			return rb;
		}
	}

	@RequestMapping(value = "/uploadFeedBack", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean uploadFeedBack(HttpServletRequest request,@RequestBody UserFeedBackRestEntity userFeedBackRestEntity) {
		return this.uploadFeedBack(null, request,userFeedBackRestEntity);
	}
}
