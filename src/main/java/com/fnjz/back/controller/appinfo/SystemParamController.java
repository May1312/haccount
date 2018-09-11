package com.fnjz.back.controller.appinfo;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.utils.RedisTemplateUtils;
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

import com.fnjz.back.entity.appinfo.SystemParamEntity;
import com.fnjz.back.service.appinfo.SystemParamServiceI;

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
 * @Description: 系统参数控制表
 * @author zhangdaihao
 * @date 2018-09-10 11:10:12
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/systemParamController")
public class SystemParamController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SystemParamController.class);

	@Autowired
	private SystemParamServiceI systemParamService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	
	@Autowired
	private RedisTemplateUtils redisTemplateUtils;

	/**
	 * 系统参数控制表列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/back/appinfo/systemParamList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(SystemParamEntity systemParam,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(SystemParamEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, systemParam, request.getParameterMap());
		this.systemParamService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除系统参数控制表
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(SystemParamEntity systemParam, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		systemParam = systemService.getEntity(SystemParamEntity.class, systemParam.getId());
		message = "系统参数控制表删除成功";
		systemParamService.delete(systemParam);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加系统参数控制表
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(SystemParamEntity systemParam, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(systemParam.getId())) {
			message = "系统参数控制表更新成功";
			SystemParamEntity t = systemParamService.get(SystemParamEntity.class, systemParam.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(systemParam, t);
				systemParamService.saveOrUpdate(t);

				//清除缓存
				redisTemplateUtils.deleteKey(RedisPrefix.SYS_SPEND_LABEL_TYPE);
				redisTemplateUtils.deleteKey(RedisPrefix.SYS_INCOME_LABEL_TYPE);

				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "系统参数控制表更新失败";
			}
		} else {
			message = "系统参数控制表添加成功";
			systemParamService.save(systemParam);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 系统参数控制表列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(SystemParamEntity systemParam, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(systemParam.getId())) {
			systemParam = systemParamService.getEntity(SystemParamEntity.class, systemParam.getId());
			req.setAttribute("systemParamPage", systemParam);
		}
		return new ModelAndView("com/fnjz/back/appinfo/systemParam");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<SystemParamEntity> list() {
		List<SystemParamEntity> listSystemParams=systemParamService.getList(SystemParamEntity.class);
		return listSystemParams;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		SystemParamEntity task = systemParamService.get(SystemParamEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody SystemParamEntity systemParam, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<SystemParamEntity>> failures = validator.validate(systemParam);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		systemParamService.save(systemParam);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id =String.valueOf(systemParam.getId());
		URI uri = uriBuilder.path("/rest/systemParamController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody SystemParamEntity systemParam) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<SystemParamEntity>> failures = validator.validate(systemParam);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		systemParamService.saveOrUpdate(systemParam);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		systemParamService.deleteEntityById(SystemParamEntity.class, id);
	}
}
