package com.fnjz.front.controller.api.accountbooktype;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.accountbook.AccountBookRestDTO;
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

import com.fnjz.front.entity.api.accountbooktype.AccountBookTypeRestEntity;
import com.fnjz.front.service.api.accountbooktype.AccountBookTypeRestServiceI;

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

	@RequestMapping(value = "/getABTypeAll", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean getABTypeAll() {
		return this.getABTypeAll(null);
	}
}
