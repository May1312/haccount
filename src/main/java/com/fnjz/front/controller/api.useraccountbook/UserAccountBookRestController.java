package com.fnjz.front.controller.api.useraccountbook;
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

import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.useraccountbook.UserAccountBookRestServiceI;

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
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 用户账本关联表相关列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.useraccountbook/userAccountBookRestList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(UserAccountBookRestEntity userAccountBookRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(UserAccountBookRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userAccountBookRest, request.getParameterMap());
		this.userAccountBookRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除用户账本关联表相关
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(UserAccountBookRestEntity userAccountBookRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		userAccountBookRest = systemService.getEntity(UserAccountBookRestEntity.class, userAccountBookRest.getId());
		message = "用户账本关联表相关删除成功";
		userAccountBookRestService.delete(userAccountBookRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加用户账本关联表相关
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(UserAccountBookRestEntity userAccountBookRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(userAccountBookRest.getId())) {
			message = "用户账本关联表相关更新成功";
			UserAccountBookRestEntity t = userAccountBookRestService.get(UserAccountBookRestEntity.class, userAccountBookRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(userAccountBookRest, t);
				userAccountBookRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户账本关联表相关更新失败";
			}
		} else {
			message = "用户账本关联表相关添加成功";
			userAccountBookRestService.save(userAccountBookRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 用户账本关联表相关列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(UserAccountBookRestEntity userAccountBookRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userAccountBookRest.getId())) {
			userAccountBookRest = userAccountBookRestService.getEntity(UserAccountBookRestEntity.class, userAccountBookRest.getId());
			req.setAttribute("userAccountBookRestPage", userAccountBookRest);
		}
		return new ModelAndView("com/fnjz/front/api.useraccountbook/userAccountBookRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<UserAccountBookRestEntity> list() {
		List<UserAccountBookRestEntity> listUserAccountBookRests=userAccountBookRestService.getList(UserAccountBookRestEntity.class);
		return listUserAccountBookRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		UserAccountBookRestEntity task = userAccountBookRestService.get(UserAccountBookRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody UserAccountBookRestEntity userAccountBookRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserAccountBookRestEntity>> failures = validator.validate(userAccountBookRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userAccountBookRestService.save(userAccountBookRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = userAccountBookRest.getId()+"";
		URI uri = uriBuilder.path("/rest/userAccountBookRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody UserAccountBookRestEntity userAccountBookRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserAccountBookRestEntity>> failures = validator.validate(userAccountBookRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userAccountBookRestService.saveOrUpdate(userAccountBookRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		userAccountBookRestService.deleteEntityById(UserAccountBookRestEntity.class, id);
	}
}
