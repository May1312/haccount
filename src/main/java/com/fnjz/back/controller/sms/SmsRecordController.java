package com.fnjz.back.controller.sms;

import com.fnjz.back.entity.sms.SmsRecordEntity;
import com.fnjz.back.service.sms.SmsRecordServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.beanvalidator.BeanValidators;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.DateUtils;
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
 * @Description: 短信发送记录
 * @author zhangdaihao
 * @date 2018-05-30 10:11:23
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/smsRecordController")
public class SmsRecordController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SmsRecordController.class);

	@Autowired
	private SmsRecordServiceI smsRecordService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	


	/**
	 * 短信发送记录列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/fnjz/back/sms/smsRecordList");
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
	public void datagrid(SmsRecordEntity smsRecord,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(SmsRecordEntity.class, dataGrid);
		//查询条件组装器
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, smsRecord, request.getParameterMap());
		this.smsRecordService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * 删除短信发送记录
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(SmsRecordEntity smsRecord, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		smsRecord = systemService.getEntity(SmsRecordEntity.class, smsRecord.getId());
		message = "短信发送记录删除成功";
		smsRecordService.delete(smsRecord);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		
		j.setMsg(message);
		return j;
	}


	/**
	 * 添加短信发送记录
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(SmsRecordEntity smsRecord, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(smsRecord.getId())) {
			message = "短信发送记录更新成功";
			SmsRecordEntity t = smsRecordService.get(SmsRecordEntity.class, smsRecord.getId());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(smsRecord, t);
				smsRecordService.saveOrUpdate(t);
				systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
			} catch (Exception e) {
				e.printStackTrace();
				message = "短信发送记录更新失败";
			}
		} else {
			message = "短信发送记录添加成功";
			if ( ! DateUtils.compateDate(DateUtils.getDate(),smsRecord.getSendtime()).equalsIgnoreCase("before")){
				message="已过发送时间";
			}else if (StringUtil.isEmpty(smsRecord.getSendmobile()) && StringUtil.isEmpty(smsRecord.getTerminaltype())){
				message="请输入手机号，或者终端类型";
			}
			else{
				smsRecord.setSendstate("unsend");
				smsRecordService.save(smsRecord);
				systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
			}

		}
		j.setMsg(message);
		return j;
	}

	/**
	 * 短信发送记录列表页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(SmsRecordEntity smsRecord, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(smsRecord.getId())) {
			smsRecord = smsRecordService.getEntity(SmsRecordEntity.class, smsRecord.getId());
			req.setAttribute("smsRecordPage", smsRecord);
		}
		return new ModelAndView("com/fnjz/back/sms/smsRecord");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<SmsRecordEntity> list() {
		List<SmsRecordEntity> listSmsRecords=smsRecordService.getList(SmsRecordEntity.class);
		return listSmsRecords;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		SmsRecordEntity task = smsRecordService.get(SmsRecordEntity.class, id);
		if (task == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(task, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody SmsRecordEntity smsRecord, UriComponentsBuilder uriBuilder) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<SmsRecordEntity>> failures = validator.validate(smsRecord);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		smsRecordService.save(smsRecord);

		//按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		String id = smsRecord.getId();
		URI uri = uriBuilder.path("/rest/smsRecordController/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@RequestBody SmsRecordEntity smsRecord) {
		//调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
		Set<ConstraintViolation<SmsRecordEntity>> failures = validator.validate(smsRecord);
		if (!failures.isEmpty()) {
			return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
		}

		//保存
		smsRecordService.saveOrUpdate(smsRecord);

		//按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") String id) {
		smsRecordService.deleteEntityById(SmsRecordEntity.class, id);
	}
}
