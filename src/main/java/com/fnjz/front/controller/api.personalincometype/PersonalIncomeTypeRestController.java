package com.fnjz.front.controller.api.personalincometype;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.fnjz.front.entity.api.personalincometype.PersonalIncomeTypeRestEntity;
import com.fnjz.front.service.api.personalincometype.PersonalIncomeTypeRestServiceI;

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
 * @Description: 用户私有收入类目表相关
 * @date 2018-06-06 11:55:28
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/personalIncomeTypeRestController")
public class PersonalIncomeTypeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(PersonalIncomeTypeRestController.class);

	@Autowired
	private PersonalIncomeTypeRestServiceI personalIncomeTypeRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 用户私有收入类目表相关列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.personalincometype/personalIncomeTypeRestList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param user
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(PersonalIncomeTypeRestEntity personalIncomeTypeRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(PersonalIncomeTypeRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, personalIncomeTypeRest, request.getParameterMap());
		this.personalIncomeTypeRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除用户私有收入类目表相关
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(PersonalIncomeTypeRestEntity personalIncomeTypeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		personalIncomeTypeRest = systemService.getEntity(PersonalIncomeTypeRestEntity.class, personalIncomeTypeRest.getId());
		message = "用户私有收入类目表相关删除成功";
		personalIncomeTypeRestService.delete(personalIncomeTypeRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加用户私有收入类目表相关
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(PersonalIncomeTypeRestEntity personalIncomeTypeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(personalIncomeTypeRest.getId())) {
			message = "用户私有收入类目表相关更新成功";
			PersonalIncomeTypeRestEntity t = personalIncomeTypeRestService.get(PersonalIncomeTypeRestEntity.class, personalIncomeTypeRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(personalIncomeTypeRest, t);
				personalIncomeTypeRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户私有收入类目表相关更新失败";
			}
		} else {
			message = "用户私有收入类目表相关添加成功";
			personalIncomeTypeRestService.save(personalIncomeTypeRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 用户私有收入类目表相关列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(PersonalIncomeTypeRestEntity personalIncomeTypeRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(personalIncomeTypeRest.getId())) {
			personalIncomeTypeRest = personalIncomeTypeRestService.getEntity(PersonalIncomeTypeRestEntity.class, personalIncomeTypeRest.getId());
			req.setAttribute("personalIncomeTypeRestPage", personalIncomeTypeRest);
		}
		return new ModelAndView("com/fnjz/front/api.personalincometype/personalIncomeTypeRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<PersonalIncomeTypeRestEntity> list() {
		List<PersonalIncomeTypeRestEntity> listPersonalIncomeTypeRests=personalIncomeTypeRestService.getList(PersonalIncomeTypeRestEntity.class);
		return listPersonalIncomeTypeRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		PersonalIncomeTypeRestEntity task = personalIncomeTypeRestService.get(PersonalIncomeTypeRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody PersonalIncomeTypeRestEntity personalIncomeTypeRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<PersonalIncomeTypeRestEntity>> failures = validator.validate(personalIncomeTypeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		personalIncomeTypeRestService.save(personalIncomeTypeRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = personalIncomeTypeRest.getId();
		URI uri = uriBuilder.path("/rest/personalIncomeTypeRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody PersonalIncomeTypeRestEntity personalIncomeTypeRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<PersonalIncomeTypeRestEntity>> failures = validator.validate(personalIncomeTypeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		personalIncomeTypeRestService.saveOrUpdate(personalIncomeTypeRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		personalIncomeTypeRestService.deleteEntityById(PersonalIncomeTypeRestEntity.class, id);
	}
}
