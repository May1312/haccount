package com.fnjz.front.controller.appinfo;
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

import com.fnjz.front.entity.appinfo.AppVersionRestEntity;
import com.fnjz.front.service.appinfo.AppVersionRestServiceI;

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
 * @Description: app版本升级
 * @author zhangdaihao
 * @date 2018-05-29 14:06:55
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/appVersionRestController")
public class AppVersionRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AppVersionRestController.class);

	@Autowired
	private AppVersionRestServiceI appVersionRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * app版本升级列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/appinfo/appVersionRestList");
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
	public void datagrid(AppVersionRestEntity appVersionRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(AppVersionRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, appVersionRest, request.getParameterMap());
		this.appVersionRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除app版本升级
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(AppVersionRestEntity appVersionRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		appVersionRest = systemService.getEntity(AppVersionRestEntity.class, appVersionRest.getId());
		message = "app版本升级删除成功";
		appVersionRestService.delete(appVersionRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加app版本升级
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(AppVersionRestEntity appVersionRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(appVersionRest.getId())) {
			message = "app版本升级更新成功";
			AppVersionRestEntity t = appVersionRestService.get(AppVersionRestEntity.class, appVersionRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(appVersionRest, t);
				appVersionRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "app版本升级更新失败";
			}
		} else {
			message = "app版本升级添加成功";
			appVersionRestService.save(appVersionRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * app版本升级列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(AppVersionRestEntity appVersionRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(appVersionRest.getId())) {
			appVersionRest = appVersionRestService.getEntity(AppVersionRestEntity.class, appVersionRest.getId());
			req.setAttribute("appVersionRestPage", appVersionRest);
		}
		return new ModelAndView("com/fnjz/front/appinfo/appVersionRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<AppVersionRestEntity> list() {
		List<AppVersionRestEntity> listAppVersionRests=appVersionRestService.getList(AppVersionRestEntity.class);
		return listAppVersionRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		AppVersionRestEntity task = appVersionRestService.get(AppVersionRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody AppVersionRestEntity appVersionRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<AppVersionRestEntity>> failures = validator.validate(appVersionRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		appVersionRestService.save(appVersionRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = appVersionRest.getId();
		URI uri = uriBuilder.path("/rest/appVersionRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody AppVersionRestEntity appVersionRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<AppVersionRestEntity>> failures = validator.validate(appVersionRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		appVersionRestService.saveOrUpdate(appVersionRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		appVersionRestService.deleteEntityById(AppVersionRestEntity.class, id);
	}
}
