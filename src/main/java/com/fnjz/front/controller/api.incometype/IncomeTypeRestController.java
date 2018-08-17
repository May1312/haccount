package com.fnjz.front.controller.api.incometype;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.incometype.IncomeTypeRestServiceI;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**   
 * @Title: Controller
 * @Description: 系统收入类目表相关
 * @date 2018-06-06 13:28:45
 * @version V1.0   
 *
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
@Api(description = "android/ios",tags = "公用调用接口")
public class IncomeTypeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(IncomeTypeRestController.class);

	@Autowired
	private IncomeTypeRestServiceI incomeTypeRestService;

}
