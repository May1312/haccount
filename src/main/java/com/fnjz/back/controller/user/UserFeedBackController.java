package com.fnjz.back.controller.user;

import com.fnjz.back.entity.user.UserFeedBackEntity;
import com.fnjz.back.service.user.UserFeedBackServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.beanvalidator.BeanValidators;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**   
 * @Title: Controller
 * @Description: 用户反馈
 * @author zhangdaihao
 * @date 2018-05-29 15:26:38
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/userFeedBackController")
public class UserFeedBackController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserFeedBackController.class);

	@Autowired
	private UserFeedBackServiceI userFeedBackService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 用户反馈列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/back/user/userFeedBackList");
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
	public void datagrid(UserFeedBackEntity userFeedBack,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(UserFeedBackEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userFeedBack, request.getParameterMap());
		this.userFeedBackService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除用户反馈
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(UserFeedBackEntity userFeedBack, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		userFeedBack = systemService.getEntity(UserFeedBackEntity.class, userFeedBack.getId());
		message = "用户反馈删除成功";
		userFeedBackService.delete(userFeedBack);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加用户反馈
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(UserFeedBackEntity userFeedBack, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(userFeedBack.getId())) {
			message = "用户反馈更新成功";
			UserFeedBackEntity t = userFeedBackService.get(UserFeedBackEntity.class, userFeedBack.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(userFeedBack, t);
				userFeedBackService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "用户反馈更新失败";
			}
		} else {
			message = "用户反馈添加成功";
			userFeedBackService.save(userFeedBack);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 用户反馈列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(UserFeedBackEntity userFeedBack, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(userFeedBack.getId())) {
			userFeedBack = userFeedBackService.getEntity(UserFeedBackEntity.class, userFeedBack.getId());
			req.setAttribute("userFeedBackPage", userFeedBack);
		}
		return new ModelAndView("com/fnjz/back/user/userFeedBack");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<UserFeedBackEntity> list() {
		List<UserFeedBackEntity> listUserFeedBacks=userFeedBackService.getList(UserFeedBackEntity.class);
		return listUserFeedBacks;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		UserFeedBackEntity task = userFeedBackService.get(UserFeedBackEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody UserFeedBackEntity userFeedBack, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserFeedBackEntity>> failures = validator.validate(userFeedBack);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userFeedBackService.save(userFeedBack);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = userFeedBack.getId();
		URI uri = uriBuilder.path("/rest/userFeedBackController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody UserFeedBackEntity userFeedBack) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<UserFeedBackEntity>> failures = validator.validate(userFeedBack);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		userFeedBackService.saveOrUpdate(userFeedBack);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		userFeedBackService.deleteEntityById(UserFeedBackEntity.class, id);
	}
}