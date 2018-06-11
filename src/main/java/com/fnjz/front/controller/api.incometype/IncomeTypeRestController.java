package com.fnjz.front.controller.api.incometype;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.web.system.service.SystemService;

import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.service.api.incometype.IncomeTypeRestServiceI;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.validation.Validator;

/**   
 * @Title: Controller
 * @Description: 系统收入类目表相关
 * @date 2018-06-06 13:28:45
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1")
@Api(description = "android/ios",tags = "公用调用接口")
public class IncomeTypeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(IncomeTypeRestController.class);

	@Autowired
	private IncomeTypeRestServiceI incomeTypeRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;


	@ApiOperation(value = "查询手机号是否注册")
	@RequestMapping(value = "/listIncomeType" , method = RequestMethod.GET)
	@ResponseBody
	public List<IncomeTypeRestEntity> list() {
		List<IncomeTypeRestEntity> listIncomeTypeRests=incomeTypeRestService.getList(IncomeTypeRestEntity.class);
		return listIncomeTypeRests;
	}

}
