package com.jeecg.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.common.UploadFile;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.ComboTree;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.common.model.json.TreeGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil;
import org.jeecgframework.core.util.DateUtils;
import org.jeecgframework.core.util.HttpRequest;
import org.jeecgframework.core.util.JSONHelper;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.MyClassLoader;
import org.jeecgframework.core.util.NumberComparator;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.tag.vo.datatable.SortDirection;
import org.jeecgframework.tag.vo.easyui.ComboTreeModel;
import org.jeecgframework.tag.vo.easyui.TreeGridModel;
import org.jeecgframework.web.system.pojo.base.TSAttachment;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.pojo.base.TSFunction;
import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSTypegroup;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.jeecg.demo.dao.JeecgMinidaoDao;
import com.jeecg.demo.entity.TSDocument;

/**
 * @ClassName: JeecgFormDemoController
 * @Description: TODO(?????????????????????)
 * @author jeecg
 */
@Controller
@RequestMapping("/jeecgFormDemoController")
public class JeecgFormDemoController extends BaseController {
	private static final Logger logger = Logger.getLogger(JeecgFormDemoController.class);
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private JeecgMinidaoDao jeecgMinidaoDao;
	
	@RequestMapping(params = "uitag")
	public ModelAndView uitag(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/demo/form_uitag");
	}

	@RequestMapping(params = "qrcode")
	public ModelAndView qrcode(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/demo/form_QRCode");
	}


	@RequestMapping(params = "formValidDemo")
	public ModelAndView formValidDemo(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/demo/form_valid");
	}

	@RequestMapping(params = "testsubmit=1",method ={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView testsubmit(HttpServletRequest request) {
		logger.info("????????????byebye");
		return new ModelAndView("com/jeecg/demo/form_valid");
	}
	
	@RequestMapping(params = "nature")
	public ModelAndView easyDemo(HttpServletRequest request) {
		logger.info("demo-nature");
		//ztree????????????
		JSONArray jsonArray=JSONArray.fromObject(getZtreeData());
		request.setAttribute("regions", jsonArray.toString().replaceAll("pid","pId"));
		return new ModelAndView("com/jeecg/demo/form_nature");
	}

	@RequestMapping(params = "ueditor")
	public ModelAndView ueditor(HttpServletRequest request) {
		logger.info("ueditor");
		return new ModelAndView("com/jeecg/demo/ueditor");
	}

	@RequestMapping(params = "popupMultiValue")
	public ModelAndView popupMultiValue(HttpServletRequest request) {
		logger.info("popupMultiValue");
		return new ModelAndView("com/jeecg/demo/form_popupMultiValue");
	}


	/**
	 *??????????????????---?????????
	 */
	@RequestMapping(params="regionSelect",method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> cityselect(HttpServletRequest req) throws Exception{
		logger.info("----???????????????-----");
		String pid=req.getParameter("pid");
		List<Map<String, String>> list=jeecgMinidaoDao.getProCity(pid);
		return jeecgMinidaoDao.getProCity(pid);
	}
	
	/**
	 * Ztree
	 * ??????????????????????????????
	 * @return
	 */
	public List<Map<String, String>> getZtreeData(){
		return jeecgMinidaoDao.getAllRegions();
	}
	
	/**
	 * ??????DEMO????????????
	 */
	@RequestMapping(params = "getComboTreeData")
	@ResponseBody
	public List<ComboTree> getComboTreeData(HttpServletRequest request, ComboTree comboTree) {
		CriteriaQuery cq = new CriteriaQuery(TSDepart.class);
		if (comboTree.getId() != null) {
			cq.eq("TSPDepart.id", comboTree.getId());
		}
		if (comboTree.getId() == null) {
			cq.isNull("TSPDepart");
		}
		cq.add();
		List<TSDepart> demoList = systemService.getListByCriteriaQuery(cq, false);
		List<ComboTree> comboTrees = new ArrayList<ComboTree>();
		ComboTreeModel comboTreeModel = new ComboTreeModel("id", "departname", "TSDeparts");
		comboTrees = systemService.ComboTree(demoList, comboTreeModel, null, false);
		return comboTrees;
	}
	
	/**
	 * ??????ztree
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(params="getTreeData",method ={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public AjaxJson getTreeData(TSDepart depatr,HttpServletResponse response,HttpServletRequest request ){
		AjaxJson j = new AjaxJson();
		try{
			List<TSDepart> depatrList = new ArrayList<TSDepart>();
			StringBuffer hql = new StringBuffer(" from TSDepart t");
			//hql.append(" and (parent.id is null or parent.id='')");
			depatrList = this.systemService.findHql(hql.toString());
			List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = null;
			for (TSDepart tsdepart : depatrList) {
				String sqls = null;
				Object[] paramss = null;
				map = new HashMap<String,Object>();
				map.put("id", tsdepart.getId());
				map.put("name", tsdepart.getDepartname());
				if (tsdepart.getTSPDepart() != null) {
					map.put("pId", tsdepart.getTSPDepart().getId());
					map.put("open",false);
				}else {
					map.put("pId", "1");
					map.put("open",false);
				}
				sqls = "select count(1) from t_s_depart t where t.parentdepartid = ?";
				paramss = new Object[]{tsdepart.getId()};
				long counts = this.systemService.getCountForJdbcParam(sqls, paramss);
				if(counts>0){
					dataList.add(map);
				}else{
					TSDepart de = this.systemService.get(TSDepart.class, tsdepart.getId());
					if (de != null) {
						map.put("id", de.getId());
						map.put("name", de.getDepartname());
						if(tsdepart.getTSPDepart()!=null){
							map.put("pId", tsdepart.getTSPDepart().getId());
							map.put("open",false);
						}else{
							map.put("pId", "1");
							map.put("open",false);
						}
						dataList.add(map);
					}else{
						map.put("open",false);
						dataList.add(map);
					}
				}
			}
		j.setObj(dataList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return j;
	}
	
	
	/**
	 * ??????????????????????????????
	 * @param request
	 * @param responss
	 */
	@RequestMapping(params = "getAutocompleteData",method ={RequestMethod.GET, RequestMethod.POST})
	public void getAutocompleteData(HttpServletRequest request, HttpServletResponse response) {

		String searchVal = request.getParameter("q");

		String hql = "from TSUser where userName like '%"+searchVal+"%'";
		List autoList = systemService.findHql(hql);
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.getWriter().write(JSONHelper.listtojson(new String[]{"userName"},1,autoList));
            response.getWriter().flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}finally{
			try {
				response.getWriter().close();
			} catch (IOException e) {
			}
		}

	}

	@RequestMapping(params = "eSign")
	public ModelAndView eSignDemo(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/demo/zsign");
	}

	@RequestMapping(params = "siteSelect")
	public ModelAndView siteSelect(HttpServletRequest request) {
		logger.info("----???????????? demo????????????-----");
		return new ModelAndView("com/jeecg/demo/siteSelect");
	}	

	/**
	 * ??????????????????
	 */
	@RequestMapping(params = "specialLayout")
	public ModelAndView rowListDemo(HttpServletRequest request) {
		logger.info("----?????????????????? demo????????????-----");
		return new ModelAndView("com/jeecg/demo/specialLayout");
	}


	@RequestMapping(params = "commonUpload")
	public ModelAndView commonUploadDemo(){
		return new ModelAndView("system/commonupload/commonUploadFile");
	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(params = "saveUploadFile")
	@ResponseBody
	public AjaxJson saveUploadFile(String documentTitle,String filename,String swfpath){
		AjaxJson ajaxJson = new AjaxJson();
		try {

			if(StringUtil.isEmpty(filename)){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("???????????????");
				return ajaxJson;
			}

			TSTypegroup tsTypegroup=systemService.getTypeGroup("fieltype","????????????");
			TSType tsType = systemService.getType("files","??????", tsTypegroup);
			TSDocument document = new TSDocument();
			document.setDocumentTitle(documentTitle);
			document.setRealpath(filename);
			document.setSubclassname(MyClassLoader.getPackPath(document));
			document.setCreatedate(DateUtils.gettimestamp());
			document.setTSType(tsType);
			document.setSwfpath(swfpath);

			String fileName = filename.substring(filename.lastIndexOf("/")+1,filename.lastIndexOf("."));
			document.setAttachmenttitle(fileName);
			document.setExtend(filename.substring(filename.lastIndexOf(".") + 1));

			systemService.save(document);
		} catch (Exception e) {
			e.printStackTrace();
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("?????????"+e.getMessage());
		}
		return ajaxJson;
	}



	/**
	 * ??????????????????
	 *
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "addFiles")
	public ModelAndView addFiles(HttpServletRequest req) {
		return new ModelAndView("system/document/files");
	}

	/**
	 * ??????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "editFiles")
	public ModelAndView editFiles(TSDocument doc, ModelMap map,HttpServletRequest request) {
		if (StringUtil.isNotEmpty(doc.getId())) {
			doc = systemService.getEntity(TSDocument.class, doc.getId());
			map.put("doc", doc);
			TSAttachment attachment = systemService.get(TSAttachment.class, doc.getId());
			map.put("attachment",attachment);
		}
		return new ModelAndView("system/document/files");
	}

	/**
	 * ????????????
	 *
	 * @param document
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "saveFiles", method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson saveFiles(HttpServletRequest request, HttpServletResponse response, TSDocument document) {
		AjaxJson j = new AjaxJson();
		Map<String, Object> attributes = new HashMap<String, Object>();
		TSTypegroup tsTypegroup=systemService.getTypeGroup("fieltype","????????????");
		TSType tsType = systemService.getType("files","??????", tsTypegroup);

		String documentId = oConvertUtils.getString(request.getParameter("documentId"));// ??????ID
		String documentTitle = oConvertUtils.getString(request.getParameter("documentTitle"));// ????????????
		if (StringUtil.isNotEmpty(documentId)) {
			document.setId(documentId);
			document = systemService.getEntity(TSDocument.class, documentId);
			document.setDocumentTitle(documentTitle);
		}

		document.setSubclassname(MyClassLoader.getPackPath(document));
		document.setCreatedate(DateUtils.gettimestamp());
		document.setTSType(tsType);
		UploadFile uploadFile = new UploadFile(request, document);
		uploadFile.setCusPath("files");
		//??????weboffice????????????????????????????????????????????????????????????
		uploadFile.setSwfpath("swfpath");
		document = systemService.uploadFile(uploadFile);
		attributes.put("url", document.getRealpath());
		attributes.put("fileKey", document.getId());
		attributes.put("name", document.getAttachmenttitle());
		attributes.put("viewhref", "commonController.do?objfileList&fileKey=" + document.getId());
		attributes.put("delurl", "commonController.do?delObjFile&fileKey=" + document.getId());
		j.setMsg("??????????????????");
		j.setAttributes(attributes);
		return j;
	}
	
	/**
	 * ????????????????????????
	 */
	@RequestMapping(params = "documentList")
	public void documentList(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSDocument.class, dataGrid);
		String typecode = oConvertUtils.getString(request.getParameter("typecode"));
		cq.createAlias("TSType", "TSType");
		cq.eq("TSType.typecode", typecode);
		cq.add();
		this.systemService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * ????????????
	 *
	 * @param document
	 * @return
	 */
	@RequestMapping(params = "delDocument")
	@ResponseBody
	public AjaxJson delDocument(TSDocument document, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		document = systemService.getEntity(TSDocument.class, document.getId());
		message = "" + document.getDocumentTitle() + "???????????????";
		systemService.delete(document);
		systemService.addLog(message, Globals.Log_Type_DEL,
				Globals.Log_Leavel_INFO);
		j.setSuccess(true);
		j.setMsg(message);
		return j;
	}

	/**
	 * ????????????
	 */
	@RequestMapping(params = "functionGrid")
	@ResponseBody
	public  Object functionGrid(HttpServletRequest request,TreeGrid treegrid, Integer type,HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSFunction.class,dataGrid);
		boolean pageflag=true;
		String selfId = request.getParameter("selfId");
		if (selfId != null) {
			cq.notEq("id", selfId);
		}
		if (treegrid.getId() != null) {
			pageflag=false;
			cq.eq("TSFunction.id", treegrid.getId());
		}
		if (treegrid.getId() == null) {
			cq.isNull("TSFunction");
		}
		if(type != null){
			cq.eq("functionType", type.shortValue());
		}
		cq.addOrder("functionOrder", SortDirection.asc);
		cq.add();

		//?????????????????????????????????HQL
		cq = HqlGenerateUtil.getDataAuthorConditionHql(cq, new TSFunction());
		cq.add();


		List<TSFunction> functionList = systemService.getListByCriteriaQuery(cq, pageflag);
		Long total=systemService.getCountForJdbc("select count(*) from t_s_function where functionlevel=0");

		Collections.sort(functionList, new NumberComparator());

		List<TreeGrid> treeGrids = new ArrayList<TreeGrid>();
		TreeGridModel treeGridModel = new TreeGridModel();
		treeGridModel.setIcon("TSIcon_iconPath");
		treeGridModel.setTextField("functionName");
		treeGridModel.setParentText("TSFunction_functionName");
		treeGridModel.setParentId("TSFunction_id");
		treeGridModel.setSrc("functionUrl");
		treeGridModel.setIdField("id");
		treeGridModel.setChildList("TSFunctions");
		// ??????????????????
		treeGridModel.setOrder("functionOrder");

		treeGridModel.setIconStyle("functionIconStyle");


		treeGridModel.setFunctionType("functionType");

		treeGrids = systemService.treegrid(functionList, treeGridModel);

		MutiLangUtil.setMutiTree(treeGrids);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("rows",treeGrids);
		jsonObject.put("total",total);
		if (pageflag){
			return jsonObject;
		}
		return treeGrids;

	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "function")
	public ModelAndView function(ModelMap model) {
		return new ModelAndView("com/jeecg/demo/functionList");
	}

	
	/**
	 * ?????????????????????????????????
	 */
	@RequestMapping(params = "selectSort")
	public ModelAndView selectSort() {
		return new ModelAndView("com/jeecg/demo/form_selectSort");
	}
	
	/**
	 * ???????????????????????????????????????
	 * @return
	 */
	@RequestMapping(params = "gridSelectdemo")
	public ModelAndView gridSelectdemo() {
		return new ModelAndView("com/jeecg/demo/gridSelectdemo");
	}
	
	/**
	 * ???????????????????????????????????????Datagrid???
	 * @param user
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(params = "easyUIGrid", method = RequestMethod.POST)
	@ResponseBody
	public void getEasyUIGrid(TSUser user,HttpServletRequest request,HttpServletResponse response,DataGrid dataGrid)throws Exception{
		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);
        //?????????????????????
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, user);
        Short[] userstate = new Short[]{Globals.User_Normal, Globals.User_ADMIN, Globals.User_Forbidden};
        cq.in("status", userstate);
        cq.eq("deleteFlag", Globals.Delete_Normal);
        cq.add();
        this.systemService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
	}

	@RequestMapping(params = "ztreeDemo")
	public ModelAndView ztreeDemo(HttpServletRequest request) {
		return new ModelAndView("com/jeecg/demo/ztreeDemo");
	}
	
	
	@RequestMapping(params="getTreeDemoData",method ={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public AjaxJson getTreeDemoData(TSDepart depatr,HttpServletResponse response,HttpServletRequest request ){
		AjaxJson j = new AjaxJson();
		try{
			List<TSDepart> depatrList = new ArrayList<TSDepart>();
			StringBuffer hql = new StringBuffer(" from TSDepart t");
			depatrList = this.systemService.findHql(hql.toString());
			List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = null;
			for (TSDepart tsdepart : depatrList) {
				map = new HashMap<String,Object>();
				map.put("chkDisabled",false);
				map.put("click", true);
				map.put("id", tsdepart.getId());
				map.put("name", tsdepart.getDepartname());
				map.put("nocheck", false);
				map.put("struct","TREE");
				map.put("title",tsdepart.getDepartname());
				if (tsdepart.getTSPDepart() != null) {
					map.put("parentId",tsdepart.getTSPDepart().getId());
				}else {
					map.put("parentId","0");
				}
				dataList.add(map);
			}
		j.setObj(dataList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return j;
	}
	
	/**
	 * ????????????
	 * @param depart
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(TSDepart depart, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		depart = systemService.getEntity(TSDepart.class, depart.getId());
        Long childCount = systemService.getCountForJdbc("select count(1) from t_s_depart where parentdepartid ='" + depart.getId() + "'");
        if(childCount>0){
        	j.setSuccess(false);
        	j.setMsg("?????????,????????????");
        	return j;
        }
        systemService.executeSql("delete from t_s_role_org where org_id=?", depart.getId());
        //systemService.delete();
        j.setMsg("????????????");
		return j;
	}

	/**
	 * ????????????demo
	 */
	@RequestMapping(params = "tabsDemo")
	public ModelAndView tabsDemo(HttpServletRequest request) {
		logger.info("----????????????demo????????????-----");
		return new ModelAndView("com/jeecg/demo/tabsDemo");
	}
	@RequestMapping(params = "tabDemo")
	public ModelAndView tabDemo(HttpServletRequest request) {
		logger.info("----?????????demo????????????-----");
		return new ModelAndView("com/jeecg/demo/tabDemo");
	}

	/**
	 * ????????????Demo:????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "interfaceTestDemo")
	public ModelAndView interfaceTestDemo(HttpServletRequest request) {
		logger.info("----????????????demo????????????-----");
		return new ModelAndView("com/jeecg/demo/form_interfaceTestDemo");
	}
	/**
	 * ????????????Demo:????????????
	 * @param request
	 * @param response
	 * @return AjaxJson
	 */
	@RequestMapping(params = "interfaceTest")
	@ResponseBody
	public AjaxJson testInterface(HttpServletRequest request,HttpServletResponse response) {
			 AjaxJson j=new AjaxJson();
		 try {
			 String serverUrl = request.getParameter("serverUrl");//???????????????
			 String requestBody = request.getParameter("requestBody");//???????????????
			 String requestMethod = request.getParameter("requestMethod");//???????????????
				 if(requestMethod.equals("POST")){
					 if(requestBody !=""){
						 logger.info("----??????????????????-----");
						 String sendPost = HttpRequest.sendPost(serverUrl, requestBody);
						 logger.info("----??????????????????-----"+sendPost);
						 j.setSuccess(true);
						 j.setObj(sendPost);
					 }else{
						 j.setSuccess(false);
						 j.setObj("?????????????????????");
					 }
					 
				 }
				 if(requestMethod.equals("GET")){
					  logger.info("----??????????????????-----");
					  String sendGet = HttpRequest.sendGet(serverUrl, requestBody);
					  logger.info("----??????????????????-----"+sendGet);
					  j.setSuccess(true);
					  j.setObj(sendGet);
				 }
		} catch (Exception e) {
			j.setSuccess(false);
			j.setObj("?????????????????????");
			e.printStackTrace();
		}
		return j;
	}

}
