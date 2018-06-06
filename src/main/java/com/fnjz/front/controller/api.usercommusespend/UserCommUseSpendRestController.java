package com.fnjz.front.controller.api.usercommusespend;
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

import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import com.fnjz.front.service.api.usercommusespend.UserCommUseSpendRestServiceI;

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
 * @Description: 用户常用支出类目表
 * @author zhangdaihao
 * @date 2018-06-06 13:25:22
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/userCommUseSpendRestController")
public class UserCommUseSpendRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserCommUseSpendRestController.class);

	@Autowired
	private UserCommUseSpendRestServiceI userCommUseSpendRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 用户常用支出类目表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.usercommusespend/userCommUseSpendRestList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(UserCommUseSpendRestEntity userCommUseSpendRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(UserCommUseSpendRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userCommUseSpendRest, request.getParameterMap());
		this.userCommUseSpendRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除用户常用支出类目表
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(UserCommUseSpendRestEntity userCommUseSpendRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		userCommUseSpendRest = systemService.getEntity(UserCommUseSpendRestEntity.class, userCommUseSpendRest.getId());
		message = "用户常用支出类目表删除成功";
		userCommUseSpendRestService.delete(userCommUseSpendRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加用户常用支出类目表
	 *
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(UserCommUseSpendRestEntity userCommUseSpendRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(userCommUseSpendRest.getId())) {
			message = "用户常用支出类目表更新成功";
			UserCommUseSpendRestEntity t = userCommUseSpendRestService.get(UserCommUseSpendRestEntity.class, userCommUseSpendRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(userCommUseSpendRest, t);
				userCommUseSpendRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户常用支出类目表更新失败";
			}
		} else {
			message = "用户常用支出类目表添加成功";
			userCommUseSpendRestService.save(userCommUseSpendRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 用户常用支出类目表列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(UserCommUseSpendRestEntity userCommUseSpendRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userCommUseSpendRest.getId())) {
			userCommUseSpendRest = userCommUseSpendRestService.getEntity(UserCommUseSpendRestEntity.class, userCommUseSpendRest.getId());
			req.setAttribute("userCommUseSpendRestPage", userCommUseSpendRest);
		}
		return new ModelAndView("com/fnjz/front/api.usercommusespend/userCommUseSpendRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<UserCommUseSpendRestEntity> list() {
		List<UserCommUseSpendRestEntity> listUserCommUseSpendRests=userCommUseSpendRestService.getList(UserCommUseSpendRestEntity.class);
		return listUserCommUseSpendRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		UserCommUseSpendRestEntity task = userCommUseSpendRestService.get(UserCommUseSpendRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody UserCommUseSpendRestEntity userCommUseSpendRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserCommUseSpendRestEntity>> failures = validator.validate(userCommUseSpendRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userCommUseSpendRestService.save(userCommUseSpendRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = userCommUseSpendRest.getId()+"";
		URI uri = uriBuilder.path("/rest/userCommUseSpendRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody UserCommUseSpendRestEntity userCommUseSpendRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserCommUseSpendRestEntity>> failures = validator.validate(userCommUseSpendRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userCommUseSpendRestService.saveOrUpdate(userCommUseSpendRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		userCommUseSpendRestService.deleteEntityById(UserCommUseSpendRestEntity.class, id);
	}
}
