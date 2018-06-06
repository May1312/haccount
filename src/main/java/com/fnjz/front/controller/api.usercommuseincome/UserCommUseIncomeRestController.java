package com.fnjz.front.controller.api.usercommuseincome;
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

import com.fnjz.front.entity.api.usercommuseincome.UserCommUseIncomeRestEntity;
import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;

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
 * @Description: 用户常用收入类目表
 * @author zhangdaihao
 * @date 2018-06-06 13:24:06
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/userCommUseIncomeRestController")
public class UserCommUseIncomeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserCommUseIncomeRestController.class);

	@Autowired
	private UserCommUseIncomeRestServiceI userCommUseIncomeRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 用户常用收入类目表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.usercommuseincome/userCommUseIncomeRestList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(UserCommUseIncomeRestEntity userCommUseIncomeRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(UserCommUseIncomeRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userCommUseIncomeRest, request.getParameterMap());
		this.userCommUseIncomeRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除用户常用收入类目表
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(UserCommUseIncomeRestEntity userCommUseIncomeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		userCommUseIncomeRest = systemService.getEntity(UserCommUseIncomeRestEntity.class, userCommUseIncomeRest.getId());
		message = "用户常用收入类目表删除成功";
		userCommUseIncomeRestService.delete(userCommUseIncomeRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加用户常用收入类目表
	 *
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(UserCommUseIncomeRestEntity userCommUseIncomeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(userCommUseIncomeRest.getId())) {
			message = "用户常用收入类目表更新成功";
			UserCommUseIncomeRestEntity t = userCommUseIncomeRestService.get(UserCommUseIncomeRestEntity.class, userCommUseIncomeRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(userCommUseIncomeRest, t);
				userCommUseIncomeRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户常用收入类目表更新失败";
			}
		} else {
			message = "用户常用收入类目表添加成功";
			userCommUseIncomeRestService.save(userCommUseIncomeRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 用户常用收入类目表列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(UserCommUseIncomeRestEntity userCommUseIncomeRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userCommUseIncomeRest.getId())) {
			userCommUseIncomeRest = userCommUseIncomeRestService.getEntity(UserCommUseIncomeRestEntity.class, userCommUseIncomeRest.getId());
			req.setAttribute("userCommUseIncomeRestPage", userCommUseIncomeRest);
		}
		return new ModelAndView("com/fnjz/front/api.usercommuseincome/userCommUseIncomeRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<UserCommUseIncomeRestEntity> list() {
		List<UserCommUseIncomeRestEntity> listUserCommUseIncomeRests=userCommUseIncomeRestService.getList(UserCommUseIncomeRestEntity.class);
		return listUserCommUseIncomeRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		UserCommUseIncomeRestEntity task = userCommUseIncomeRestService.get(UserCommUseIncomeRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody UserCommUseIncomeRestEntity userCommUseIncomeRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserCommUseIncomeRestEntity>> failures = validator.validate(userCommUseIncomeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userCommUseIncomeRestService.save(userCommUseIncomeRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = userCommUseIncomeRest.getId()+"";
		URI uri = uriBuilder.path("/rest/userCommUseIncomeRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody UserCommUseIncomeRestEntity userCommUseIncomeRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserCommUseIncomeRestEntity>> failures = validator.validate(userCommUseIncomeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userCommUseIncomeRestService.saveOrUpdate(userCommUseIncomeRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		userCommUseIncomeRestService.deleteEntityById(UserCommUseIncomeRestEntity.class, id);
	}
}
