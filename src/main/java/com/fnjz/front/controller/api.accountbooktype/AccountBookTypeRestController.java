package com.fnjz.front.controller.api.accountbooktype;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.accountbooktype.AccountBookTypeRestEntity;
import com.fnjz.front.service.api.accountbooktype.AccountBookTypeRestServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**   
 * @Title: Controller
 * @Description: 账本类型相关
 * @author zhangdaihao
 * @date 2018-11-10 16:44:41
 * @version V1.0   
 *
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class AccountBookTypeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AccountBookTypeRestController.class);

	@Autowired
	private AccountBookTypeRestServiceI accountBookTypeRestService;

	/**
	 * 获取所有账本类型
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/getABTypeAll/{type}", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getABTypeAll(@PathVariable("type") String type) {
		System.out.println(type);
		try {
			List<AccountBookTypeRestEntity> list = accountBookTypeRestService.getABTypeAll();
			return new ResultBean(ApiResultType.OK,list);
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResultBean(ApiResultType.SERVER_ERROR, null);
		}
	}

	/**
	 * 获取用户已有的账本类型
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/getHadABType/{type}", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getHadABType(@PathVariable("type") String type,HttpServletRequest request) {
		System.out.println(type);
		String userInfoId = (String) request.getAttribute("userInfoId");
		try {
			List<AccountBookTypeRestEntity> list = accountBookTypeRestService.getHadABType(userInfoId);
			return new ResultBean(ApiResultType.OK,list);
		} catch (Exception e) {
			logger.error(e.toString());
			return new ResultBean(ApiResultType.SERVER_ERROR, null);
		}
	}

	@RequestMapping(value = "/getABTypeAll", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getABTypeAll() {
		return this.getABTypeAll(null);
	}

	@RequestMapping(value = "/getHadABType", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getHadABType(HttpServletRequest request) {
		return this.getHadABType(null,request);
	}
}
