package org.jeecgframework.web.cgform.controller.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.DataBaseConstant;
import org.jeecgframework.core.util.DBTypeUtil;
import org.jeecgframework.core.util.ExceptionUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.UUIDGenerator;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.jeecgframework.poi.excel.entity.vo.MapExcelConstants;
import org.jeecgframework.poi.handler.impl.ExcelDataHandlerDefaultImpl;
import org.jeecgframework.poi.util.PoiPublicUtil;
import org.jeecgframework.web.cgform.common.CgAutoListConstant;
import org.jeecgframework.web.cgform.entity.config.CgFormFieldEntity;
import org.jeecgframework.web.cgform.entity.config.CgFormHeadEntity;
import org.jeecgframework.web.cgform.service.autolist.CgTableServiceI;
import org.jeecgframework.web.cgform.service.autolist.ConfigServiceI;
import org.jeecgframework.web.cgform.service.build.DataBaseService;
import org.jeecgframework.web.cgform.service.config.CgFormFieldServiceI;
import org.jeecgframework.web.cgform.service.impl.config.util.FieldNumComparator;
import org.jeecgframework.web.cgform.util.QueryParamUtil;
import org.jeecgframework.web.system.pojo.base.DictEntity;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgreSQLSequenceMaxValueIncrementer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**
 * @author huiyong
 * @ClassName: excelTempletController
 * @Description: excel????????????
 */
//@Scope("prototype")
@Controller
@RequestMapping("/excelTempletController")
public class ExcelTempletController extends BaseController {
	private static final Logger logger = Logger
			.getLogger(ExcelTempletController.class);
	@Autowired
	private ConfigServiceI configService;
	@Autowired
	private CgFormFieldServiceI cgFormFieldService;
	@Autowired
	private DataBaseService dataBaseService;
	@Autowired
	private CgTableServiceI cgTableService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private AbstractRoutingDataSource dataSource;


	/**
	 * ??????excel??????
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("all")
	@RequestMapping(params = "exportXls")

	public String exportXls(HttpServletRequest request, ModelMap modelMap,
							HttpServletResponse response, String field, DataGrid dataGrid,String id) {//update-begin--Author:dangzhenghui  Date:20170429 for???TASK #1906 ???online excel???Online excel ??????????????????


		String codedFileName = "??????";
		String sheetName = "????????????";
		List<CgFormFieldEntity> lists = null;
		if (StringUtil.isNotEmpty(request.getParameter("tableName"))) {
			String configId = request.getParameter("tableName");
			String jversion = cgFormFieldService.getCgFormVersionByTableName(configId);
			Map<String, Object> configs = configService.queryConfigs(configId, jversion);
			Map params = new HashMap<String, Object>();
			//step.2 ???????????????????????????
			List<CgFormFieldEntity> beans = (List<CgFormFieldEntity>) configs.get(CgAutoListConstant.FILEDS);
			for (CgFormFieldEntity b : beans) {
//				if(CgAutoListConstant.BOOL_TRUE.equals(b.getIsQuery())){
				QueryParamUtil.loadQueryParams(request, b, params);
//				}
			}
			//--author???zhoujf---start------date:20170207--------for:online???????????????????????????????????????
			configId = configId.split("__")[0];
			List<Map<String, Object>> result = cgTableService.querySingle(configId, field.toString(), params, null, null, 1, 99999);

			//???????????????
			lists = (List<CgFormFieldEntity>) configs.get(CgAutoListConstant.FILEDS);
			for (int i = lists.size() - 1; i >= 0; i--) {
				if (!field.contains(lists.get(i).getFieldName())) {
					lists.remove(i);
				}
			}
			handlePageDic(beans, result);
			//???????????????
			dealDic(result, beans);
			//??????????????????
			sheetName = (String) configs.get(CgAutoListConstant.CONFIG_NAME);
			//??????????????????
			String tableName = (String) configs.get(CgAutoListConstant.TABLENAME);
			//?????????????????? form???????????????-v?????????.xsl
			codedFileName = sheetName + "_" + tableName + "-v" + (String) configs.get(CgAutoListConstant.CONFIG_VERSION);

			//--author???JueYue---------date:20150615--------for: ???????????????EasyPoi
			List<ExcelExportEntity> entityList = convertToExportEntity(lists);

			//?????????????????????
			if (CgAutoListConstant.JFORM_TYPE_MAIN_TALBE == Integer.parseInt(configs.get(CgAutoListConstant.TABLE_TYPE).toString())) {
				String subTableStr = configs.get(CgAutoListConstant.SUB_TABLES).toString();
				if (StringUtils.isNotEmpty(subTableStr)) {
					String[] subTables = subTableStr.split(",");
					for (String subTable : subTables) {
						addAllSubTableDate(subTable, configs, result, entityList);
					}
				}
			}

			List<Map<String, Object>> nresult=new ArrayList<Map<String, Object>>();
			if (StringUtil.isNotEmpty(id)){
				for(Map map:result){

					if(id.contains(map.get("id").toString())){
						nresult.add(map);
					}

				}
			}else {
				nresult.addAll(result);
			}

			modelMap.put(MapExcelConstants.ENTITY_LIST, entityList);
			modelMap.put(MapExcelConstants.MAP_LIST, nresult);
			modelMap.put(MapExcelConstants.FILE_NAME, codedFileName);
			modelMap.put(MapExcelConstants.PARAMS, new ExportParams(null, sheetName));
			return MapExcelConstants.JEECG_MAP_EXCEL_VIEW;
			//--author???JueYue---------date:20150615--------for: ???????????????EasyPoi
		} else {
			throw new BusinessException("????????????");
		}

	}

	/**
	 * ????????????????????????Excel???????????????
	 *
	 * @param lists
	 * @return
	 */
	private List<ExcelExportEntity> convertToExportEntity(List<CgFormFieldEntity> lists) {
		// ???????????????????????????
		Collections.sort(lists, new FieldNumComparator());
		List<ExcelExportEntity> entityList = new ArrayList<ExcelExportEntity>();
		for (int i = 0; i < lists.size(); i++) {
			if (lists.get(i).getIsShow().equals("Y")) {
				ExcelExportEntity entity = new ExcelExportEntity(lists.get(i).getContent(), lists.get(i).getFieldName());
				int columnWidth = lists.get(i).getLength() == 0 ? 12 : lists.get(i).getLength() > 30 ? 30 : lists.get(i).getLength();
				if (lists.get(i).getShowType().equals("date")) {
					entity.setFormat("yyyy-MM-dd");
				} else if (lists.get(i).getShowType().equals("datetime")) {
					entity.setFormat("yyyy-MM-dd HH:mm:ss");
				}
				entity.setWidth(columnWidth);
				entityList.add(entity);
			}
		}
		return entityList;
	}

	/**
	 * ??????????????????????????????
	 *
	 * @param subTable
	 * @param configs
	 * @param result
	 * @param entityList
	 */
	private void addAllSubTableDate(String subTable, Map<String, Object> configs, List<Map<String, Object>> result, List<ExcelExportEntity> entityList) {
		String jversion = cgFormFieldService.getCgFormVersionByTableName(subTable);
		Map<String, Object> subConfigs = configService.queryConfigs(subTable, jversion);
		ExcelExportEntity tableEntity = new ExcelExportEntity(subConfigs.get(CgAutoListConstant.CONFIG_NAME).toString(), subTable);
		List<CgFormFieldEntity> beans = (List<CgFormFieldEntity>) subConfigs.get(CgAutoListConstant.FILEDS);
		tableEntity.setList(convertToExportEntity(beans));
		entityList.add(tableEntity);
		// ???????????????????????????
		for (int i = 0; i < result.size(); i++) {
			List<Map<String, Object>> subResult = cgFormFieldService.getSubTableData(configs.get(CgAutoListConstant.CONFIG_ID).toString(), subTable, result.get(i).get("id"));
			handlePageDic(beans, subResult);
			dealDic(subResult, beans);
			result.get(i).put(subTable,subResult);
		}
	}

	/**
	 * ????????????????????????checkbox????????????code??????????????????text?????????
	 *
	 * @param beans
	 * @param result
	 */
	private void handlePageDic(List<CgFormFieldEntity> beans, List<Map<String, Object>> result) {
		Map<String, Object> dicMap = new HashMap<String, Object>();
		for (CgFormFieldEntity b : beans) {
			loadDic(dicMap, b);
			List<DictEntity> dicList = (List<DictEntity>) dicMap.get(CgAutoListConstant.FIELD_DICTLIST);
			if (dicList.size() > 0) {
				for (Map<String, Object> resultMap : result) {
					StringBuffer sb = new StringBuffer();
					String value = (String) resultMap.get(b.getFieldName());
					if (oConvertUtils.isEmpty(value)) {
						continue;
					}
					String[] arrayVal = value.split(",");
					if (arrayVal.length > 1) {
						for (String val : arrayVal) {
							for (DictEntity dictEntity : dicList) {
								if (val.equals(dictEntity.getTypecode())) {
									sb.append(dictEntity.getTypename());
									sb.append(",");
								}

							}
						}
						resultMap.put(b.getFieldName(), sb.toString().substring(0, sb.toString().length() - 1));
					}

				}
			}
		}
	}

	@RequestMapping(params = "goImplXls", method = RequestMethod.GET)
	public ModelAndView goImplXls(HttpServletRequest request) {
		request.setAttribute("tableName", request.getParameter("tableName"));
		return new ModelAndView("jeecg/cgform/excel/upload");
	}

	/**
	 * ??????????????????
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(params = "importExcel", method = RequestMethod.POST)
	@ResponseBody
	@SuppressWarnings("all")
	public AjaxJson importExcel(HttpServletRequest request, HttpServletResponse response) {
		String message = "????????????";
		AjaxJson j = new AjaxJson();
		String configId = request.getParameter("tableName");
		String jversion = cgFormFieldService.getCgFormVersionByTableName(configId);
		Map<String, Object> configs = configService.queryConfigs(configId, jversion);
		//?????????????????????
		String version = (String) configs.get(CgAutoListConstant.CONFIG_VERSION);
		List<CgFormFieldEntity> lists = (List<CgFormFieldEntity>) configs.get(CgAutoListConstant.FILEDS);
		Object subTables = configs.get("subTables");
		List<String> subTabList = new ArrayList();
		if (null != subTables) {
			subTabList.addAll(Arrays.asList(subTables.toString().split(",")));
		}
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// ????????????????????????
			//????????????????????????
			String docVersion = getDocVersion(file.getOriginalFilename());
			if (docVersion.equals(version)) {
				List<Map<String, Object>> listDate;
				//--author???luobaoli---------date:20150615--------for: ??????service??????????????????
				try {
					//??????excel????????????
					//--author???JueYue---------date:20150622--------for: ?????????EasyPoi ?????????
					ImportParams params = new ImportParams();
					params.setDataHanlder(new CgFormExcelHandler(lists));
					listDate = ExcelImportUtil.importExcel(file.getInputStream(), Map.class, params);
					//--author???JueYue---------date:20150622--------for: ?????????EasyPoi ?????????
					if (listDate == null) {
						message = "????????????????????????";
						logger.error(message);
					} else {
						//--author???zhoujf---start------date:20170207--------for:online???????????????????????????????????????
						configId = configId.split("__")[0];

//						String mainId = "";
						Object mainId = "";
						for (Map<String, Object> map : listDate) {
							//???????????????????????????
							boolean isMainData = false;
							Set<String> keySet = map.keySet();
							Map mainData = new HashMap();
							for (String key : keySet) {
								if (key.indexOf("$subTable$")==-1) {
									if (key.indexOf("$mainTable$")!=-1 && StringUtils.isNotEmpty(map.get(key).toString())) {
										isMainData = true;
//										mainId = UUIDGenerator.generate();
										mainId = getPkValue(configId);
									}
									mainData.put(key.replace("$mainTable$", ""), map.get(key));
								}
							}

//							map.put("$mainTable$id", mainId);//???????????????
							if (isMainData) {

								//???????????????
								dealDicForImport(mainData, lists);

								mainData.put("id", mainId);//????????????
								dataBaseService.insertTable(configId, mainData);
								mainId =  mainData.get("id");
							}
							map.put("$mainTable$id", mainId);//???????????????

						}

						//??????????????????????????????
						for (String subConfigId: subTabList) {
							Map<String, Object> subConfigs = configService.queryConfigs(subConfigId, jversion);
							List<CgFormFieldEntity> subLists = (List<CgFormFieldEntity>) subConfigs.get(CgAutoListConstant.FILEDS);
							//??????????????????????????????????????????????????????
							String configName = subConfigs.get("config_name").toString();
							for (Map<String, Object> map : listDate) {
								//???????????????????????????
								boolean isSubData = false;
								Map subData = new HashMap();
								for (CgFormFieldEntity fieldEntity: subLists) {
									String mainTab = fieldEntity.getMainTable();
									String mainField = fieldEntity.getMainField();
									boolean isForeignKey = configId.equals(mainTab) && StringUtil.isNotEmpty(mainField);
									String tempKey = configName+"_"+fieldEntity.getContent();
									//???????????????????????????????????????
									if(isForeignKey){
										subData.put(fieldEntity.getFieldName(), map.get("$mainTable$"+mainField));
									}
									Object subObj = map.get("$subTable$"+tempKey);
									if (null != subObj && StringUtils.isNotEmpty(subObj.toString())) {
										isSubData = true;
										//System.out.println(tempKey+"=>"+fieldEntity.getFieldName()+"--"+subObj);
										subData.put(fieldEntity.getFieldName(), subObj);
									}
								}
								//??????????????????ID
								if (isSubData) {

									//???????????????
									dealDicForImport(subData, subLists);

//									subData.put("id", UUIDGenerator.generate());
									subData.put("id", getPkValue(subConfigId));

									dataBaseService.insertTable(subConfigId, subData);
								}
							}
						}

						message = "?????????????????????";
					}
				} catch (Exception e) {
					message = "?????????????????????";
					logger.error(ExceptionUtil.getExceptionMessage(e));
				}
				//--author???luobaoli---------date:20150615--------for: ??????service??????????????????
			} else {
				message = "????????????????????????????????????????????????????????????";
				logger.error(message);
			}
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * ????????????????????????????????????????????????
	 * @param tableName ????????????
	 * @return
	 */
	public Object getPkValue(String tableName) {
		Object pkValue = null;
		CgFormHeadEntity  cghead = cgFormFieldService.getCgFormHeadByTableName(tableName);
		String dbType = DBTypeUtil.getDBType();
		String pkType = cghead.getJformPkType();
		String pkSequence = cghead.getJformPkSequence();
		if(StringUtil.isNotEmpty(pkType)&&"UUID".equalsIgnoreCase(pkType)){
			pkValue = UUIDGenerator.generate();
		}else if(StringUtil.isNotEmpty(pkType)&&"NATIVE".equalsIgnoreCase(pkType)){
			if(StringUtil.isNotEmpty(dbType)&&"oracle".equalsIgnoreCase(dbType)){
				OracleSequenceMaxValueIncrementer incr = new OracleSequenceMaxValueIncrementer(dataSource, "HIBERNATE_SEQUENCE");
				try{
					pkValue = incr.nextLongValue();
				}catch (Exception e) {
					logger.error(e,e);
				}
			}else if(StringUtil.isNotEmpty(dbType)&&"postgres".equalsIgnoreCase(dbType)){
				PostgreSQLSequenceMaxValueIncrementer incr = new PostgreSQLSequenceMaxValueIncrementer(dataSource, "HIBERNATE_SEQUENCE");
				try{
					pkValue = incr.nextLongValue();
				}catch (Exception e) {
					logger.error(e,e);
				}
			}else{
				pkValue = null;
			}
		}else if(StringUtil.isNotEmpty(pkType)&&"SEQUENCE".equalsIgnoreCase(pkType)){
			if(StringUtil.isNotEmpty(dbType)&&"oracle".equalsIgnoreCase(dbType)){
				OracleSequenceMaxValueIncrementer incr = new OracleSequenceMaxValueIncrementer(dataSource, pkSequence);
				try{
					pkValue = incr.nextLongValue();
				}catch (Exception e) {
					logger.error(e,e);
				}
			}else if(StringUtil.isNotEmpty(dbType)&&"postgres".equalsIgnoreCase(dbType)){
				PostgreSQLSequenceMaxValueIncrementer incr = new PostgreSQLSequenceMaxValueIncrementer(dataSource, pkSequence);
				try{
					pkValue = incr.nextLongValue();
				}catch (Exception e) {
					logger.error(e,e);
				}
			}else{
				pkValue = null;
			}
		}else{
			pkValue = UUIDGenerator.generate();
		}
		return pkValue;
	}


	/**
	 * ??????????????????????????????
	 * ????????????????????? form???????????????-v?????????.xsl
	 * ?????????????????? form???????????????-v?????????(1).xsl
	 *
	 * @param docName
	 * @return
	 */
	private static String getDocVersion(String docName) {
		//--author???JueYue---------date:20150621--------for: ??????????????????
		if (docName.indexOf("(") > 0) {
			return docName.substring(docName.indexOf("-v") + 2, docName.indexOf("(")).trim();
		} else {
			return docName.substring(docName.indexOf("-v") + 2, docName.indexOf(".")).trim();
		}
		//--author???JueYue---------date:20150621--------for: ??????????????????
	}

	private void loadDic(Map m, CgFormFieldEntity bean) {
		String dicT = bean.getDictTable();//??????Table
		String dicF = bean.getDictField();//??????Code
		String dicText = bean.getDictText();//??????Text
		if (StringUtil.isEmpty(dicT) && StringUtil.isEmpty(dicF)) {
			//??????????????????????????????????????????????????????
			m.put(CgAutoListConstant.FIELD_DICTLIST, new ArrayList(0));
			return;
		}
		if (!bean.getShowType().equals("popup")) {
			List<DictEntity> dicDatas = queryDic(dicT, dicF, dicText);
			m.put(CgAutoListConstant.FIELD_DICTLIST, dicDatas);
		} else {
			m.put(CgAutoListConstant.FIELD_DICTLIST, new ArrayList(0));
		}
	}

	private List<DictEntity> queryDic(String dicTable, String dicCode, String dicText) {
		return this.systemService.queryDict(dicTable, dicCode, dicText);
	}

	/**
	 * ??????????????????
	 *
	 * @param result ??????????????????
	 * @param beans  ????????????
	 */
	@SuppressWarnings("unchecked")
	private void dealDic(List<Map<String, Object>> result,
						 List<CgFormFieldEntity> beans) {
		for (CgFormFieldEntity bean : beans) {
			String dicTable = bean.getDictTable();//??????Table
			String dicCode = bean.getDictField();//??????Code
			String dicText = bean.getDictText();//??????text
			if (StringUtil.isEmpty(dicTable) && StringUtil.isEmpty(dicCode)) {
				//?????????????????????
				continue;
			} else {
				if (!bean.getShowType().equals("popup")) {
					List<DictEntity> dicDataList = queryDic(dicTable, dicCode, dicText);
					for (Map r : result) {
						String value = String.valueOf(r.get(bean.getFieldName()));
						for (DictEntity dictEntity : dicDataList) {
							if (value.equalsIgnoreCase(dictEntity.getTypecode())) {

								r.put(bean.getFieldName(), MutiLangUtil.getLang(dictEntity.getTypename()));

							}
						}
					}
				}
			}
		}
	}

	/**
	 * ??????????????????
	 * @param result ??????????????????
	 * @param beans  ????????????
	 */
	@SuppressWarnings("unchecked")
	private void dealDicForImport(Map result,List<CgFormFieldEntity> beans) {
		for (CgFormFieldEntity bean : beans) {
			String dicTable = bean.getDictTable();//??????Table
			String dicCode = bean.getDictField();//??????Code
			String dicText = bean.getDictText();//??????text
			if (StringUtil.isEmpty(dicTable) && StringUtil.isEmpty(dicCode)) {
				//?????????????????????
				continue;
			} else {
				if (!bean.getShowType().equals("popup")) {
					List<DictEntity> dicDataList = queryDic(dicTable, dicCode, dicText);
						String value = String.valueOf(result.get(bean.getFieldName()));
						for (DictEntity dictEntity : dicDataList) {
							if (value.equals(dictEntity.getTypename())) {
								result.put(bean.getFieldName(), dictEntity.getTypecode());
							}
						}
				}
			}
		}
	}

	
	
	private class CgFormExcelHandler extends ExcelDataHandlerDefaultImpl {

		Map<String, CgFormFieldEntity> fieldMap;

		public CgFormExcelHandler(List<CgFormFieldEntity> lists) {
			fieldMap = convertDate(lists);
		}

		private Map<String, CgFormFieldEntity> convertDate(List<CgFormFieldEntity> lists) {
			Map<String, CgFormFieldEntity> maps = new HashMap<String, CgFormFieldEntity>();

			for (CgFormFieldEntity cgFormFieldEntity : lists) {
				maps.put(cgFormFieldEntity.getContent(), cgFormFieldEntity);
			}
			return maps;
		}


		@Override
		public void setMapValue(Map<String, Object> map, String originKey, Object value) {
			if (value instanceof Double) {
				map.put(getRealKey(originKey), PoiPublicUtil.doubleToString((Double) value));
			} else {
				map.put(getRealKey(originKey), value.toString());
			}
		}

		private String getRealKey(String originKey) {
			if (fieldMap.containsKey(originKey)) {
				//????????????
				return "$mainTable$"+fieldMap.get(originKey).getFieldName();
			}
			//????????????
			return "$subTable$"+originKey;
		}


	}
}
