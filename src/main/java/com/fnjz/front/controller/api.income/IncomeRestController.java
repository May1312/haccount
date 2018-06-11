package com.fnjz.front.controller.api.income;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.fnjz.front.entity.api.income.IncomeRestEntity;
import com.fnjz.front.service.api.income.IncomeRestServiceI;
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
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

/**   
 * @Title: Controller
 * @Description: 账本-收入表相关
 * @date 2018-06-06 13:27:56
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/incomeRestController")
public class IncomeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(IncomeRestController.class);

	@Autowired
	private IncomeRestServiceI incomeRestService;
	@Autowired
	private SystemService systemService;


	/**
	 * 删除账本-收入表相关
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(IncomeRestEntity incomeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		incomeRest = systemService.getEntity(IncomeRestEntity.class, incomeRest.getId());
		message = "账本-收入表相关删除成功";
		incomeRestService.delete(incomeRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}

	/**
	 * 添加账本-收入表相关
	 *
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(IncomeRestEntity incomeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(incomeRest.getId())) {
			message = "账本-收入表相关更新成功";
			IncomeRestEntity t = incomeRestService.get(IncomeRestEntity.class, incomeRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(incomeRest, t);
				incomeRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "账本-收入表相关更新失败";
			}
		} else {
			message = "账本-收入表相关添加成功";
			incomeRestService.save(incomeRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}
}
