package com.fnjz.front.controller.api.personalspendtype;
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

import com.fnjz.front.entity.api.personalspendtype.PersonalSpendTypeRestEntity;
import com.fnjz.front.service.api.personalspendtype.PersonalSpendTypeRestServiceI;

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
 * @Description: 用户私有支出类目表相关
 * @date 2018-06-06 11:58:19
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/personalSpendTypeRestController")
public class PersonalSpendTypeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(PersonalSpendTypeRestController.class);

	@Autowired
	private PersonalSpendTypeRestServiceI personalSpendTypeRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 用户私有支出类目表相关列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.personalspendtype/personalSpendTypeRestList");
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
	public void datagrid(PersonalSpendTypeRestEntity personalSpendTypeRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(PersonalSpendTypeRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, personalSpendTypeRest, request.getParameterMap());
		this.personalSpendTypeRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除用户私有支出类目表相关
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(PersonalSpendTypeRestEntity personalSpendTypeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		personalSpendTypeRest = systemService.getEntity(PersonalSpendTypeRestEntity.class, personalSpendTypeRest.getId());
		message = "用户私有支出类目表相关删除成功";
		personalSpendTypeRestService.delete(personalSpendTypeRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加用户私有支出类目表相关
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(PersonalSpendTypeRestEntity personalSpendTypeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(personalSpendTypeRest.getId())) {
			message = "用户私有支出类目表相关更新成功";
			PersonalSpendTypeRestEntity t = personalSpendTypeRestService.get(PersonalSpendTypeRestEntity.class, personalSpendTypeRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(personalSpendTypeRest, t);
				personalSpendTypeRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户私有支出类目表相关更新失败";
			}
		} else {
			message = "用户私有支出类目表相关添加成功";
			personalSpendTypeRestService.save(personalSpendTypeRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 用户私有支出类目表相关列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(PersonalSpendTypeRestEntity personalSpendTypeRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(personalSpendTypeRest.getId())) {
			personalSpendTypeRest = personalSpendTypeRestService.getEntity(PersonalSpendTypeRestEntity.class, personalSpendTypeRest.getId());
			req.setAttribute("personalSpendTypeRestPage", personalSpendTypeRest);
		}
		return new ModelAndView("com/fnjz/front/api.personalspendtype/personalSpendTypeRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<PersonalSpendTypeRestEntity> list() {
		List<PersonalSpendTypeRestEntity> listPersonalSpendTypeRests=personalSpendTypeRestService.getList(PersonalSpendTypeRestEntity.class);
		return listPersonalSpendTypeRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		PersonalSpendTypeRestEntity task = personalSpendTypeRestService.get(PersonalSpendTypeRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody PersonalSpendTypeRestEntity personalSpendTypeRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<PersonalSpendTypeRestEntity>> failures = validator.validate(personalSpendTypeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		personalSpendTypeRestService.save(personalSpendTypeRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = personalSpendTypeRest.getId();
		URI uri = uriBuilder.path("/rest/personalSpendTypeRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody PersonalSpendTypeRestEntity personalSpendTypeRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<PersonalSpendTypeRestEntity>> failures = validator.validate(personalSpendTypeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		personalSpendTypeRestService.saveOrUpdate(personalSpendTypeRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		personalSpendTypeRestService.deleteEntityById(PersonalSpendTypeRestEntity.class, id);
	}
}
