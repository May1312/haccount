package com.fnjz.front.controller.api.apps;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.core.util.MyBeanUtils;

import com.fnjz.front.entity.api.apps.AppsRestEntity;
import com.fnjz.front.service.api.apps.AppsRestServiceI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
 * @Description: app版本管理表相关
 * @date 2018-06-26 13:11:13
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/api/v1")
public class AppsRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AppsRestController.class);

	@Autowired
	private AppsRestServiceI appsRestService;
	@Autowired
	private SystemService systemService;

	@ApiOperation(value = "app检查更新")
	@RequestMapping(value = "/appCheck/{type}", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean appCheck (@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String
									  type, HttpServletRequest request,@RequestBody Map<String,String> map){
		System.out.println("登录终端：" + type);
		ResultBean rb = new ResultBean();
		int flag;
		if(StringUtils.equals("","")){

		}
		try {

		} catch (Exception e) {
			logger.error(e.toString());
			rb.setFailMsg(ApiResultType.SERVER_ERROR);
			return rb;
		}
		return rb;
	}

}
