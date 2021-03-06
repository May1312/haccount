package com.jeecg.superquery.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.jeecgframework.core.beanvalidator.BeanValidators;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.ApplicationContextUtil;
import org.jeecgframework.core.util.ExceptionUtil;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.jwt.util.ResponseMessage;
import org.jeecgframework.jwt.util.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.MutiLangServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.alibaba.fastjson.JSONArray;
import com.jeecg.superquery.entity.SuperQueryFieldEntity;
import com.jeecg.superquery.entity.SuperQueryHistoryEntity;
import com.jeecg.superquery.entity.SuperQueryMainEntity;
import com.jeecg.superquery.entity.SuperQueryTableEntity;
import com.jeecg.superquery.page.SuperQueryMainPage;
import com.jeecg.superquery.service.SuperQueryMainServiceI;
import com.jeecg.superquery.util.SuperQueryUtil;

/**   
 * @Title: Controller
 * @Description: ????????????
 * @author onlineGenerator
 * @date 2017-12-04 18:10:18
 * @version V1.0   
 *
 */
//@Api(value="SuperQueryMain",description="????????????",tags="superQueryMainController")
@Controller
@RequestMapping("/superQueryMainController")
public class SuperQueryMainController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SuperQueryMainController.class);

	@Autowired
	private SuperQueryMainServiceI superQueryMainService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private Validator validator;
	@Autowired
	private UserService userService;

	/**
	 * ?????????????????? ????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "list")
	public ModelAndView list(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/superquery/superQueryMainList");
	}
	//????????????
	//TODO
	@RequestMapping(params = "queryBuilder")
	public ModelAndView queryBuilder(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/superquery/queryBuilder");
	}
	
	
	/**
	 * easyui AJAX????????????
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param user
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(SuperQueryMainEntity superQueryMain,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(SuperQueryMainEntity.class, dataGrid);
		//?????????????????????
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, superQueryMain);
		try{
		//???????????????????????????

			String sql = SuperQueryUtil.getComplxSuperQuerySQL(request);
			if(oConvertUtils.isNotEmpty(sql)) {
				cq.add(Restrictions.sqlRestriction(" id in ("+sql+")"));
			}

		}catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		cq.add();
		this.superQueryMainService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "doDel")
	@ResponseBody
	public AjaxJson doDel(SuperQueryMainEntity superQueryMain, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		superQueryMain = systemService.getEntity(SuperQueryMainEntity.class, superQueryMain.getId());
		String message = "????????????????????????";
		try{
			superQueryMainService.delMain(superQueryMain);
			systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "????????????????????????";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	 @RequestMapping(params = "doBatchDel")
	@ResponseBody
	public AjaxJson doBatchDel(String ids,HttpServletRequest request){
		AjaxJson j = new AjaxJson();
		String message = "????????????????????????";
		try{
			for(String id:ids.split(",")){
				SuperQueryMainEntity superQueryMain = systemService.getEntity(SuperQueryMainEntity.class,
				id
				);
				superQueryMainService.delMain(superQueryMain);
				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
			}
		}catch(Exception e){
			e.printStackTrace();
			message = "????????????????????????";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * ??????????????????
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "doAdd")
	@ResponseBody
	public AjaxJson doAdd(SuperQueryMainEntity superQueryMain,SuperQueryMainPage superQueryMainPage, HttpServletRequest request) {
		List<SuperQueryTableEntity> superQueryTableList =  superQueryMainPage.getSuperQueryTableList();
		List<SuperQueryFieldEntity> superQueryFieldList =  superQueryMainPage.getSuperQueryFieldList();
		AjaxJson j = new AjaxJson();
		String message = "????????????";
		try{
			superQueryMainService.addMain(superQueryMain, superQueryTableList,superQueryFieldList);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "????????????????????????";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}
	/**
	 * ??????????????????
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "doUpdate")
	@ResponseBody
	public AjaxJson doUpdate(SuperQueryMainEntity superQueryMain,SuperQueryMainPage superQueryMainPage, HttpServletRequest request) {
		List<SuperQueryTableEntity> superQueryTableList =  superQueryMainPage.getSuperQueryTableList();
		List<SuperQueryFieldEntity> superQueryFieldList =  superQueryMainPage.getSuperQueryFieldList();
		AjaxJson j = new AjaxJson();
		String message = "????????????";
		try{
			superQueryMainService.updateMain(superQueryMain, superQueryTableList,superQueryFieldList);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		}catch(Exception e){
			e.printStackTrace();
			message = "????????????????????????";
			throw new BusinessException(e.getMessage());
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "goAdd")
	public ModelAndView goAdd(SuperQueryMainEntity superQueryMain, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(superQueryMain.getId())) {
			superQueryMain = superQueryMainService.getEntity(SuperQueryMainEntity.class, superQueryMain.getId());
			req.setAttribute("superQueryMainPage", superQueryMain);
			
		}
		/* String hql2="select tableName from SuperQueryTableEntity";
		  List<SuperQueryTableEntity> superQueryTableList=systemService.findHql(hql2);
		  req.setAttribute("superQueryTableList", superQueryTableList);*/
		return new ModelAndView("com/jeecg/superquery/superQueryMain-add");
	}
	
	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "goUpdate")
	public ModelAndView goUpdate(SuperQueryMainEntity superQueryMain, HttpServletRequest req) {
		if (StringUtil.isNotEmpty(superQueryMain.getId())) {
			superQueryMain = superQueryMainService.getEntity(SuperQueryMainEntity.class, superQueryMain.getId());
			req.setAttribute("superQueryMainPage", superQueryMain);
		}
		return new ModelAndView("com/jeecg/superquery/superQueryMain-update");
	}
	
	
	/**
	 * ??????????????????[?????????]
	 * 
	 * @return
	 */
	@RequestMapping(params = "superQueryTableList")
	public ModelAndView superQueryTableList(SuperQueryMainEntity superQueryMain, HttpServletRequest req) {
	
		//===================================================================================
		//????????????
		Object id0 = superQueryMain.getId();
		//===================================================================================
		//??????-?????????
	    String hql0 = "from SuperQueryTableEntity where 1 = 1 AND mAIN_ID = ? ";
	    try{
	    	List<SuperQueryTableEntity> superQueryTableEntityList = systemService.findHql(hql0,id0);
			req.setAttribute("superQueryTableList", superQueryTableEntityList);
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		return new ModelAndView("com/jeecg/superquery/superQueryTableList");
	}
	/**
	 * ??????????????????[????????????]
	 * 
	 * @return
	 */
	@RequestMapping(params = "superQueryFieldList")
	public ModelAndView superQueryFieldList(SuperQueryMainEntity superQueryMain, HttpServletRequest req) {
	
		//===================================================================================
		//????????????
		Object id1 = superQueryMain.getId();
		//===================================================================================
		//??????-????????????
	    String hql1 = "from SuperQueryFieldEntity where 1 = 1 AND mAIN_ID = ? ";
	    try{
	    	List<SuperQueryFieldEntity> superQueryFieldEntityList = systemService.findHql(hql1,id1);
			req.setAttribute("superQueryFieldList", superQueryFieldEntityList);
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		return new ModelAndView("com/jeecg/superquery/superQueryFieldList");
	}

    /**
    * ??????excel
    *
    * @param request
    * @param response
    */
    @RequestMapping(params = "exportXls")
    public String exportXls(SuperQueryMainEntity superQueryMain,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid,ModelMap map) {
    	CriteriaQuery cq = new CriteriaQuery(SuperQueryMainEntity.class, dataGrid);
    	//?????????????????????
    	org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, superQueryMain);
    	try{
    	//???????????????????????????
    	}catch (Exception e) {
    		throw new BusinessException(e.getMessage());
    	}
    	cq.add();
    	List<SuperQueryMainEntity> list=this.superQueryMainService.getListByCriteriaQuery(cq, false);
    	List<SuperQueryMainPage> pageList=new ArrayList<SuperQueryMainPage>();
        if(list!=null&&list.size()>0){
        	for(SuperQueryMainEntity entity:list){
        		try{
        		SuperQueryMainPage page=new SuperQueryMainPage();
        		   MyBeanUtils.copyBeanNotNull2Bean(entity,page);
            	    Object id0 = entity.getId();
				    String hql0 = "from SuperQueryTableEntity where 1 = 1 AND mAIN_ID = ? ";
        	        List<SuperQueryTableEntity> superQueryTableEntityList = systemService.findHql(hql0,id0);
            		page.setSuperQueryTableList(superQueryTableEntityList);
            	    Object id1 = entity.getId();
				    String hql1 = "from SuperQueryFieldEntity where 1 = 1 AND mAIN_ID = ? ";
        	        List<SuperQueryFieldEntity> superQueryFieldEntityList = systemService.findHql(hql1,id1);
            		page.setSuperQueryFieldList(superQueryFieldEntityList);
            		pageList.add(page);
            	}catch(Exception e){
            		logger.info(e.getMessage());
            	}
            }
        }
        map.put(NormalExcelConstants.FILE_NAME,"????????????");
        map.put(NormalExcelConstants.CLASS,SuperQueryMainPage.class);
        map.put(NormalExcelConstants.PARAMS,new ExportParams("??????????????????", "?????????:Jeecg",
            "????????????"));
        map.put(NormalExcelConstants.DATA_LIST,pageList);
        return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}

    /**
	 * ??????excel????????????
	 * @param request
	 * @param
	 * @return
	 */
	@RequestMapping(params = "importExcel", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson importExcel(HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// ????????????????????????
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(2);
			params.setNeedSave(true);
			try {
				List<SuperQueryMainPage> list =  ExcelImportUtil.importExcel(file.getInputStream(), SuperQueryMainPage.class, params);
				SuperQueryMainEntity entity1=null;
				for (SuperQueryMainPage page : list) {
					entity1=new SuperQueryMainEntity();
					MyBeanUtils.copyBeanNotNull2Bean(page,entity1);
		            superQueryMainService.addMain(entity1, page.getSuperQueryTableList(),page.getSuperQueryFieldList());
				}
				j.setMsg("?????????????????????");
			} catch (Exception e) {
				j.setMsg("?????????????????????");
				logger.error(ExceptionUtil.getExceptionMessage(e));
			}finally{
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			}
			return j;
	}
	/**
	* ??????excel ?????????
	*/
	@SuppressWarnings( "rawtypes"   )
	@RequestMapping(params = "exportXlsByT")
	public String exportXlsByT(ModelMap map) {
		map.put(NormalExcelConstants.FILE_NAME,"????????????");
		map.put(NormalExcelConstants.CLASS,SuperQueryMainPage.class);
		map.put(NormalExcelConstants.PARAMS,new ExportParams("??????????????????", "?????????:"+ ResourceUtil.getSessionUser().getRealName(),
		"????????????"));
		map.put(NormalExcelConstants.DATA_LIST,new ArrayList());
		return NormalExcelConstants.JEECG_EXCEL_VIEW;
	}
	/**
	* ??????????????????
	*
	* @return
	*/
	@RequestMapping(params = "upload")
	public ModelAndView upload(HttpServletRequest req) {
		req.setAttribute("controller_name", "superQueryMainController");
		return new ModelAndView("common/upload/pub_excel_upload");
	}

 	
 	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	//@ApiOperation(value="????????????????????????",produces="application/json",httpMethod="GET")
	public ResponseMessage<List<SuperQueryMainPage>> list() {
		List<SuperQueryMainEntity> list= superQueryMainService.getList(SuperQueryMainEntity.class);
    	List<SuperQueryMainPage> pageList=new ArrayList<SuperQueryMainPage>();
        if(list!=null&&list.size()>0){
        	for(SuperQueryMainEntity entity:list){
        		try{
        			SuperQueryMainPage page=new SuperQueryMainPage();
        		   MyBeanUtils.copyBeanNotNull2Bean(entity,page);
					Object id0 = entity.getId();
					Object id1 = entity.getId();
				     String hql0 = "from SuperQueryTableEntity where 1 = 1 AND mAIN_ID = ? ";
	    			List<SuperQueryTableEntity> superQueryTableOldList = this.superQueryMainService.findHql(hql0,id0);
            		page.setSuperQueryTableList(superQueryTableOldList);
				     String hql1 = "from SuperQueryFieldEntity where 1 = 1 AND mAIN_ID = ? ";
	    			List<SuperQueryFieldEntity> superQueryFieldOldList = this.superQueryMainService.findHql(hql1,id1);
            		page.setSuperQueryFieldList(superQueryFieldOldList);
            		pageList.add(page);
            	}catch(Exception e){
            		logger.info(e.getMessage());
            	}
            }
        }
		return Result.success(pageList);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	//@ApiOperation(value="??????ID????????????????????????",notes="??????ID????????????????????????",httpMethod="GET",produces="application/json")
	public ResponseMessage<?> get(@ApiParam(required=true,name="id",value="ID")@PathVariable("id") String id) {
		SuperQueryMainEntity task = superQueryMainService.get(SuperQueryMainEntity.class, id);
		if (task == null) {
			return Result.error("??????ID??????????????????????????????");
		}
		SuperQueryMainPage page = new SuperQueryMainPage();
		try {
			MyBeanUtils.copyBeanNotNull2Bean(task, page);
				Object id0 = task.getId();
				Object id1 = task.getId();
		    String hql0 = "from SuperQueryTableEntity where 1 = 1 AND mAIN_ID = ? ";
			List<SuperQueryTableEntity> superQueryTableOldList = this.superQueryMainService.findHql(hql0,id0);
    		page.setSuperQueryTableList(superQueryTableOldList);
		    String hql1 = "from SuperQueryFieldEntity where 1 = 1 AND mAIN_ID = ? ";
			List<SuperQueryFieldEntity> superQueryFieldOldList = this.superQueryMainService.findHql(hql1,id1);
    		page.setSuperQueryFieldList(superQueryFieldOldList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.success(page);
	}
 	
 	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	//@ApiOperation(value="??????????????????")
	public ResponseMessage<?> create(@ApiParam(name="??????????????????")@RequestBody SuperQueryMainPage superQueryMainPage, UriComponentsBuilder uriBuilder) {
		//??????JSR303 Bean Validator????????????????????????????????????400????????????json?????????????????????.
		Set<ConstraintViolation<SuperQueryMainPage>> failures = validator.validate(superQueryMainPage);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//??????
		List<SuperQueryTableEntity> superQueryTableList =  superQueryMainPage.getSuperQueryTableList();
		List<SuperQueryFieldEntity> superQueryFieldList =  superQueryMainPage.getSuperQueryFieldList();
		
		SuperQueryMainEntity superQueryMain = new SuperQueryMainEntity();
		try{
			MyBeanUtils.copyBeanNotNull2Bean(superQueryMainPage,superQueryMain);
		}catch(Exception e){
            logger.info(e.getMessage());
            return Result.error("????????????????????????");
        }
		superQueryMainService.addMain(superQueryMain, superQueryTableList,superQueryFieldList);

		return Result.success(superQueryMain);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	//@ApiOperation(value="??????????????????",notes="??????????????????")
	public ResponseMessage<?> update(@RequestBody SuperQueryMainPage superQueryMainPage) {
		//??????JSR303 Bean Validator????????????????????????????????????400????????????json?????????????????????.
		Set<ConstraintViolation<SuperQueryMainPage>> failures = validator.validate(superQueryMainPage);
		if (!failures.isEmpty()) {
			return Result.error(JSONArray.toJSONString(BeanValidators.extractPropertyAndMessage(failures)));
		}

		//??????
		List<SuperQueryTableEntity> superQueryTableList =  superQueryMainPage.getSuperQueryTableList();
		List<SuperQueryFieldEntity> superQueryFieldList =  superQueryMainPage.getSuperQueryFieldList();
		
		SuperQueryMainEntity superQueryMain = new SuperQueryMainEntity();
		try{
			MyBeanUtils.copyBeanNotNull2Bean(superQueryMainPage,superQueryMain);
		}catch(Exception e){
            logger.info(e.getMessage());
            return Result.error("????????????????????????");
        }
		superQueryMainService.updateMain(superQueryMain, superQueryTableList,superQueryFieldList);

		//???Restful???????????????204?????????, ?????????. ???????????????200?????????.
		return Result.success();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	//@ApiOperation(value="??????????????????")
	public ResponseMessage<?> delete(@ApiParam(name="id",value="ID",required=true)@PathVariable("id") String id) {
		logger.info("delete[{}]" + id);
		// ??????
		if (StringUtils.isEmpty(id)) {
			return Result.error("ID????????????");
		}
		try {
			SuperQueryMainEntity superQueryMain = superQueryMainService.get(SuperQueryMainEntity.class, id);
			superQueryMainService.delMain(superQueryMain);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("????????????????????????");
		}

		return Result.success();
	}
	
	//TODO
	/**
	 * ?????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "querysBuilder" ,method = { RequestMethod.GET, RequestMethod.POST } )
	public ModelAndView querysBuilder(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/superquery/querysBuilder");
	}
	
	
	/***
	 * Ztree ??????????????? result list<Map<String,Object>>
	 * 
	 * @param depatr
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getTreeData", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public AjaxJson getTreeData(SuperQueryTableEntity table, HttpServletResponse response, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		try {
			String queryCode = request.getParameter("queryCode");
			String hql = " select  a FROM SuperQueryTableEntity a ,SuperQueryMainEntity b  WHERE a.mainId=b.id and b.queryCode=?";
			//String query="select * FROM super_query_main a ,super_query_table b where  a.id=b.main_id  and a.query_code=?";
			List<SuperQueryTableEntity> findHql = systemService.findHql(hql, queryCode);
			// ????????????map??????????????????
			Map<String, List<SuperQueryTableEntity>> mapList = new HashMap<String, List<SuperQueryTableEntity>>();
			for (Iterator it = findHql.iterator(); it.hasNext();) {
				// ???????????????????????????????????????
				SuperQueryTableEntity tab = (SuperQueryTableEntity) it.next();
				// ???????????????map???????????????????????????????????????????????????????????????
				if (mapList.containsKey(tab.getMainId())) {
					List<SuperQueryTableEntity> syn = mapList.get(tab.getMainId());
					syn.add(tab);
					// ??????????????????????????????????????????????????????????????????
				} else {
					List<SuperQueryTableEntity> syns = new ArrayList<SuperQueryTableEntity>();
					syns.add(tab);
					mapList.put(tab.getMainId(), syns);
				}
			}
			
			// ??????map
			List<Map<String, Object>> json = new ArrayList<Map<String, Object>>();
			for (Map.Entry<String, List<SuperQueryTableEntity>> m : mapList.entrySet()) {
				List<SuperQueryTableEntity> value = m.getValue();
				Map<String, Object> map = null;
				String id = "";
				for (SuperQueryTableEntity superQueryTableEntity : value) {
					map = new HashMap<String, Object>();
					map.put("id", superQueryTableEntity.getId());
					map.put("chkDisabled", false);
					map.put("click", true);
					map.put("name", superQueryTableEntity.getInstruction());
					map.put("nocheck", false);
					map.put("struct", "TREE");
					map.put("title", superQueryTableEntity.getTableName());
					map.put("queryCode", queryCode);
					if (superQueryTableEntity.getIsMain().equals("Y")) {
						map.put("parentId", "0");
						map.put("level", 1);
						map.put("icon", "plug-in/easyui/themes/metrole/images/icon_tree_folder.png");
						id = superQueryTableEntity.getId();
					} else {
						map.put("parentId", id);
						map.put("level", 2);
						map.put("icon", "plug-in/easyui/themes/metrole/images/tag.png");
					}
					json.add(map);
				}
				j.setObj(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}

	/**
	 * ??????tableName?????????????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params = "getTextByTabelName" , method = { RequestMethod.GET, RequestMethod.POST })
	public AjaxJson getTextById(HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json=new AjaxJson();
		String tableName = request.getParameter("tableName");
		String sql=" SELECT table_name, `name`, txt,ctype,stype,dict_code,dict_table ,dict_text,main_id  from super_query_field where table_name= ? GROUP BY `name`,txt";
		List<Map<String, Object>> findForJdbc = systemService.findForJdbc(sql,tableName);
		json.setObj(findForJdbc);
		return  json;
	}
	
	/**
	 * ????????????????????????
	 * @param param
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params = "saveHistory" , method = { RequestMethod.GET, RequestMethod.POST })
	public AjaxJson saveHistory(@RequestParam Map<String,Object> param, HttpServletRequest request,HttpServletResponse response) {
		AjaxJson ajaxJson=new AjaxJson();
		String queryCode = request.getParameter("queryCode");
		try {
			if(StringUtil.isNotEmpty(param)){
				String name = param.get("name").toString();
				String userId = ResourceUtil.getSessionUser().getId();
				String [] obj=new String[]{name,userId,queryCode};
				List<SuperQueryHistoryEntity> list = systemService.findHql("from SuperQueryHistoryEntity where historyName=? and userId=? and queryCode = ?", obj);
				if(list != null && list.size() > 0 ){
					ajaxJson.setSuccess(false);
				}else{
					String json=param.get("json").toString();
					String substring = json.substring(json.indexOf("[")+1, json.length()-1);
					SuperQueryHistoryEntity historyEntity=new SuperQueryHistoryEntity();
					historyEntity.setHistoryName(name);
					historyEntity.setRecord(substring );
					historyEntity.setUserId(userId);
					historyEntity.setQueryCode(queryCode);
					systemService.save(historyEntity);
					ajaxJson.setSuccess(true);
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			ajaxJson.setSuccess(false);
		}
		return  ajaxJson;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * ??????????????????????????????????????????????????????
	 */
	@ResponseBody
	@RequestMapping(params= "getHistoryByUserId", method = RequestMethod.POST )
	public AjaxJson getHistoryByUserId(  HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json=new AjaxJson();
		String queryCode = request.getParameter("queryCode");
		try {
			List<String>list=new ArrayList<String>();
			String userId = ResourceUtil.getSessionUser().getId();
			String hql="from SuperQueryHistoryEntity where user_id=? and queryCode=?";
			String[] param=new String[]{userId,queryCode};
			List<SuperQueryHistoryEntity> findHql = systemService.findHql(hql, param);
			if(findHql != null && findHql.size() >0){
			  for (SuperQueryHistoryEntity superQueryHistoryEntity : findHql) {
				list.add(superQueryHistoryEntity.getHistoryName());
			} 
			  String[] array = new String[list.size()];
			  String[] s=list.toArray(array);
			  json.setSuccess(true);
			  json.setObj(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
		}
		return  json;
	}
	/**
	 * ???????????????????????? ??????????????????
	 */
	@ResponseBody
	@RequestMapping(params= "getHistoryByText", method = RequestMethod.POST )
	public AjaxJson getHistoryById(  HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json=new AjaxJson();
		 String record="";
		try {
			String name = request.getParameter("name");
			String userId = ResourceUtil.getSessionUser().getId();
			String[] arr=new String[]{name,userId};
			String hql=" from SuperQueryHistoryEntity where history_name=? and user_id=?";
			List<SuperQueryHistoryEntity> findHql = systemService.findHql(hql, arr);
			if(findHql != null && findHql.size() >0){
				 for (SuperQueryHistoryEntity history : findHql) {
					 record = history.getRecord();
				}
				json.setSuccess(true);
				json.setObj(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
		}
		return  json;
	}
	
	/***
	 * ????????????????????????
	 */
	@ResponseBody
	@RequestMapping(params= "updateHistoryByName", method = RequestMethod.POST )
	public AjaxJson updateHistoryByName(  HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json=new AjaxJson();
		try {
			if(StringUtil.isNotEmpty(request.getParameter("name"))){
				String name = request.getParameter("name");//??????????????????
				String userId=ResourceUtil.getSessionUser().getId();
				String [] par=new String []{name,userId};
				String hql="from SuperQueryHistoryEntity where history_name=? and userId=?";
				List<SuperQueryHistoryEntity> historyList = systemService.findHql(hql, par);
				if(historyList!=null&&historyList.size()>0){
					json.setSuccess(false);
				}else{
					String nodeName = request.getParameter("nodeName");//??????????????????
					String sql = "update super_query_history set history_name="+"'"+name+"' where user_id="+"'"+userId+"' and history_name="+"'"+nodeName+"'";
					int updateByName = systemService.updateBySqlString(sql);
					if(updateByName==1){
						json.setSuccess(true);
					}
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("????????????");
		}
		return  json;
	}
	
	
	/**
	 * ???????????????????????? byName
	 */
	 
	@ResponseBody
	@RequestMapping(params= "deleteHistoryByName", method = RequestMethod.POST )
	public AjaxJson deleteHistoryByName(  HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json=new AjaxJson();
		try {
			if(StringUtil.isNotEmpty(request.getParameter("name"))){
				String name = request.getParameter("name");
				String userId=ResourceUtil.getSessionUser().getId();
				String sql = "delete from  super_query_history where  history_name="+"'"+name+"' and user_id="+"'"+userId+"'";
				int deletebyName = systemService.updateBySqlString(sql);
				if(deletebyName==1){
					json.setSuccess(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("????????????");
		}
		return  json;
	}
	
	/**
	 * ??????filed??????????????????????????????
	 * @param field ??????Value
	 * @param mainId ??????ID
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params = "getFieldType" , method = { RequestMethod.GET, RequestMethod.POST })
	public AjaxJson getFieldType(String field,String mainId, HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json = new AjaxJson();
		try {
			// step.1 ??????mainId???????????????????????????
			String sql = "select stype,`name`,txt,dict_table,dict_code,dict_text from super_query_field where `name`=? AND main_id = ?";
			List<Map<String, Object>> page = systemService.findForJdbc(sql,field,mainId);
			if(page.size()>0) {
				json.setObj(page);
			}
		} catch(Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
		}
		return json;
	}
	
	/**
	 * ??????????????????CODE??????TYPE
	 * @param typegroup	????????????CODE
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(params = "getSelectType",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public AjaxJson getSelectType(String typegroup,HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json = new AjaxJson();
		try {
			//step.1 ??????????????????Code????????????
			String sql = "select t.typecode,t.typename from t_s_type t where typegroupid = (select tg.id from t_s_typegroup tg where tg.typegroupcode=?)";
			List<Map<String, Object>> selectType = systemService.findForJdbc(sql,typegroup);
			//step.2 ?????????????????????????????????????????????????????????????????????
			List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
			MutiLangServiceI mutiLangService = ApplicationContextUtil.getContext().getBean(MutiLangServiceI.class);	
			if(selectType.size()>0) {
				for (Map<String, Object> map : selectType) {
					Map<String, Object> mutiMap = new HashMap<String, Object>();
					mutiMap.put("typecode", map.get("typecode"));
					mutiMap.put("typename", mutiLangService.getLang(map.get("typename").toString()));
					listMap.add(mutiMap);
				}
				json.setObj(listMap);
			}
		} catch(Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
		}
		return json;
	}
	
	/**
	 * ??????QueryCode??????MainID
	 * @param queryCode	?????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(params = "getMainIdByQueryCode",method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public AjaxJson getMainIdByQueryCode(String queryCode,HttpServletRequest request,HttpServletResponse response) {
		AjaxJson json = new AjaxJson();
		try {
			String sql = "select id from super_query_main where query_code = ?";
			List<Map<String,Object>> main = systemService.findForJdbc(sql, queryCode);
			if(main != null && main.size() > 0) {
				json.setObj(main.get(0).get("id"));
			}
		} catch(Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
		}
		return json;
	}
}
