package com.fnjz.front.controller.api.useraccountbook;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.jeecgframework.core.common.controller.BaseController;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**   
 * @Title: Controller
 * @Description: 用户账本关联表相关
 * @author zhangdaihao
 * @date 2018-05-30 14:07:36
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/userAccountBookRestController")
public class UserAccountBookRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserAccountBookRestController.class);

	@Autowired
	private UserAccountBookRestServiceI userAccountBookRestService;

	/**
	 * 功能描述: 判断用户是否已经加入此账本
	 *
	 * @param:
	 * @return:
	 * @auther: yonghuizhao
	 * @date: 2018/11/19 16:41
	 */
	@RequestMapping(value = "/checkUserisExistAccount/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean checkUserisExistAccount(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,Object> map) {
		String userInfoId  = (String) request.getAttribute("userInfoId");
		//账本id
		String accountBookId =String.valueOf(map.get("accountBookId"));
		UserAccountBookRestEntity userAccountBookRestEntity = userAccountBookRestService.checkUserisExistAccount(userInfoId, accountBookId);
		if (userAccountBookRestEntity != null){
			return new ResultBean(ApiResultType.OK,"YES");
		}else {
			return new ResultBean(ApiResultType.OK,"NO");
		}
	}
}
