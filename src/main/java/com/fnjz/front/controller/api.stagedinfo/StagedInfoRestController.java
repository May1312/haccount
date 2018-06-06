package com.fnjz.front.controller.api.stagedinfo;
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

import com.fnjz.front.entity.api.stagedinfo.StagedInfoRestEntity;
import com.fnjz.front.service.api.stagedinfo.StagedInfoRestServiceI;

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
 * @Description: 支出关联分期详情表相关
 * @author zhangdaihao
 * @date 2018-06-06 13:21:00
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/stagedInfoRestController")
public class StagedInfoRestController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(StagedInfoRestController.class);

	@Autowired
	private StagedInfoRestServiceI stagedInfoRestService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 支出关联分期详情表相关列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/front/api.stagedinfo/stagedInfoRestList");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(StagedInfoRestEntity stagedInfoRest,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(StagedInfoRestEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, stagedInfoRest, request.getParameterMap());
		this.stagedInfoRestService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除支出关联分期详情表相关
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(StagedInfoRestEntity stagedInfoRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		stagedInfoRest = systemService.getEntity(StagedInfoRestEntity.class, stagedInfoRest.getId());
		message = "支出关联分期详情表相关删除成功";
		stagedInfoRestService.delete(stagedInfoRest);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加支出关联分期详情表相关
	 *
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(StagedInfoRestEntity stagedInfoRest, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(stagedInfoRest.getId())) {
			message = "支出关联分期详情表相关更新成功";
			StagedInfoRestEntity t = stagedInfoRestService.get(StagedInfoRestEntity.class, stagedInfoRest.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(stagedInfoRest, t);
				stagedInfoRestService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "支出关联分期详情表相关更新失败";
			}
		} else {
			message = "支出关联分期详情表相关添加成功";
			stagedInfoRestService.save(stagedInfoRest);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 支出关联分期详情表相关列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(StagedInfoRestEntity stagedInfoRest, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(stagedInfoRest.getId())) {
			stagedInfoRest = stagedInfoRestService.getEntity(StagedInfoRestEntity.class, stagedInfoRest.getId());
			req.setAttribute("stagedInfoRestPage", stagedInfoRest);
		}
		return new ModelAndView("com/fnjz/front/api.stagedinfo/stagedInfoRest");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<StagedInfoRestEntity> list() {
		List<StagedInfoRestEntity> listStagedInfoRests=stagedInfoRestService.getList(StagedInfoRestEntity.class);
		return listStagedInfoRests;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		StagedInfoRestEntity task = stagedInfoRestService.get(StagedInfoRestEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody StagedInfoRestEntity stagedInfoRest, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<StagedInfoRestEntity>> failures = validator.validate(stagedInfoRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		stagedInfoRestService.save(stagedInfoRest);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = stagedInfoRest.getId()+"";
		URI uri = uriBuilder.path("/rest/stagedInfoRestController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody StagedInfoRestEntity stagedInfoRest) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<StagedInfoRestEntity>> failures = validator.validate(stagedInfoRest);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		stagedInfoRestService.saveOrUpdate(stagedInfoRest);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		stagedInfoRestService.deleteEntityById(StagedInfoRestEntity.class, id);
	}
}
