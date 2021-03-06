package com.fnjz.front.controller.api.userfeedback;

import javax.servlet.http.HttpServletRequest;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.userfeedback.UserFeedBackRestEntity;
import com.fnjz.front.utils.EmojiUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.jeecgframework.core.common.controller.BaseController;
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

	@ApiOperation(value = "用户上传反馈意见")
	@RequestMapping(value = "/uploadFeedBack/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean uploadFeedBack(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody UserFeedBackRestEntity userFeedBackRestEntity) {
		System.out.println("登录终端：" + type);
		if(StringUtils.isEmpty(userFeedBackRestEntity.getContent())){
			return new ResultBean(ApiResultType.CONTENT_IS_NULL,null);
		}
		//获取用户详情
		String userInfoId = (String) request.getAttribute("userInfoId");
		if(StringUtils.isNotEmpty(userInfoId)){
			userFeedBackRestEntity.setUserInfoId(Integer.valueOf(userInfoId));
		}
		userFeedBackRestEntity.setStatus("0");//未处理状态
		userFeedBackRestEntity.setCreateDate(new Date());
		//转义emoji表情
		if(StringUtils.isNotEmpty(userFeedBackRestEntity.getContent())){
			//userFeedBackRestEntity.setContent(EmojiUtils.emojiToAlias(userFeedBackRestEntity.getContent()));
			userFeedBackRestEntity.setContent(userFeedBackRestEntity.getContent());
		}
		if(StringUtils.isNotEmpty(userFeedBackRestEntity.getContact())){
			//userFeedBackRestEntity.setContact(EmojiUtils.emojiToAlias(userFeedBackRestEntity.getContact()));
			userFeedBackRestEntity.setContact(userFeedBackRestEntity.getContact());
		}
		try {
			userFeedBackRestService.save(userFeedBackRestEntity);
			return new ResultBean(ApiResultType.OK,null);
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResultBean(ApiResultType.SERVER_ERROR,null);
		}
	}

	@RequestMapping(value = "/uploadFeedBack", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean uploadFeedBack(HttpServletRequest request,@RequestBody UserFeedBackRestEntity userFeedBackRestEntity) {
		return this.uploadFeedBack(null, request,userFeedBackRestEntity);
	}
}
