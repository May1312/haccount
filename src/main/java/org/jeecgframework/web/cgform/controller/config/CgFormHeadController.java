package org.jeecgframework.web.cgform.controller.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.ExceptionUtil;
import org.jeecgframework.core.util.IpUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.cgform.common.CgAutoListConstant;
import org.jeecgframework.web.cgform.engine.TempletContext;
import org.jeecgframework.web.cgform.entity.config.CgFormFieldEntity;
import org.jeecgframework.web.cgform.entity.config.CgFormFieldVO;
import org.jeecgframework.web.cgform.entity.config.CgFormHeadEntity;
import org.jeecgframework.web.cgform.exception.BusinessException;
import org.jeecgframework.web.cgform.service.config.CgFormFieldServiceI;
import org.jeecgframework.web.cgform.service.config.CgFormIndexServiceI;
import org.jeecgframework.web.cgform.service.config.DbTableHandleI;
import org.jeecgframework.web.cgform.service.impl.config.TableSQLServerHandleImpl;
import org.jeecgframework.web.cgform.service.impl.config.util.DbTableUtil;
import org.jeecgframework.web.cgform.service.impl.config.util.FieldNumComparator;
import org.jeecgframework.web.cgform.util.PublicUtil;
import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Title: Controller
 * @Description: ??????????????????
 * @author ?????????
 * @date 2013-06-30 11:36:53
 * @version V1.0
 * 
 */
//@Scope("prototype")
@Controller
@RequestMapping("/cgFormHeadController")
public class CgFormHeadController extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CgFormHeadController.class);
	
	@Autowired
	private CgFormFieldServiceI cgFormFieldService;
	@Autowired
	private CgFormIndexServiceI cgFormIndexService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private TempletContext templetContext;

	/**
	 * ??????????????????????????? ????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "cgFormHeadList")
	public ModelAndView cgFormHeadList(HttpServletRequest request) {
		return new ModelAndView("jeecg/cgform/config/cgFormHeadList");
	}
	/**
	 * ?????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "cgForms")
	public ModelAndView cgForms(HttpServletRequest request) {
		return new ModelAndView("jeecg/cgform/config/cgForms");
	}
	@RequestMapping(params = "goCgFormSynChoice")
	public ModelAndView goCgFormSynChoice(HttpServletRequest request) {
		return new ModelAndView("jeecg/cgform/config/cgformSynChoice");
	}

	@RequestMapping(params = "popmenulink")
	public ModelAndView popmenulink(ModelMap modelMap,
                                    @RequestParam String url,
                                    @RequestParam String title, HttpServletRequest request) {
        modelMap.put("title",title);
        modelMap.put("url",url);
		return new ModelAndView("jeecg/cgform/config/popmenulink");
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
	public void datagrid(CgFormHeadEntity cgFormHead,
			HttpServletRequest request, HttpServletResponse response,
			DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(CgFormHeadEntity.class,
				dataGrid);

		String jformCategory = request.getParameter("jformCategory");
		if(StringUtil.isNotEmpty(jformCategory)){
			cq.eq("jformCategory", jformCategory);
			//cq.add();
		}

		cq.isNull("physiceId");
		cq.add();

		
		// ?????????????????????
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq,
				cgFormHead);
		this.cgFormFieldService.getDataGridReturn(cq, true);

		List<CgFormHeadEntity> list = dataGrid.getResults();
		Map<String,Map<String,Object>> extMap = new HashMap<String, Map<String,Object>>();		
		List<Map<String,Object>> pzlist = this.cgFormFieldService.getPeizhiCountByIds(list);
		for(Map<String,Object> temp:pzlist){
	        //???????????????????????????????????????????????????
			Map<String,Object> m = new HashMap<String,Object>();
	        m.put("hasPeizhi",temp.get("hasPeizhi")==null?"0":temp.get("hasPeizhi"));
	        extMap.put(temp.get("id").toString(), m);
		}
		//??????????????????????????????????????????????????????????????????????????????????????????
		for(CgFormHeadEntity temp:list){
	        //???????????????????????????????????????????????????
		    if (extMap.get(temp.getId())==null) {
		    	Map<String,Object> m = new HashMap<String,Object>();
				m.put("hasPeizhi","0");
			 	extMap.put(temp.getId(), m); 
			}       
		}

		
		TagUtil.datagrid(response, dataGrid, extMap);

	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(CgFormHeadEntity cgFormHead,
			HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		cgFormHead = systemService.getEntity(CgFormHeadEntity.class,
				cgFormHead.getId());
		String message = "????????????";
		cgFormFieldService.deleteCgForm(cgFormHead);
		cgFormFieldService.removeSubTableStr4Main(cgFormHead);
		systemService.addLog(message, Globals.Log_Type_DEL,
				Globals.Log_Leavel_INFO);
		logger.info("["+IpUtil.getIpAddr(request)+"][online??????????????????]"+message+"?????????"+cgFormHead.getTableName());
		j.setMsg(message);
		return j;
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "rem")
	@ResponseBody
	public AjaxJson rem(CgFormHeadEntity cgFormHead,
			HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		cgFormHead = systemService.getEntity(CgFormHeadEntity.class,
				cgFormHead.getId());
		String message = "????????????";
		cgFormFieldService.delete(cgFormHead);
		cgFormFieldService.removeSubTableStr4Main(cgFormHead);
		systemService.addLog(message, Globals.Log_Type_DEL,
				Globals.Log_Leavel_INFO);
		logger.info("["+IpUtil.getIpAddr(request)+"][online??????????????????]"+message+"?????????"+cgFormHead.getTableName());
		j.setMsg(message);
		return j;
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "delField")
	@ResponseBody
	public AjaxJson delField(CgFormFieldEntity cgFormField,
			HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		cgFormField = systemService.getEntity(CgFormFieldEntity.class,
				cgFormField.getId());
		String message = cgFormField.getFieldName()+"????????????";

		CgFormHeadEntity table = cgFormField.getTable();
		table.setIsDbSynch("N");
		this.cgFormFieldService.updateEntitie(table);

		cgFormFieldService.delete(cgFormField);
		systemService.addLog(message, Globals.Log_Type_DEL,
				Globals.Log_Leavel_INFO);
		j.setMsg(message);
		return j;
	}

	
	/**
	 * ??????????????????????????????
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "doDbSynch")
	@ResponseBody
	public AjaxJson doDbSynch(CgFormHeadEntity cgFormHead,String synMethod,
			HttpServletRequest request) {
		String message;
		AjaxJson j = new AjaxJson();
		cgFormHead = systemService.getEntity(CgFormHeadEntity.class,cgFormHead.getId());

		logger.info("---??????????????? ---doDbSynch-----> TableName:"+cgFormHead.getTableName()+" ---???????????? :"+cgFormHead.getUpdateDate()+" ----????????????:"+cgFormHead.getCreateDate() +"---??????IP ---+"+oConvertUtils.getIpAddrByRequest(request));
		//???????????????????????????online??????????????????????????????
		String sql = "select count(*) from cgform_head where table_name = '"+cgFormHead.getTableName()+"'";
		Long i = systemService.getCountForJdbc(sql);
		if(i==0){
			message = "???????????????????????????????????????";
			logger.info(message+" ----- ??????IP ---+"+IpUtil.getIpAddr(request));
			j.setMsg(message);
			return j;
		}
		TSUser currentUser = ResourceUtil.getSessionUser();
       if("0".equals(currentUser.getDevFlag())){
            message = "??????????????????????????????????????????????????????";
            logger.info(message+" ----- ??????IP ---+"+IpUtil.getIpAddr(request));
            j.setMsg(message);
            return j;
        }
       //TODO ??????????????????????????????????????????

		
		//???????????????
		try {

			if("force".equals(synMethod)){
				DbTableHandleI dbTableHandle = DbTableUtil.getTableHandle(systemService.getSession());
				if(dbTableHandle instanceof TableSQLServerHandleImpl){
					String dropsql =  dbTableHandle.dropTableSQL(cgFormHead.getTableName());
					systemService.executeSql(dropsql); 
				}
			}

			
			boolean bl = cgFormFieldService.dbSynch(cgFormHead,synMethod);
			if(bl){
				//????????????????????????
				cgFormFieldService.appendSubTableStr4Main(cgFormHead);

				//?????????????????????????????????
				List<CgFormHeadEntity> list = cgFormFieldService.findByProperty(CgFormHeadEntity.class, "physiceId", cgFormHead.getId());
				if(list!=null&&list.size()>0){
					message = "????????????,???????????????????????????????????????";		
				}else{
					message = "????????????";
				}

				j.setMsg(message);
				logger.info("["+IpUtil.getIpAddr(request)+"][online???????????????????????????]"+message+"?????????"+cgFormHead.getTableName());
			}else{
				message = "????????????";		
				j.setMsg(message);
				return j;
			}
		} catch (BusinessException e) {
			j.setMsg(e.getMessage());
			return j;
		}
		return j;
	}
	
	
	
	/**
	 * ???????????????????????????
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "save")
	@ResponseBody
	public AjaxJson save(CgFormHeadEntity cgFormHead,
			HttpServletRequest request) {
		String message = "";

		templetContext.clearCache();

		AjaxJson j = new AjaxJson();
		CgFormHeadEntity oldTable =cgFormFieldService.getEntity(CgFormHeadEntity.class, cgFormHead.getId());
		cgFormFieldService.removeSubTableStr4Main(oldTable);
		//step.1 ???????????????????????????,???????????????????????????(???????????????????????????????????????)
		/*if(cgFormHead.getId()!=null){
			boolean tableexist = cgFormFieldService.checkTableExist(cgFormHead.getTableName());
			if(tableexist){
				if(!cgFormHead.getJformPkType().equalsIgnoreCase(oldTable.getJformPkType())){
					if((cgFormHead.getJformPkType().equalsIgnoreCase("NATIVE")||cgFormHead.getJformPkType().equalsIgnoreCase("SEQUENCE"))
							&&(oldTable.getJformPkType().equalsIgnoreCase("NATIVE")||oldTable.getJformPkType().equalsIgnoreCase("SEQUENCE"))){
						//native???sequence????????????
					}else{
						throw new org.jeecgframework.core.common.exception.BusinessException("?????????????????????,????????????????????????");
					}
				}
			}
		}
		*/

		/**
		 * ?????????????????????????????????????????????????????????????????????????????????
		 */
		if(oConvertUtils.isEmpty(cgFormHead.getId())){
			String sql = "select count(*) from tmp_tables where wl_table_name = ?";
			long i = systemService.getCountForJdbcParam(sql, new String[]{cgFormHead.getTableName()});
			if(i>0){
				logger.info("["+IpUtil.getIpAddr(request)+"][?????????????????????online?????????"+cgFormHead.getTableName());
				j.setMsg("?????????????????????????????????????????????");
				return j;
			}
		}

		
		//step.2 ????????????????????????
		StringBuffer msg = new StringBuffer();
		CgFormHeadEntity table = judgeTableIsNotExit(cgFormHead,oldTable,msg);
		message = msg.toString();
		//step.3 ??????orderNum???????????????
		refreshFormFieldOrderNum(cgFormHead);
		
		
		if (StringUtil.isNotEmpty(cgFormHead.getId())&&table!=null) {
			List<CgFormFieldEntity>	formFieldEntities = table.getColumns();
			for (CgFormFieldEntity cgFormFieldEntity : formFieldEntities) {
				if (StringUtil.isEmpty(cgFormFieldEntity.getOldFieldName()) 
						&& StringUtil.isNotEmpty(cgFormFieldEntity.getFieldName())) {
					cgFormFieldEntity.setFieldName(cgFormFieldEntity.getFieldName().toLowerCase());
					cgFormFieldEntity.setOldFieldName(cgFormFieldEntity.getFieldName());
				}

				if (StringUtil.isNotEmpty(cgFormFieldEntity.getFieldName()))
					cgFormFieldEntity.setFieldName(cgFormFieldEntity.getFieldName().trim());

			}

			boolean isChange = cgFormIndexService.updateIndexes(cgFormHead);

			//isChange ??????????????????
			cgFormFieldService.updateTable(table,null,isChange);

			cgFormFieldService.appendSubTableStr4Main(table);
			cgFormFieldService.sortSubTableStr(table);

			/**???????????????*/
			syncTable(table);

			
			systemService.addLog(message, Globals.Log_Type_UPDATE,
					Globals.Log_Leavel_INFO);
		} else if (StringUtil.isEmpty(cgFormHead.getId())&&table==null) {
			List<CgFormFieldEntity>	formFieldEntities = cgFormHead.getColumns();
			for (CgFormFieldEntity cgFormFieldEntity : formFieldEntities) {
				if (StringUtil.isEmpty(cgFormFieldEntity.getOldFieldName())) {
					cgFormFieldEntity.setFieldName(cgFormFieldEntity.getFieldName().toLowerCase());
					cgFormFieldEntity.setOldFieldName(cgFormFieldEntity.getFieldName());
				}

				if (StringUtil.isNotEmpty(cgFormFieldEntity.getFieldName()))
					cgFormFieldEntity.setFieldName(cgFormFieldEntity.getFieldName().trim());

			}
			cgFormFieldService.saveTable(cgFormHead);

			cgFormIndexService.updateIndexes(cgFormHead);

			systemService.addLog(message, Globals.Log_Type_INSERT,
					Globals.Log_Leavel_INFO);
		}
		logger.info("["+IpUtil.getIpAddr(request)+"][online??????????????????]"+message+"?????????"+cgFormHead.getTableName());
		j.setMsg(message);
		return j;
	}

	
	
	/**
	 * ?????????????????????????????????
	 * @param table
	 */
	private void syncTable(CgFormHeadEntity table) {
		List<CgFormHeadEntity> headList = systemService.findByProperty(CgFormHeadEntity.class, "physiceId", table.getId());
		List<CgFormFieldEntity>	formFieldEntities = table.getColumns();
		if(headList!=null&&headList.size()>0){
			for (CgFormHeadEntity cgform : headList) {
				List<CgFormFieldEntity> fieldList = new ArrayList<CgFormFieldEntity>();
				List<CgFormFieldEntity> columns = cgform.getColumns();
				if(columns==null||columns.size()<=0){
					for (CgFormFieldEntity column : formFieldEntities) {
						CgFormFieldEntity field = new CgFormFieldEntity();
						field.setContent(column.getContent());
						field.setDictField(column.getDictField());
						field.setDictTable(column.getDictTable());
						field.setDictText(column.getDictText());
						field.setExtendJson(column.getExtendJson());
						field.setFieldDefault(column.getFieldDefault());
						field.setFieldHref(column.getFieldHref());
						field.setFieldLength(column.getFieldLength());
						field.setFieldName(column.getFieldName());
						field.setFieldValidType(column.getFieldValidType());
						field.setLength(column.getLength());

						field.setMainField(null);
						field.setMainTable(null);

						field.setOldFieldName(column.getOldFieldName());
						field.setOrderNum(column.getOrderNum());
						field.setPointLength(column.getPointLength());
						field.setQueryMode(column.getQueryMode());
						field.setShowType(column.getShowType());
						field.setType(column.getType());
						field.setIsNull(column.getIsNull());
						field.setIsShow(column.getIsShow());
						field.setIsShowList(column.getIsShowList());
						field.setIsKey(column.getIsKey());
						field.setIsQuery(column.getIsQuery());
						fieldList.add(field);
					}
				}else{
					for (CgFormFieldEntity cgFormFieldEntity : formFieldEntities) {
						if(columns!=null&&columns.size()>0){
							for (CgFormFieldEntity column : columns) {
								//????????????????????????????????????
								if(cgFormFieldEntity.getFieldName().equals(column.getFieldName())){
									//?????????????????????list,???????????????remove;
									CgFormFieldEntity field = new CgFormFieldEntity();
									field.setContent(column.getContent());
									field.setDictField(column.getDictField());
									field.setDictTable(column.getDictTable());
									field.setDictText(column.getDictText());
									field.setExtendJson(column.getExtendJson());
									field.setFieldDefault(column.getFieldDefault());
									field.setFieldHref(column.getFieldHref());
									field.setFieldLength(column.getFieldLength());
									field.setFieldName(column.getFieldName());
									field.setFieldValidType(column.getFieldValidType());
									field.setLength(column.getLength());

									field.setMainField(null);
									field.setMainTable(null);

									field.setOldFieldName(column.getOldFieldName());
									field.setOrderNum(column.getOrderNum());
									field.setPointLength(column.getPointLength());
									field.setQueryMode(column.getQueryMode());
									field.setShowType(column.getShowType());
									field.setType(column.getType());
									field.setIsNull(cgFormFieldEntity.getIsNull());
									field.setIsShow(cgFormFieldEntity.getIsShow());
									field.setIsShowList(cgFormFieldEntity.getIsShowList());
									field.setIsKey(cgFormFieldEntity.getIsKey());
									field.setIsQuery(cgFormFieldEntity.getIsQuery());
									fieldList.add(field);
									columns.remove(column);
									//?????????????????????????????????
									break;
								}else{
									CgFormFieldEntity field = new CgFormFieldEntity();
									field.setContent(cgFormFieldEntity.getContent());
									field.setDictField(cgFormFieldEntity.getDictField());
									field.setDictTable(cgFormFieldEntity.getDictTable());
									field.setDictText(cgFormFieldEntity.getDictText());
									field.setExtendJson(cgFormFieldEntity.getExtendJson());
									field.setFieldDefault(cgFormFieldEntity.getFieldDefault());
									field.setFieldHref(cgFormFieldEntity.getFieldHref());
									field.setFieldLength(cgFormFieldEntity.getFieldLength());
									field.setFieldName(cgFormFieldEntity.getFieldName());
									field.setFieldValidType(cgFormFieldEntity.getFieldValidType());
									field.setLength(cgFormFieldEntity.getLength());

									field.setMainField(null);
									field.setMainTable(null);

									field.setOldFieldName(cgFormFieldEntity.getOldFieldName());
									field.setOrderNum(cgFormFieldEntity.getOrderNum());
									field.setPointLength(cgFormFieldEntity.getPointLength());
									field.setQueryMode(cgFormFieldEntity.getQueryMode());
									field.setShowType(cgFormFieldEntity.getShowType());
									field.setType(cgFormFieldEntity.getType());
									field.setIsNull(cgFormFieldEntity.getIsNull());
									field.setIsShow(cgFormFieldEntity.getIsShow());
									field.setIsShowList(cgFormFieldEntity.getIsShowList());
									field.setIsKey(cgFormFieldEntity.getIsKey());
									field.setIsQuery(cgFormFieldEntity.getIsQuery());
									fieldList.add(field);
									columns.remove(column);
									//?????????????????????????????????
									break;
								}
							}
						}else{
							CgFormFieldEntity field = new CgFormFieldEntity();
							field.setContent(cgFormFieldEntity.getContent());
							field.setDictField(cgFormFieldEntity.getDictField());
							field.setDictTable(cgFormFieldEntity.getDictTable());
							field.setDictText(cgFormFieldEntity.getDictText());
							field.setExtendJson(cgFormFieldEntity.getExtendJson());
							field.setFieldDefault(cgFormFieldEntity.getFieldDefault());
							field.setFieldHref(cgFormFieldEntity.getFieldHref());
							field.setFieldLength(cgFormFieldEntity.getFieldLength());
							field.setFieldName(cgFormFieldEntity.getFieldName());
							field.setFieldValidType(cgFormFieldEntity.getFieldValidType());
							field.setLength(cgFormFieldEntity.getLength());

							field.setMainField(null);
							field.setMainTable(null);

							field.setOldFieldName(cgFormFieldEntity.getOldFieldName());
							field.setOrderNum(cgFormFieldEntity.getOrderNum());
							field.setPointLength(cgFormFieldEntity.getPointLength());
							field.setQueryMode(cgFormFieldEntity.getQueryMode());
							field.setShowType(cgFormFieldEntity.getShowType());
							field.setType(cgFormFieldEntity.getType());
							field.setIsNull(cgFormFieldEntity.getIsNull());
							field.setIsShow(cgFormFieldEntity.getIsShow());
							field.setIsShowList(cgFormFieldEntity.getIsShowList());
							field.setIsKey(cgFormFieldEntity.getIsKey());
							field.setIsQuery(cgFormFieldEntity.getIsQuery());
							fieldList.add(field);
						}
					}
				}
				List<CgFormFieldEntity> colums = cgFormFieldService.findByProperty(CgFormFieldEntity.class, "table.id", cgform.getId());
				cgFormFieldService.deleteAllEntitie(colums);
				cgform.setColumns(fieldList);
				cgFormFieldService.saveTable(cgform);
			}
		}
	}

	
	/**
	 * ??????OrderNum
	 * @param cgFormHead
	 */
	private void refreshFormFieldOrderNum(CgFormHeadEntity cgFormHead) {
		Collections.sort(cgFormHead.getColumns(),new FieldNumComparator());
		for(int i = 0;i<cgFormHead.getColumns().size();i++){
			cgFormHead.getColumns().get(i).setOrderNum(i+1);
		}
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param cgFormHead
	 * @param oldTable 
	 * @return
	 */
	private CgFormHeadEntity judgeTableIsNotExit(CgFormHeadEntity cgFormHead, CgFormHeadEntity oldTable,StringBuffer msg) {
		String message = "";
		CgFormHeadEntity table = cgFormFieldService.findUniqueByProperty(CgFormHeadEntity.class, "tableName",cgFormHead.getTableName());
		if (StringUtil.isNotEmpty(cgFormHead.getId())) {
			if(table != null && !oldTable.getTableName().equals(cgFormHead.getTableName())){
				message = "???????????????????????????";
				table = null;
			}else{
				if(table == null){//???????????????
					cgFormHead.setIsDbSynch("N");
				}
				table = table == null?oldTable:table;
				try {
					MyBeanUtils.copyBeanNotNull2Bean(cgFormHead, table);
				} catch (Exception e) {
					e.printStackTrace();
				}
				message = "????????????";
			}
		} else {
			message = table != null? "???????????????":"????????????";
		}
		msg.append(message);
		return table;
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "addorupdate")
	public ModelAndView addorupdate(CgFormHeadEntity cgFormHead,
			HttpServletRequest req) {
		if (StringUtil.isNotEmpty(cgFormHead.getId())) {
			cgFormHead = cgFormFieldService.getEntity(
					CgFormHeadEntity.class, cgFormHead.getId());
			//??????jform????????????
			//cgFormHead.setTableName(cgFormHead.getTableName().replace(CgAutoListConstant.jform_, ""));
			req.setAttribute("cgFormHeadPage", cgFormHead);
		}

		List<TSType> typeList = ResourceUtil.allTypes.get(MutiLangUtil.getLang("bdfl"));
		req.setAttribute("typeList", typeList);

		return new ModelAndView("jeecg/cgform/config/cgFormHead");
	}
	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(params = "getColumnList")
	@ResponseBody
	public List<CgFormFieldEntity> getColumnList(CgFormHeadEntity cgFormHead,String type,
			HttpServletRequest req) {
		
		List<CgFormFieldEntity> columnList = new ArrayList<CgFormFieldEntity>();
		if (StringUtil.isNotEmpty(cgFormHead.getId())) {
			CriteriaQuery cq = new CriteriaQuery(CgFormFieldEntity.class);
			cq.eq("table.id", cgFormHead.getId());
			cq.add();
			columnList = cgFormFieldService
					.getListByCriteriaQuery(cq, false);
			//???????????????????????????
			Collections.sort(columnList,new FieldNumComparator());
		}else{
//			CgFormFieldEntity field = new CgFormFieldEntity();
//			field.setFieldName("id");
//			field.setLength(36);
//			field.setContent("??????");
//			field.setIsKey("Y");
//			field.setIsNull("N");
//			field.setOrderNum(1);
//			field.setType("string");
//			field.setPointLength(0);
//			field.setIsShow("N");
//			field.setIsShowList("N");
//			field.setFieldLength(120);
//			columnList.add(field);
			columnList=getInitDataList();
		}
		return columnList;
	}
	
	/**
	 * ??????????????????
	 * @return
	 */
	private List<CgFormFieldEntity>  getInitDataList(){
		List<CgFormFieldEntity> columnList = new ArrayList<CgFormFieldEntity>();
		
		columnList.add(initCgFormFieldEntityId());
		columnList.add(initCgFormFieldEntityString("create_name","???????????????"));
		columnList.add(initCgFormFieldEntityString("create_by", "?????????????????????"));
		columnList.add(initCgFormFieldEntityTime("create_date", "????????????"));
		columnList.add(initCgFormFieldEntityString("update_name","???????????????"));
		columnList.add(initCgFormFieldEntityString("update_by", "?????????????????????"));
		columnList.add(initCgFormFieldEntityTime("update_date", "????????????"));

		columnList.add(initCgFormFieldEntityString("sys_org_code","????????????"));
		columnList.add(initCgFormFieldEntityString("sys_company_code", "????????????"));

		columnList.add(initCgFormFieldEntityBpmStatus());

		return columnList;
	}
	/**
	 * ????????????id
	 * @return
	 */
	private  CgFormFieldEntity  initCgFormFieldEntityId(){
		CgFormFieldEntity field = new CgFormFieldEntity();
		field.setFieldName("id");
		field.setLength(36);
		field.setContent("??????");
		field.setIsKey("Y");
		field.setIsNull("N");
		field.setOrderNum(1);
		field.setType("string");
		field.setPointLength(0);
		field.setIsShow("N");
		field.setIsShowList("N");
		field.setFieldLength(120);
		return field;
	}
	
	/**
	 * ????????????id
	 * @return
	 */
	private  CgFormFieldEntity  initCgFormFieldEntityBpmStatus(){
		CgFormFieldEntity field = new CgFormFieldEntity();
		field.setFieldName("bpm_status");
		field.setLength(32);
		field.setContent("????????????");
		field.setIsKey("N");
		field.setIsNull("Y");
		field.setOrderNum(1);
		field.setType("string");
		field.setPointLength(0);
		field.setIsShow("N");
		field.setIsShowList("Y");
		field.setFieldLength(120);
		field.setDictField("bpm_status");
		field.setFieldDefault("1");
		return field;
	}

	/**
	 * ??????????????????
	 * @return
	 */
	private  CgFormFieldEntity  initCgFormFieldEntityString(String fieldName,String content){
		CgFormFieldEntity field = new CgFormFieldEntity();
		field.setFieldName(fieldName);
		field.setLength(50);
		field.setContent(content);
		field.setIsKey("N");
		field.setIsNull("Y");
		field.setOrderNum(2);
		field.setType("string");
		field.setPointLength(0);
		field.setIsShow("N");
		field.setIsShowList("N");
		field.setFieldLength(120);
		return field;
	}
	
	/**
	 * ??????????????????
	 * @return
	 */
	private  CgFormFieldEntity  initCgFormFieldEntityTime(String fieldName,String content){
		CgFormFieldEntity field = new CgFormFieldEntity();
		field.setFieldName(fieldName);
		field.setLength(20);
		field.setContent(content);
		field.setIsKey("N");
		field.setIsNull("Y");
		field.setOrderNum(3);
		field.setType("Date");
		field.setPointLength(0);
		field.setIsShow("N");
		field.setIsShowList("N");
		field.setFieldLength(120);
		field.setShowType("date");
		return field;
	}
	/**
	 * ??????????????????????????????
	 * 
	 * @return AjaxJson ??????success
	 */
	@RequestMapping(params = "checkIsExit")
	@ResponseBody
	public AjaxJson checkIsExit(String name,
			HttpServletRequest req) {
		AjaxJson j = new AjaxJson();

		//????????????????????????V?????????,????????????????????????
		name = PublicUtil.replaceTableName(name);

		j.setSuccess(cgFormFieldService.judgeTableIsExit(name));
		return j;
	}
	/**
	 * sql?????? ????????????
	 * @return
	 */
	@RequestMapping(params = "sqlPlugin")
	public ModelAndView sqlPlugin(String id,HttpServletRequest request) {
		CgFormHeadEntity bean = cgFormFieldService.getEntity(
				CgFormHeadEntity.class, id);
		request.setAttribute("bean", bean);
		return new ModelAndView("jeecg/cgform/config/cgFormSqlPlugin");
	}
	/**
	 * sql ????????????
	 * @param id ??????id
	 * @param sql_plug_in ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "sqlPluginSave")
	@ResponseBody
	public AjaxJson sqlPluginSave(String id,String sql_plug_in,
			HttpServletRequest request) {
		String message = "";
		CgFormHeadEntity bean = cgFormFieldService.getEntity(
				CgFormHeadEntity.class, id);
		//bean.setSqlPlugIn(sql_plug_in);
		cgFormFieldService.updateTable(bean,null,false);
		message = "????????????";
		systemService.addLog(message, Globals.Log_Type_INSERT,
				Globals.Log_Leavel_INFO);
		AjaxJson j =  new AjaxJson();
		j.setMsg(message);
		return j;
	}
	/**
	 * js?????? ????????????
	 * @return
	 */
	@RequestMapping(params = "jsPlugin")
	public ModelAndView jsPlugin(String id,HttpServletRequest request) {
		CgFormHeadEntity bean = cgFormFieldService.getEntity(
				CgFormHeadEntity.class, id);
		request.setAttribute("bean", bean);
		return new ModelAndView("jeecg/cgform/config/cgFormJsPlugin");
	}
	/**
	 * js ????????????
	 * @param id ??????id
	 * @param js_plug_in ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "jsPluginSave")
	@ResponseBody
	public AjaxJson jsPluginSave(String id,String js_plug_in,
			HttpServletRequest request) {
		String message = "";
		CgFormHeadEntity bean = cgFormFieldService.getEntity(
				CgFormHeadEntity.class, id);
		//bean.setJsPlugIn(js_plug_in);??????jsPlugIn????????????
		cgFormFieldService.updateTable(bean,null,false);
		message = "????????????";
		systemService.addLog(message, Globals.Log_Type_INSERT,
				Globals.Log_Leavel_INFO);
		AjaxJson j =  new AjaxJson();
		j.setMsg(message);
		return j;
	}
	
	
	@RequestMapping(params = "importExcel")
	@ResponseBody
	public AjaxJson importExcel(String headId,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// ????????????????????????
			ImportParams params = new ImportParams();
			params.setTitleRows(0);
			params.setHeadRows(1);
			params.setNeedSave(false);
			try {
				CgFormHeadEntity cgFormHead = systemService.getEntity(CgFormHeadEntity.class,headId);
				if(cgFormHead==null){
					j.setMsg("??????????????????");
					return j;
				}
				List<CgFormFieldVO> fieldList =  ExcelImportUtil.importExcel(file.getInputStream(),CgFormFieldVO.class,params);
				//??????headid??????????????????????????????
				String hql = "from CgFormFieldEntity where table.id = ? ";
				List<CgFormFieldEntity> list = systemService.findHql(hql, headId);
				if(list==null){
					list = new ArrayList<CgFormFieldEntity>();
				}
				CgFormFieldEntity fieldEntity = null;
				StringBuilder sb = new StringBuilder("");
				List<CgFormFieldEntity> saveList =  new ArrayList<CgFormFieldEntity>();
				for (CgFormFieldVO field : fieldList) {
					//System.out.println("-------------field------------"+field);
					if(StringUtil.isEmpty(field.getFieldName())){
						continue;
					}
					if(existField(field.getFieldName(),list)){
						sb.append(field.getFieldName()).append(",");
						continue;
					}
					fieldEntity = new CgFormFieldEntity();
					fieldEntity.setTable(cgFormHead);
					fieldEntity.setFieldName(field.getFieldName());
					String content = field.getContent();
					if(StringUtil.isEmpty(content)){
						content = field.getFieldName();
					}
					fieldEntity.setContent(content);
					String type = field.getType();
					if(StringUtil.isEmpty(type)){
						type = "string";
					}
					fieldEntity.setType(type);
					String length = field.getLength();
					if(StringUtil.isEmpty(length)){
						length = "32";
					}
					fieldEntity.setLength(Integer.valueOf(length));
					String pointLength = field.getPointLength();
					if(StringUtil.isEmpty(pointLength)){
						pointLength = "0";
					}
					fieldEntity.setPointLength(Integer.valueOf(pointLength));
					fieldEntity.setFieldDefault(field.getFieldDefault());
					fieldEntity.setIsKey("N");
					String isNull = field.getIsNull();
					if("???".equals(isNull)){
						isNull = "N";
					}else{
						isNull = "Y";
					}
					fieldEntity.setIsNull(isNull);
					fieldEntity.setOrderNum(1);
					fieldEntity.setIsShow("Y");
					fieldEntity.setIsShowList("Y");
					fieldEntity.setFieldLength(120);
					//--author???zhoujf---start------date:20170207--------for:online??????  ????????? ???????????? ???????????????
					fieldEntity.setIsQuery("N");
					fieldEntity.setShowType("text");
					fieldEntity.setOldFieldName(field.getFieldName());
					fieldEntity.setQueryMode("single");
					list.add(fieldEntity);
					saveList.add(fieldEntity);
				}
				systemService.batchSave(saveList);
				if(StringUtil.isEmpty(sb.toString())){
					j.setMsg("?????????????????????");
				}else{
					j.setMsg("????????????????????????????????????"+sb.toString()+"?????????");
				}
				
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
	
	private boolean existField(String field,List<CgFormFieldEntity> list){
		boolean flag = false;
		for(CgFormFieldEntity entity :list){
			if(field.equalsIgnoreCase(entity.getFieldName())){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * excel????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "upload")
	public String upload(String id,HttpServletRequest request) {
        request.setAttribute("headId", id);
		return "jeecg/cgform/config/cgformColUpload";
	}

	/**
	 * ??????????????????????????????
	 * copyOnline
	 */
	@RequestMapping(params = "copyOnline")
	@ResponseBody
	public AjaxJson copyOnline(String id,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		if(StringUtil.isNotEmpty(id)){
			String hql = "select max(c.tableVersion) from CgFormHeadEntity c where c.physiceId = ?";
			List<Integer> versions = systemService.findHql(hql, id);
			if(versions.get(0)!=null){
				int version = versions.get(0); 
				CgFormHeadEntity cgFormHead = new CgFormHeadEntity();
				CgFormHeadEntity physicsTable = systemService.get(CgFormHeadEntity.class,id);
				cgFormHead.setTableName(physicsTable.getTableName()+CgAutoListConstant.ONLINE_TABLE_SPLIT_STR+(version+1+""));
				cgFormHead.setIsTree(physicsTable.getIsTree());
				cgFormHead.setContent(physicsTable.getContent());
				cgFormHead.setJformPkType(physicsTable.getJformPkType());
				cgFormHead.setJformPkSequence(physicsTable.getJformPkSequence());
				cgFormHead.setQuerymode(physicsTable.getQuerymode());
				cgFormHead.setIsCheckbox(physicsTable.getIsCheckbox());
				cgFormHead.setIsPagination(physicsTable.getIsPagination());

				cgFormHead.setJformType(1);//????????????????????????

				cgFormHead.setJformCategory(physicsTable.getJformCategory());
				cgFormHead.setRelationType(physicsTable.getRelationType());

				cgFormHead.setSubTableStr(null);

				cgFormHead.setPhysiceId(physicsTable.getId());
				cgFormHead.setTabOrder(physicsTable.getTabOrder());
				cgFormHead.setTableVersion(version+1);
				cgFormHead.setTableType("1");
				cgFormHead.setIsDbSynch("N");
				cgFormHead.setJformVersion(physicsTable.getJformVersion());
				cgFormHead.setFormTemplate(physicsTable.getFormTemplate());
				cgFormHead.setFormTemplateMobile(physicsTable.getFormTemplateMobile());
				cgFormHead.setTreeFieldname(physicsTable.getTreeFieldname());
				cgFormHead.setTreeIdFieldname(physicsTable.getTreeIdFieldname());
				cgFormHead.setTreeParentIdFieldName(physicsTable.getTreeParentIdFieldName());
				List<CgFormFieldEntity> fieldList = new ArrayList<CgFormFieldEntity>();
				List<CgFormFieldEntity> columns = physicsTable.getColumns();
				for (CgFormFieldEntity f : columns) {
					CgFormFieldEntity field = new CgFormFieldEntity();
					field.setContent(f.getContent());
					field.setDictField(f.getDictField());
					field.setDictTable(f.getDictTable());
					field.setDictText(f.getDictText());
					field.setExtendJson(f.getExtendJson());
					field.setFieldDefault(f.getFieldDefault());
					field.setFieldHref(f.getFieldHref());
					field.setFieldLength(f.getFieldLength());
					field.setFieldName(f.getFieldName());
					field.setFieldValidType(f.getFieldValidType());
					field.setLength(f.getLength());

					field.setMainField(null);//???????????????
					field.setMainTable(null);//???????????????

					field.setOldFieldName(f.getOldFieldName());
					field.setOrderNum(f.getOrderNum());
					field.setPointLength(f.getPointLength());
					field.setQueryMode(f.getQueryMode());
					field.setShowType(f.getShowType());
					field.setType(f.getType());
					field.setIsNull(f.getIsNull());
					field.setIsShow(f.getIsShow());
					field.setIsShowList(f.getIsShowList());
					field.setIsKey(f.getIsKey());
					field.setIsQuery(f.getIsQuery());
					fieldList.add(field);
				}
				cgFormHead.setColumns(fieldList);
				cgFormFieldService.saveTable(cgFormHead);
					j.setObj(cgFormHead.getId());
					j.setMsg("?????????????????????:"+cgFormHead.getTableName()+"????????????");
					j.setSuccess(true);
					return j;
			}else{
				CgFormHeadEntity cgFormHead = new CgFormHeadEntity();
				CgFormHeadEntity physicsTable = systemService.get(CgFormHeadEntity.class,id);
				cgFormHead.setTableName(physicsTable.getTableName()+CgAutoListConstant.ONLINE_TABLE_SPLIT_STR+"0");
				cgFormHead.setIsTree(physicsTable.getIsTree());
				cgFormHead.setContent(physicsTable.getContent());
				cgFormHead.setJformPkType(physicsTable.getJformPkType());
				cgFormHead.setJformPkSequence(physicsTable.getJformPkSequence());
				cgFormHead.setQuerymode(physicsTable.getQuerymode());
				cgFormHead.setIsCheckbox(physicsTable.getIsCheckbox());
				cgFormHead.setIsPagination(physicsTable.getIsPagination());

				cgFormHead.setJformType(1);//????????????????????????

				cgFormHead.setJformCategory(physicsTable.getJformCategory());
				cgFormHead.setRelationType(physicsTable.getRelationType());

				cgFormHead.setSubTableStr(null);

				cgFormHead.setPhysiceId(physicsTable.getId());
				cgFormHead.setTabOrder(physicsTable.getTabOrder());
				cgFormHead.setTableVersion(0);
				cgFormHead.setTableType("1");
				cgFormHead.setIsDbSynch("N");
				cgFormHead.setJformVersion(physicsTable.getJformVersion());
				cgFormHead.setFormTemplate(physicsTable.getFormTemplate());
				cgFormHead.setFormTemplateMobile(physicsTable.getFormTemplateMobile());
				cgFormHead.setTreeFieldname(physicsTable.getTreeFieldname());
				cgFormHead.setTreeIdFieldname(physicsTable.getTreeIdFieldname());
				cgFormHead.setTreeParentIdFieldName(physicsTable.getTreeParentIdFieldName());
				List<CgFormFieldEntity> fieldList = new ArrayList<CgFormFieldEntity>();
				List<CgFormFieldEntity> columns = physicsTable.getColumns();
				for (CgFormFieldEntity f : columns) {
					CgFormFieldEntity field = new CgFormFieldEntity();
					field.setContent(f.getContent());
					field.setDictField(f.getDictField());
					field.setDictTable(f.getDictTable());
					field.setDictText(f.getDictText());
					field.setExtendJson(f.getExtendJson());
					field.setFieldDefault(f.getFieldDefault());
					field.setFieldHref(f.getFieldHref());
					field.setFieldLength(f.getFieldLength());
					field.setFieldName(f.getFieldName());
					field.setFieldValidType(f.getFieldValidType());
					field.setLength(f.getLength());

					field.setMainField(null);
					field.setMainTable(null);

					field.setOldFieldName(f.getOldFieldName());
					field.setOrderNum(f.getOrderNum());
					field.setPointLength(f.getPointLength());
					field.setQueryMode(f.getQueryMode());
					field.setShowType(f.getShowType());
					field.setType(f.getType());
					field.setIsNull(f.getIsNull());
					field.setIsShow(f.getIsShow());
					field.setIsShowList(f.getIsShowList());
					field.setIsKey(f.getIsKey());
					field.setIsQuery(f.getIsQuery());
					fieldList.add(field);
				}
				cgFormHead.setColumns(fieldList);
				cgFormFieldService.saveTable(cgFormHead);
				j.setObj(cgFormHead.getId());
				j.setMsg("????????????:"+cgFormHead.getTableName()+"????????????");
				j.setSuccess(true);
				return j;
			}
		}
		return j;
	}
	/**
	 * ??????????????????????????????
	 */
	@RequestMapping(params = "cgFormHeadConfigList")
	public ModelAndView cgFormHeadConfigList(String id,HttpServletRequest request) {
		if(StringUtil.isNotEmpty(id)){
			request.setAttribute("physiceId", id);
			return new ModelAndView("jeecg/cgform/config/cgFormHeadConfigList");
		}else{
			return null;
		}
	}
	
	/**
	 * ?????????????????????
	 */
	@RequestMapping(params = "configDatagrid")
	public void configDatagrid(CgFormHeadEntity cgFormHead,String id,
			HttpServletRequest request, HttpServletResponse response,
			DataGrid dataGrid) {
		String hql = "from CgFormHeadEntity c where c.physiceId = ? order by c.tableVersion asc";
		List<CgFormHeadEntity> findHql = systemService.findHql(hql, id);
		dataGrid.setResults(findHql);
		dataGrid.setTotal(findHql.size());
		TagUtil.datagrid(response, dataGrid);
	}
	
	/**
	 * ???????????????????????????
	 * 
	 */
	@RequestMapping(params = "getConfigId")
	@ResponseBody
	public AjaxJson getConfigId(String id,HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		if(StringUtil.isNotEmpty(id)){
			String hql = "from CgFormHeadEntity c where physiceId = ?";
			List<CgFormHeadEntity> cgformList = systemService.findHql(hql, id);
			if(cgformList!=null&&cgformList.size()>0){
				CgFormHeadEntity cgFormHeadEntity = cgformList.get(0);
				j.setSuccess(true);
				j.setObj(cgFormHeadEntity.getPhysiceId());
				return j;
			}else{
				j.setSuccess(false);
				j.setMsg("???????????????????????????");
				return j;
			}
		}
		return j;
	}

}
