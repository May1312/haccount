package com.fnjz.back.controller.api.useraccountbook;
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

import com.fnjz.back.entity.api.useraccountbook.UserAccountBookEntity;
import com.fnjz.back.service.api.useraccountbook.UserAccountBookServiceI;

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
 * @Description: 移动端用户账本关联表
 * @author zhangdaihao
 * @date 2018-05-30 10:07:47
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/userAccountBookController")
public class UserAccountBookController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserAccountBookController.class);

	@Autowired
	private UserAccountBookServiceI userAccountBookService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 移动端用户账本关联表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/back/api.useraccountbook/userAccountBookList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(UserAccountBookEntity userAccountBook,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(UserAccountBookEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userAccountBook, request.getParameterMap());
		this.userAccountBookService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除移动端用户账本关联表
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(UserAccountBookEntity userAccountBook, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		userAccountBook = systemService.getEntity(UserAccountBookEntity.class, userAccountBook.getId());
		message = "移动端用户账本关联表删除成功";
		userAccountBookService.delete(userAccountBook);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加移动端用户账本关联表
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(UserAccountBookEntity userAccountBook, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(userAccountBook.getId())) {
			message = "移动端用户账本关联表更新成功";
			UserAccountBookEntity t = userAccountBookService.get(UserAccountBookEntity.class, userAccountBook.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(userAccountBook, t);
				userAccountBookService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "移动端用户账本关联表更新失败";
			}
		} else {
			message = "移动端用户账本关联表添加成功";
			userAccountBookService.save(userAccountBook);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 移动端用户账本关联表列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(UserAccountBookEntity userAccountBook, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userAccountBook.getId())) {
			userAccountBook = userAccountBookService.getEntity(UserAccountBookEntity.class, userAccountBook.getId());
			req.setAttribute("userAccountBookPage", userAccountBook);
		}
		return new ModelAndView("com/fnjz/back/api.useraccountbook/userAccountBook");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<UserAccountBookEntity> list() {
		List<UserAccountBookEntity> listUserAccountBooks=userAccountBookService.getList(UserAccountBookEntity.class);
		return listUserAccountBooks;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		UserAccountBookEntity task = userAccountBookService.get(UserAccountBookEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody UserAccountBookEntity userAccountBook, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserAccountBookEntity>> failures = validator.validate(userAccountBook);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userAccountBookService.save(userAccountBook);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = userAccountBook.getId()+"";
		URI uri = uriBuilder.path("/rest/userAccountBookController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody UserAccountBookEntity userAccountBook) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserAccountBookEntity>> failures = validator.validate(userAccountBook);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userAccountBookService.saveOrUpdate(userAccountBook);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		userAccountBookService.deleteEntityById(UserAccountBookEntity.class, id);
	}
}
