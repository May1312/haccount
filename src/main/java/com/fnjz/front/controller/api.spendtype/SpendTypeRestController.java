package com.fnjz.front.controller.api.spendtype;
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

import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.service.api.spendtype.SpendTypeRestServiceI;

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
 * @Description: 系统支出类目表相关
 * @author zhangdaihao
 * @date 2018-06-06 12:01:14
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/spendTypeRestController")
public class SpendTypeRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SpendTypeRestController.class);

	@Autowired
	private SpendTypeRestServiceI spendTypeRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 系统支出类目表相关列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.spendtype/spendTypeRestList");
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
	public void datagrid(SpendTypeRestEntity spendTypeRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(SpendTypeRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, spendTypeRest, request.getParameterMap());
		this.spendTypeRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除系统支出类目表相关
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(SpendTypeRestEntity spendTypeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		spendTypeRest = systemService.getEntity(SpendTypeRestEntity.class, spendTypeRest.getId());
		message = "系统支出类目表相关删除成功";
		spendTypeRestService.delete(spendTypeRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加系统支出类目表相关
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(SpendTypeRestEntity spendTypeRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(spendTypeRest.getId())) {
			message = "系统支出类目表相关更新成功";
			SpendTypeRestEntity t = spendTypeRestService.get(SpendTypeRestEntity.class, spendTypeRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(spendTypeRest, t);
				spendTypeRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "系统支出类目表相关更新失败";
			}
		} else {
			message = "系统支出类目表相关添加成功";
			spendTypeRestService.save(spendTypeRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 系统支出类目表相关列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(SpendTypeRestEntity spendTypeRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(spendTypeRest.getId())) {
			spendTypeRest = spendTypeRestService.getEntity(SpendTypeRestEntity.class, spendTypeRest.getId());
			req.setAttribute("spendTypeRestPage", spendTypeRest);
		}
		return new ModelAndView("com/fnjz/front/api.spendtype/spendTypeRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<SpendTypeRestEntity> list() {
		List<SpendTypeRestEntity> listSpendTypeRests=spendTypeRestService.getList(SpendTypeRestEntity.class);
		return listSpendTypeRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		SpendTypeRestEntity task = spendTypeRestService.get(SpendTypeRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody SpendTypeRestEntity spendTypeRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<SpendTypeRestEntity>> failures = validator.validate(spendTypeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		spendTypeRestService.save(spendTypeRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = spendTypeRest.getId();
		URI uri = uriBuilder.path("/rest/spendTypeRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody SpendTypeRestEntity spendTypeRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<SpendTypeRestEntity>> failures = validator.validate(spendTypeRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		spendTypeRestService.saveOrUpdate(spendTypeRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		spendTypeRestService.deleteEntityById(SpendTypeRestEntity.class, id);
	}
}
