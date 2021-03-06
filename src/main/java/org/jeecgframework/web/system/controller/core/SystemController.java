package org.jeecgframework.web.system.controller.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.dao.jdbc.JdbcDao;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.ComboTree;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.common.model.json.TreeGrid;
import org.jeecgframework.core.common.model.json.ValidForm;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.enums.StoreUploadFilePathEnum;
import org.jeecgframework.core.extend.hqlsearch.parse.ObjectParseUtil;
import org.jeecgframework.core.extend.hqlsearch.parse.PageValueConvertRuleEnum;
import org.jeecgframework.core.extend.hqlsearch.parse.vo.HqlRuleEnum;
import org.jeecgframework.core.util.JSONHelper;
import org.jeecgframework.core.util.ListUtils;
import org.jeecgframework.core.util.MutiLangSqlCriteriaUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.PropertiesUtil;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.SetListSort;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.YouBianCodeUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.tag.vo.datatable.SortDirection;
import org.jeecgframework.tag.vo.easyui.ComboTreeModel;
import org.jeecgframework.tag.vo.easyui.TreeGridModel;
import org.jeecgframework.web.cgform.exception.BusinessException;
import org.jeecgframework.web.system.manager.ClientManager;
import org.jeecgframework.web.system.manager.ClientSort;
import org.jeecgframework.web.system.pojo.base.Client;
import org.jeecgframework.web.system.pojo.base.DataLogDiff;
import org.jeecgframework.web.system.pojo.base.TSDatalogEntity;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.pojo.base.TSFunction;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSRoleFunction;
import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSTypegroup;
import org.jeecgframework.web.system.service.MutiLangServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * ?????????????????????
 *
 * @author ?????????
 *
 */
//@Scope("prototype")
@Controller
@RequestMapping("/systemController")
public class SystemController extends BaseController {
	private static final Logger logger = Logger.getLogger(SystemController.class);
	private UserService userService;
	private SystemService systemService;
	private MutiLangServiceI mutiLangService;


	@Autowired
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	@Autowired
	public void setMutiLangService(MutiLangServiceI mutiLangService) {
		this.mutiLangService = mutiLangService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@RequestMapping(params = "druid")
	public ModelAndView druid() {
		return new ModelAndView(new RedirectView("druid/index.html"));
	}

	@RequestMapping(params = "typeListJson")
	@ResponseBody
	public AjaxJson typeListJson(@RequestParam(required=true)String typeGroupName) {
		AjaxJson ajaxJson = new AjaxJson();
		try {
			List<TSType> typeList = ResourceUtil.allTypes.get(typeGroupName.toLowerCase());
			JSONArray typeArray = new JSONArray();
			JSONObject headJson = new JSONObject();
			headJson.put("typecode", "");
			headJson.put("typename", "--?????????--");
			typeArray.add(headJson);
			if(typeList != null && !typeList.isEmpty()){
				for (TSType type : typeList) {
					JSONObject typeJson = new JSONObject();
					typeJson.put("typecode", type.getTypecode());

					String typename = type.getTypename();
					if(MutiLangUtil.existLangKey(typename)){
						typename = MutiLangUtil.doMutiLang(typename,"");
					}
					typeJson.put("typename",typename );

					typeArray.add(typeJson);
				}
			}
			ajaxJson.setObj(typeArray);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg(e.getMessage());
		}
		return ajaxJson;
	}

	
	/**
	 * ??????????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "typeGroupTabs")
	public ModelAndView typeGroupTabs(HttpServletRequest request) {
		List<TSTypegroup> typegroupList = systemService.loadAll(TSTypegroup.class);
		request.setAttribute("typegroupList", typegroupList);
		return new ModelAndView("system/type/typeGroupTabs");
	}

	/**
	 * ??????????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "typeGroupList")
	public ModelAndView typeGroupList(HttpServletRequest request) {
		return new ModelAndView("system/type/typeGroupList");
	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "typeList")
	public ModelAndView typeList(HttpServletRequest request) {
		String typegroupid = request.getParameter("typegroupid");
		TSTypegroup typegroup = systemService.getEntity(TSTypegroup.class, typegroupid);
		request.setAttribute("typegroup", typegroup);
		return new ModelAndView("system/type/typeList");
	}

	/**
	 * easyuiAJAX????????????
	 */

	@RequestMapping(params = "typeGroupGrid")
	public void typeGroupGrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid, TSTypegroup typegroup) {
		CriteriaQuery cq = new CriteriaQuery(TSTypegroup.class, dataGrid);

        String typegroupname = request.getParameter("typegroupname");
        if(oConvertUtils.isNotEmpty(typegroupname)) {
            typegroupname = typegroupname.trim();
            List<String> typegroupnameKeyList = systemService.findByQueryString("select typegroupname from TSTypegroup");
            if(typegroupname.lastIndexOf("*")==-1){
            	typegroupname = typegroupname + "*";
            }
            MutiLangSqlCriteriaUtil.assembleCondition(typegroupnameKeyList, cq, "typegroupname", typegroupname);
        }
        
        String typegroupcode = request.getParameter("typegroupcode");
        if(oConvertUtils.isNotEmpty(typegroupcode)) {
        	 cq.eq("typegroupcode", typegroupcode);
        	 cq.add();
        }
		this.systemService.getDataGridReturn(cq, true);
        MutiLangUtil.setMutiLangValueForList(dataGrid.getResults(), "typegroupname");


		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 *
	 * @param request
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(params = "formTree")
	@ResponseBody
	public List<ComboTree> formTree(HttpServletRequest request,final ComboTree rootCombotree) {
		String typegroupCode = request.getParameter("typegroupCode");
		TSTypegroup group = ResourceUtil.allTypeGroups.get(typegroupCode.toLowerCase());
		List<ComboTree> comboTrees = new ArrayList<ComboTree>();

		for(TSType tsType : ResourceUtil.allTypes.get(typegroupCode.toLowerCase())){
			ComboTree combotree = new ComboTree();
			combotree.setId(tsType.getTypecode());
			combotree.setText(tsType.getTypename());
			comboTrees.add(combotree);
		}
		rootCombotree.setId(group.getTypegroupcode());
		rootCombotree.setText(group.getTypegroupname());
		rootCombotree.setChecked(false);
		rootCombotree.setChildren(comboTrees);

		return new ArrayList<ComboTree>(){{add(rootCombotree);}};
	}


	/**
	 * easyuiAJAX????????????
	 *
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "typeGrid")
	public void typeGrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		String typegroupid = request.getParameter("typegroupid");
		String typename = request.getParameter("typename");
		CriteriaQuery cq = new CriteriaQuery(TSType.class, dataGrid);
		cq.eq("TSTypegroup.id", typegroupid);
		cq.like("typename", typename);

		cq.addOrder("createDate", SortDirection.desc);

		cq.add();
		this.systemService.getDataGridReturn(cq, true);

        MutiLangUtil.setMutiLangValueForList(dataGrid.getResults(), "typename");


		TagUtil.datagrid(response, dataGrid);
	}

    /**
     * ?????????????????????
     * @param request request
     * @return
     */
	@RequestMapping(params = "goTypeGrid")
	public ModelAndView goTypeGrid(HttpServletRequest request) {
		String typegroupid = request.getParameter("typegroupid");
        request.setAttribute("typegroupid", typegroupid);
		return new ModelAndView("system/type/typeListForTypegroup");
	}

//	@RequestMapping(params = "typeGroupTree")
//	@ResponseBody
//	public List<ComboTree> typeGroupTree(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//		CriteriaQuery cq = new CriteriaQuery(TSTypegroup.class);
//		List<TSTypegroup> typeGroupList = systemService.getListByCriteriaQuery(cq, false);
//		List<ComboTree> trees = new ArrayList<ComboTree>();
//		for (TSTypegroup obj : typeGroupList) {
//			ComboTree tree = new ComboTree();
//			tree.setId(obj.getId());
//			tree.setText(obj.getTypegroupname());
//			List<TSType> types = obj.getTSTypes();
//			if (types != null) {
//				if (types.size() > 0) {
//					//tree.setState("closed");
//					List<ComboTree> children = new ArrayList<ComboTree>();
//					for (TSType type : types) {
//						ComboTree tree2 = new ComboTree();
//						tree2.setId(type.getId());
//						tree2.setText(type.getTypename());
//						children.add(tree2);
//					}
//					tree.setChildren(children);
//				}
//			}
//			//tree.setChecked(false);
//			trees.add(tree);
//		}
//		return trees;
//	}

	@RequestMapping(params = "typeGridTree")
	@ResponseBody
    @Deprecated // add-begin-end--Author:zhangguoming  Date:20140928 for?????????????????????????????????????????????????????????????????????????????????
	public List<TreeGrid> typeGridTree(HttpServletRequest request, TreeGrid treegrid) {
		CriteriaQuery cq;
		List<TreeGrid> treeGrids = new ArrayList<TreeGrid>();
		if (treegrid.getId() != null) {
			cq = new CriteriaQuery(TSType.class);
			cq.eq("TSTypegroup.id", treegrid.getId().substring(1));
			cq.add();
			List<TSType> typeList = systemService.getListByCriteriaQuery(cq, false);
			for (TSType obj : typeList) {
				TreeGrid treeNode = new TreeGrid();
				treeNode.setId("T"+obj.getId());
				treeNode.setText(obj.getTypename());
				treeNode.setCode(obj.getTypecode());
				treeGrids.add(treeNode);
			}
		} else {
			cq = new CriteriaQuery(TSTypegroup.class);

            String typegroupcode = request.getParameter("typegroupcode");
            if(typegroupcode != null ) {

                HqlRuleEnum rule = PageValueConvertRuleEnum
						.convert(typegroupcode);
                Object value = PageValueConvertRuleEnum.replaceValue(rule,
                		typegroupcode);
				ObjectParseUtil.addCriteria(cq, "typegroupcode", rule, value);

                cq.add();
            }
            String typegroupname = request.getParameter("typegroupname");
            if(typegroupname != null && typegroupname.trim().length() > 0) {
                typegroupname = typegroupname.trim();
                List<String> typegroupnameKeyList = systemService.findByQueryString("select typegroupname from TSTypegroup");
                MutiLangSqlCriteriaUtil.assembleCondition(typegroupnameKeyList, cq, "typegroupname", typegroupname);
            }

            List<TSTypegroup> typeGroupList = systemService.getListByCriteriaQuery(cq, false);
			for (TSTypegroup obj : typeGroupList) {
				TreeGrid treeNode = new TreeGrid();
				treeNode.setId("G"+obj.getId());
				treeNode.setText(obj.getTypegroupname());
				treeNode.setCode(obj.getTypegroupcode());
				treeNode.setState("closed");
				treeGrids.add(treeNode);
			}
		}
		MutiLangUtil.setMutiTree(treeGrids);
		return treeGrids;
	}

//    private void assembleConditionForMutilLang(CriteriaQuery cq, String typegroupname, List<String> typegroupnameKeyList) {
//        Map<String,String> typegroupnameMap = new HashMap<String, String>();
//        for (String nameKey : typegroupnameKeyList) {
//            String name = mutiLangService.getLang(nameKey);
//            typegroupnameMap.put(nameKey, name);
//        }
//        List<String> tepegroupnameParamList = new ArrayList<String>();
//        for (Map.Entry<String, String> entry : typegroupnameMap.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            if (typegroupname.startsWith("*") && typegroupname.endsWith("*")) {
//                if (value.contains(typegroupname)) {
//                    tepegroupnameParamList.add(key);
//                }
//            } else if(typegroupname.startsWith("*")) {
//                if (value.endsWith(typegroupname.substring(1))) {
//                    tepegroupnameParamList.add(key);
//                }
//            } else if(typegroupname.endsWith("*")) {
//                if (value.startsWith(typegroupname.substring(0, typegroupname.length() -1))) {
//                    tepegroupnameParamList.add(key);
//                }
//            } else {
//                if (value.equals(typegroupname)) {
//                    tepegroupnameParamList.add(key);
//                }
//            }
//        }
//
//        if (tepegroupnameParamList.size() > 0) {
//            cq.in("typegroupname", tepegroupnameParamList.toArray());
//            cq.add();
//        }
//    }

    /**
	 * ?????????????????????????????????ID???G?????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "delTypeGridTree")
	@ResponseBody
	public AjaxJson delTypeGridTree(String id, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (id.startsWith("G")) {//??????
			TSTypegroup typegroup = systemService.getEntity(TSTypegroup.class, id.substring(1));
			message = "??????????????????: " + mutiLangService.getLang(typegroup.getTypegroupname()) + "????????? ??????";
			systemService.delete(typegroup);
		} else {
			TSType type = systemService.getEntity(TSType.class, id.substring(1));
			message = "??????????????????: " + mutiLangService.getLang(type.getTypename()) + "????????? ??????";
			systemService.delete(type);
		}
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		//????????????
		systemService.refleshTypeGroupCach();
		j.setMsg(message);
		return j;
	}

	/**
	 * ??????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "delTypeGroup")
	@ResponseBody
	public AjaxJson delTypeGroup(TSTypegroup typegroup, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		typegroup = systemService.getEntity(TSTypegroup.class, typegroup.getId());

		message = "????????????: " + mutiLangService.getLang(typegroup.getTypegroupname()) + " ????????? ??????";
        if (ListUtils.isNullOrEmpty(typegroup.getTSTypes())) {
            systemService.delete(typegroup);
            systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
            //????????????
            systemService.refleshTypeGroupCach();
        } else {
            message = "????????????: " + mutiLangService.getLang(typegroup.getTypegroupname()) + " ????????????????????????????????????";
        }

		j.setMsg(message);
		return j;
	}

	/**
	 * ????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "delType")
	@ResponseBody
	public AjaxJson delType(TSType type, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		type = systemService.getEntity(TSType.class, type.getId());
		message = "??????: " + mutiLangService.getLang(type.getTypename()) + "????????? ??????";
		systemService.delete(type);
		//????????????
		systemService.refleshTypesCach(type);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		j.setMsg(message);
		return j;
	}

	/**
	 * ??????????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "checkTypeGroup")
	@ResponseBody
	public ValidForm checkTypeGroup(HttpServletRequest request) {
		ValidForm v = new ValidForm();
		String typegroupcode=oConvertUtils.getString(request.getParameter("param"));
		String code=oConvertUtils.getString(request.getParameter("code"));
		List<TSTypegroup> typegroups=systemService.findByProperty(TSTypegroup.class,"typegroupcode",typegroupcode);
		if(typegroups.size()>0&&!code.equals(typegroupcode))
		{
			v.setInfo("???????????????");
			v.setStatus("n");
		}
		return v;
	}

	/**
	 * ????????????????????????&????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "refreshTypeGroupAndTypes")
	@ResponseBody
	public AjaxJson refreshTypeGroupAndTypes(HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		try{
			systemService.refreshTypeGroupAndTypes();
			message = mutiLangService.getLang("common.refresh.success");
		} catch (Exception e) {
			message = mutiLangService.getLang("common.refresh.fail");
		}
		j.setMsg(message);
		return j;
	}

	
	/**
	 * ??????????????????
	 *
	 * @param typegroup
	 * @return
	 */
	@RequestMapping(params = "saveTypeGroup")
	@ResponseBody
	public AjaxJson saveTypeGroup(TSTypegroup typegroup, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(typegroup.getId())) {
			message = "????????????: " + mutiLangService.getLang(typegroup.getTypegroupname()) + "???????????????";
			userService.saveOrUpdate(typegroup);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} else {
			message = "????????????: " + mutiLangService.getLang(typegroup.getTypegroupname()) + "???????????????";
			userService.save(typegroup);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		//????????????
		systemService.refleshTypeGroupCach();
		j.setMsg(message);
		return j;
	}
	/**
	 * ??????????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "checkType")
	@ResponseBody
	public ValidForm checkType(HttpServletRequest request) {
		ValidForm v = new ValidForm();
		String typecode=oConvertUtils.getString(request.getParameter("param"));
		String code=oConvertUtils.getString(request.getParameter("code"));
		String typeGroupCode=oConvertUtils.getString(request.getParameter("typeGroupCode"));
		StringBuilder hql = new StringBuilder("FROM ").append(TSType.class.getName()).append(" AS entity WHERE 1=1 ");
		hql.append(" AND entity.TSTypegroup.typegroupcode =  '").append(typeGroupCode).append("'");
		hql.append(" AND entity.typecode =  '").append(typecode).append("'");
		List<Object> types = this.systemService.findByQueryString(hql.toString());
		if(types.size()>0&&!code.equals(typecode))
		{
			v.setInfo("???????????????");
			v.setStatus("n");
		}
		return v;
	}
	/**
	 * ????????????
	 *
	 * @param type
	 * @return
	 */
	@RequestMapping(params = "saveType")
	@ResponseBody
	public AjaxJson saveType(TSType type, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(type.getId())) {
			message = "??????: " + mutiLangService.getLang(type.getTypename()) + "???????????????";
			userService.saveOrUpdate(type);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} else {
			message = "??????: " + mutiLangService.getLang(type.getTypename()) + "???????????????";
			userService.save(type);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		//????????????
		systemService.refleshTypesCach(type);
		j.setMsg(message);
		return j;
	}



	/**
	 * ??????????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "aouTypeGroup")
	public ModelAndView aouTypeGroup(TSTypegroup typegroup, HttpServletRequest req) {
		if (typegroup.getId() != null) {
			typegroup = systemService.getEntity(TSTypegroup.class, typegroup.getId());
			req.setAttribute("typegroup", typegroup);
		}
		return new ModelAndView("system/type/typegroup");
	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "addorupdateType")
	public ModelAndView addorupdateType(TSType type, HttpServletRequest req) {
		String typegroupid = req.getParameter("typegroupid");
		req.setAttribute("typegroupid", typegroupid);
        TSTypegroup typegroup = systemService.findUniqueByProperty(TSTypegroup.class, "id", typegroupid);
        String typegroupname = typegroup.getTypegroupname();
        req.setAttribute("typegroupname", mutiLangService.getLang(typegroupname));
		if (StringUtil.isNotEmpty(type.getId())) {
			type = systemService.getEntity(TSType.class, type.getId());
			req.setAttribute("type", type);
		}
		return new ModelAndView("system/type/type");
	}

	/*
	 * *****************??????????????????****************************
	 */

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "depart")
	public ModelAndView depart() {
		return new ModelAndView("system/depart/departList");
	}

	/**
	 * easyuiAJAX????????????
	 *
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagridDepart")
	public void datagridDepart(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSDepart.class, dataGrid);
		this.systemService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
		;
	}

	/**
	 * ????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "delDepart")
	@ResponseBody
	public AjaxJson delDepart(TSDepart depart, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		depart = systemService.getEntity(TSDepart.class, depart.getId());
		message = "??????: " + depart.getDepartname() + "????????? ??????";
		systemService.delete(depart);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);

		return j;
	}

	/**
	 * ????????????
	 *
	 * @param depart
	 * @return
	 */
	@RequestMapping(params = "saveDepart")
	@ResponseBody
	public AjaxJson saveDepart(TSDepart depart, HttpServletRequest request) {
		String message = null;
		// ??????????????????
		String pid = request.getParameter("TSPDepart.id");
		if (pid.equals("")) {
			depart.setTSPDepart(null);
		}
		AjaxJson j = new AjaxJson();
		if (StringUtil.isNotEmpty(depart.getId())) {
			userService.saveOrUpdate(depart);
            message = MutiLangUtil.paramUpdSuccess("common.department");
            systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);

		} else {

//			String orgCode = systemService.generateOrgCode(depart.getId(), pid);
//			depart.setOrgCode(orgCode);
			if(oConvertUtils.isNotEmpty(pid)){
				TSDepart paretDept = systemService.findUniqueByProperty(TSDepart.class, "id", pid);
				String localMaxCode  = getMaxLocalCode(paretDept.getOrgCode());
				depart.setOrgCode(YouBianCodeUtil.getSubYouBianCode(paretDept.getOrgCode(), localMaxCode));
			}else{
				String localMaxCode  = getMaxLocalCode(null);
				depart.setOrgCode(YouBianCodeUtil.getNextYouBianCode(localMaxCode));
			}

			userService.save(depart);
            message = MutiLangUtil.paramAddSuccess("common.department");
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);

        }
		j.setMsg(message);
		return j;
	}

	private synchronized String getMaxLocalCode(String parentCode){
		if(oConvertUtils.isEmpty(parentCode)){
			parentCode = "";
		}
		int localCodeLength = parentCode.length() + YouBianCodeUtil.zhanweiLength;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT org_code FROM t_s_depart");

		if(ResourceUtil.getJdbcUrl().indexOf(JdbcDao.DATABSE_TYPE_SQLSERVER)!=-1){
			sb.append(" where LEN(org_code) = ").append(localCodeLength);
		}else{
			sb.append(" where LENGTH(org_code) = ").append(localCodeLength);
		}

		if(oConvertUtils.isNotEmpty(parentCode)){
			sb.append(" and  org_code like '").append(parentCode).append("%'");
		} else {

			sb.append(" and LEFT(org_code,1)='A'");

		}

		sb.append(" ORDER BY org_code DESC");
		List<Map<String, Object>> objMapList = systemService.findForJdbc(sb.toString(), 1, 1);
		String returnCode = null;
		if(objMapList!=null && objMapList.size()>0){
			returnCode = (String)objMapList.get(0).get("org_code");
		}

		return returnCode;
	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "addorupdateDepart")
	public ModelAndView addorupdateDepart(TSDepart depart, HttpServletRequest req) {
		List<TSDepart> departList = systemService.getList(TSDepart.class);
		req.setAttribute("departList", departList);
		if (depart.getId() != null) {
			depart = systemService.getEntity(TSDepart.class, depart.getId());
			req.setAttribute("depart", depart);
		}
		return new ModelAndView("system/depart/depart");
	}

	/**
	 * ??????????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "setPFunction")
	@ResponseBody
	public List<ComboTree> setPFunction(HttpServletRequest request, ComboTree comboTree) {
		CriteriaQuery cq = new CriteriaQuery(TSDepart.class);
		if (StringUtil.isNotEmpty(comboTree.getId())) {
			cq.eq("TSPDepart.id", comboTree.getId());
		}
		// ----------------------------------------------------------------
		// ----------------------------------------------------------------
		if (StringUtil.isEmpty(comboTree.getId())) {
			cq.isNull("TSPDepart.id");
		}
		// ----------------------------------------------------------------
		// ----------------------------------------------------------------
		cq.add();
		List<TSDepart> departsList = systemService.getListByCriteriaQuery(cq, false);
		List<ComboTree> comboTrees = new ArrayList<ComboTree>();
		comboTrees = systemService.comTree(departsList, comboTree);
		return comboTrees;

	}

	/*
	 * *****************??????????????????****************************
	 */
	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "role")
	public ModelAndView role() {
		return new ModelAndView("system/role/roleList");
	}

	/**
	 * easyuiAJAX????????????
	 *
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagridRole")
	public void datagridRole(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		CriteriaQuery cq = new CriteriaQuery(TSRole.class, dataGrid);
		this.systemService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
	}

	/**
	 * ????????????
	 *
	 * @param ids
	 * @return
	 */
	@RequestMapping(params = "delRole")
	@ResponseBody
	public AjaxJson delRole(TSRole role, String ids, HttpServletRequest request) {
		String message = null;
		message = "??????: " + role.getRoleName() + "???????????????";
		AjaxJson j = new AjaxJson();
		role = systemService.getEntity(TSRole.class, role.getId());
		userService.delete(role);
		systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
		j.setMsg(message);
		return j;
	}

	/**
	 * ????????????
	 *
	 * @param role
	 * @return
	 */
	@RequestMapping(params = "saveRole")
	@ResponseBody
	public AjaxJson saveRole(TSRole role, HttpServletRequest request) {
		String message = null;
		AjaxJson j = new AjaxJson();
		if (role.getId() != null) {
			message = "??????: " + role.getRoleName() + "???????????????";
			userService.saveOrUpdate(role);
			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
		} else {
			message = "??????: " + role.getRoleName() + "???????????????";
			userService.saveOrUpdate(role);
			systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
		}
		j.setMsg(message);
		return j;
	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "fun")
	public ModelAndView fun(HttpServletRequest request) {
		Integer roleid = oConvertUtils.getInt(request.getParameter("roleid"), 0);
		request.setAttribute("roleid", roleid);
		return new ModelAndView("system/role/roleList");
	}

	/**
	 * ????????????
	 *
	 * @param role
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "setAuthority")
	@ResponseBody
	public List<ComboTree> setAuthority(TSRole role, HttpServletRequest request, ComboTree comboTree) {
		CriteriaQuery cq = new CriteriaQuery(TSFunction.class);
		if (comboTree.getId() != null) {
			cq.eq("TFunction.functionid", oConvertUtils.getInt(comboTree.getId(), 0));
		}
		if (comboTree.getId() == null) {
			cq.isNull("TFunction");
		}
		cq.add();
		List<TSFunction> functionList = systemService.getListByCriteriaQuery(cq, false);
		List<ComboTree> comboTrees = new ArrayList<ComboTree>();
		Integer roleid = oConvertUtils.getInt(request.getParameter("roleid"), 0);
		List<TSFunction> loginActionlist = new ArrayList<TSFunction>();// ??????????????????
		role = this.systemService.get(TSRole.class, roleid);
		if (role != null) {
			List<TSRoleFunction> roleFunctionList = systemService.findByProperty(TSRoleFunction.class, "TSRole.id", role.getId());
			if (roleFunctionList.size() > 0) {
				for (TSRoleFunction roleFunction : roleFunctionList) {
					TSFunction function = (TSFunction) roleFunction.getTSFunction();
					loginActionlist.add(function);
				}
			}
		}
		ComboTreeModel comboTreeModel = new ComboTreeModel("id", "functionName", "TSFunctions");
		comboTrees = systemService.ComboTree(functionList, comboTreeModel, loginActionlist, false);
		return comboTrees;
	}

	/**
	 * ????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "updateAuthority")
	public String updateAuthority(HttpServletRequest request) {
		Integer roleid = oConvertUtils.getInt(request.getParameter("roleid"), 0);
		String rolefunction = request.getParameter("rolefunctions");
		TSRole role = this.systemService.get(TSRole.class, roleid);
		List<TSRoleFunction> roleFunctionList = systemService.findByProperty(TSRoleFunction.class, "TSRole.id", role.getId());
		systemService.deleteAllEntitie(roleFunctionList);
		String[] roleFunctions = null;
		if (rolefunction != "") {
			roleFunctions = rolefunction.split(",");
			for (String s : roleFunctions) {
				TSRoleFunction rf = new TSRoleFunction();
				TSFunction f = this.systemService.get(TSFunction.class, Integer.valueOf(s));
				rf.setTSFunction(f);
				rf.setTSRole(role);
				this.systemService.save(rf);
			}
		}
		return "system/role/roleList";
	}

	/**
	 * ??????????????????
	 *
	 * @param role
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "addorupdateRole")
	public ModelAndView addorupdateRole(TSRole role, HttpServletRequest req) {
		if (role.getId() != null) {
			role = systemService.getEntity(TSRole.class, role.getId());
			req.setAttribute("role", role);
		}
		return new ModelAndView("system/role/role");
	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "operate")
	public ModelAndView operate(HttpServletRequest request) {
		String roleid = request.getParameter("roleid");
		request.setAttribute("roleid", roleid);
		return new ModelAndView("system/role/functionList");
	}

	/**
	 * ??????????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "setOperate")
	@ResponseBody
	public List<TreeGrid> setOperate(HttpServletRequest request, TreeGrid treegrid) {
		String roleid = request.getParameter("roleid");
		CriteriaQuery cq = new CriteriaQuery(TSFunction.class);
		if (treegrid.getId() != null) {
			cq.eq("TFunction.functionid", oConvertUtils.getInt(treegrid.getId(), 0));
		}
		if (treegrid.getId() == null) {
			cq.isNull("TFunction");
		}
		cq.add();
		List<TSFunction> functionList = systemService.getListByCriteriaQuery(cq, false);
		List<TreeGrid> treeGrids = new ArrayList<TreeGrid>();
		Collections.sort(functionList, new SetListSort());
		TreeGridModel treeGridModel = new TreeGridModel();
		treeGridModel.setRoleid(roleid);
		treeGrids = systemService.treegrid(functionList, treeGridModel);
		return treeGrids;

	}

	/**
	 * ????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "saveOperate")
	@ResponseBody
	public AjaxJson saveOperate(HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		String fop = request.getParameter("fp");
		String roleid = request.getParameter("roleid");
		// ?????????????????????????????????????????????
		clearp(roleid);
		String[] fun_op = fop.split(",");
		String aa = "";
		String bb = "";
		// ?????????????????????
		if (fun_op.length == 1) {
			bb = fun_op[0].split("_")[1];
			aa = fun_op[0].split("_")[0];
			savep(roleid, bb, aa);
		} else {
			// ??????2????????????
			for (int i = 0; i < fun_op.length; i++) {
				String cc = fun_op[i].split("_")[0]; // ??????id
				if (i > 0 && bb.equals(fun_op[i].split("_")[1])) {
					aa += "," + cc;
					if (i == (fun_op.length - 1)) {
						savep(roleid, bb, aa);
					}
				} else if (i > 0) {
					savep(roleid, bb, aa);
					aa = fun_op[i].split("_")[0]; // ??????ID
					if (i == (fun_op.length - 1)) {
						bb = fun_op[i].split("_")[1]; // ??????id
						savep(roleid, bb, aa);
					}

				} else {
					aa = fun_op[i].split("_")[0]; // ??????ID
				}
				bb = fun_op[i].split("_")[1]; // ??????id

			}
		}

		return j;
	}

	/**
	 * ????????????
	 *
	 * @param roleid
	 * @param functionid
	 * @param ids
	 */
	public void savep(String roleid, String functionid, String ids) {
		String hql = "from TRoleFunction t where" + " t.TSRole.id=" + roleid + " " + "and t.TFunction.functionid=" + functionid;
		TSRoleFunction rFunction = systemService.singleResult(hql);
		if (rFunction != null) {
			rFunction.setOperation(ids);
			systemService.saveOrUpdate(rFunction);
		}
	}

	/**
	 * ????????????
	 *
	 * @param roleid
	 */
	public void clearp(String roleid) {
		String hql = "from TRoleFunction t where" + " t.TSRole.id=" + roleid;
		List<TSRoleFunction> rFunctions = systemService.findByQueryString(hql);
		if (rFunctions.size() > 0) {
			for (TSRoleFunction tRoleFunction : rFunctions) {
				tRoleFunction.setOperation(null);
				systemService.saveOrUpdate(tRoleFunction);
			}
		}
	}
	
	
	/**
	 * ??????????????????
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagridOnline")
	public void datagridOnline(Client tSOnline,HttpServletRequest request,
			HttpServletResponse response, DataGrid dataGrid) {
		List<Client> onlines = new ArrayList<Client>();
		onlines.addAll(ClientManager.getInstance().getAllClient());
		dataGrid.setTotal(onlines.size());
		dataGrid.setResults(getClinetList(onlines,dataGrid));
		TagUtil.datagrid(response, dataGrid);
	}
	/**
	 * ?????????????????????????????????
	 * @param onlines
	 * @param dataGrid
	 * @return
	 */
	private List<Client> getClinetList(List<Client> onlines, DataGrid dataGrid) {
		Collections.sort(onlines, new ClientSort());
		List<Client> result = new ArrayList<Client>();
		for(int i = (dataGrid.getPage()-1)*dataGrid.getRows();
				i<onlines.size()&&i<dataGrid.getPage()*dataGrid.getRows();i++){
			result.add(onlines.get(i));
		}
		return result;
	}

	/**
     * ????????????????????????
     *
     * @param req
     * @return
     */
    @RequestMapping(params = "commonUpload")
    public ModelAndView commonUpload(HttpServletRequest req) {
            return new ModelAndView("common/upload/uploadView");
    }

    @RequestMapping(params = "commonWebUpload")
    public ModelAndView commonWebUpload(HttpServletRequest req) {
            return new ModelAndView("common/upload/uploadView2");
    }

    /************************************** ???????????? ************************************/
    /**
     * ????????? ????????????
     * @param request
     * @return
     */
    @RequestMapping(params = "dataLogList")
    public ModelAndView dataLogList(HttpServletRequest request){
    	return new ModelAndView("system/dataLog/dataLogList");
    }

    @RequestMapping(params = "datagridDataLog")
    public void dataLogDatagrid(TSDatalogEntity datalogEntity,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid){
    	CriteriaQuery cq = new CriteriaQuery(TSDatalogEntity.class, dataGrid);
		//?????????????????????
		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, datalogEntity, request.getParameterMap());
		cq.add();
		this.systemService.getDataGridReturn(cq, true);
		TagUtil.datagrid(response, dataGrid);
    }

    @RequestMapping(params = "popDataContent")
	public ModelAndView popDataContent(ModelMap modelMap, @RequestParam String id, HttpServletRequest request) {
    	TSDatalogEntity datalogEntity = this.systemService.get(TSDatalogEntity.class, id);
        modelMap.put("dataContent",datalogEntity.getDataContent());
		return new ModelAndView("system/dataLog/popDataContent");
	}

    /**
     * ????????? ????????????
     * @param request
     * @return
     */
    @RequestMapping(params = "dataDiff")
    public ModelAndView dataDiff(HttpServletRequest request){
    	return new ModelAndView("system/dataLog/dataDiff");
    }

	@RequestMapping(params = "getDataVersion")
	@ResponseBody
    public AjaxJson getDataVersion(@RequestParam String tableName, @RequestParam String dataId){
    	AjaxJson j = new AjaxJson();
    	String hql = "from TSDatalogEntity where tableName = ? and dataId = ? order by versionNumber desc";
    	List<TSDatalogEntity> datalogEntities = this.systemService.findHql(hql, new Object[]{tableName,dataId});

    	if (datalogEntities.size() > 0) {
			j.setObj(datalogEntities);
		}

    	return j;
    }

	@RequestMapping(params = "diffDataVersion")
	public ModelAndView diffDataVersion(HttpServletRequest request, @RequestParam String id1, @RequestParam String id2) throws ParseException {
		String hql1 = "from TSDatalogEntity where id = '" + id1 + "'";
		TSDatalogEntity datalogEntity1 = this.systemService.singleResult(hql1);

		String hql2 = "from TSDatalogEntity where id = '" + id2 + "'";
		TSDatalogEntity datalogEntity2 = this.systemService.singleResult(hql2);

		if (datalogEntity1 != null && datalogEntity2 != null) {
			//???????????????????????????[]??????(?????????)
			Integer version1 = datalogEntity1.getVersionNumber();
			Integer version2 = datalogEntity2.getVersionNumber();
			Map<String, Object> map1 = null;
			Map<String, Object> map2 = null;

			if (version1 < version2) {
				map1 = JSONHelper.toHashMap(datalogEntity1.getDataContent().replaceAll("^\\[|\\]$", ""));
				map2 = JSONHelper.toHashMap(datalogEntity2.getDataContent().replaceAll("^\\[|\\]$", ""));
			}else{
				map1 = JSONHelper.toHashMap(datalogEntity2.getDataContent().replaceAll("^\\[|\\]$", ""));
				map2 = JSONHelper.toHashMap(datalogEntity1.getDataContent().replaceAll("^\\[|\\]$", ""));
			}

			Map<String, Object> mapAll = new HashMap<String, Object>();
			mapAll.putAll(map1);
			mapAll.putAll(map2);
			Set<String> set = mapAll.keySet();

			List<DataLogDiff> dataLogDiffs = new LinkedList<DataLogDiff>();

			String value1 = null;
			String value2 = null;
			for (String string : set) {
				DataLogDiff dataLogDiff = new DataLogDiff();
				dataLogDiff.setName(string);

				if (map1.containsKey(string)) {
					if ("createDate".equals(string)&&StringUtil.isNotEmpty(map1.get(string))){
						java.util.Date date=new Date((String) map1.get(string));
						SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						value1=simpledateformat.format(date);
					}else {
						value1 = map1.get(string).toString();
					}

					if (value1 == null) {
						dataLogDiff.setValue1("");
					}else {
						dataLogDiff.setValue1(value1);
					}
				}else{
					dataLogDiff.setValue1("");
				}

				if (map2.containsKey(string)) {
					if ("createDate".equals(string)&&StringUtil.isNotEmpty(map2.get(string))){
						java.util.Date date=new Date((String) map2.get(string));
						SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						value2=simpledateformat.format(date);
					}else {
						value2 = map2.get(string).toString();
					}

					if (value2 == null) {
						dataLogDiff.setValue2("");
					}else {
						dataLogDiff.setValue2(value2);
					}
				}else {
					dataLogDiff.setValue2("");
				}

				
				if (value1 == null && value2 == null) {
					dataLogDiff.setDiff("N");
				}else {
					if (value1 != null && value2 != null) {
						if (value1.equals(value2)) {//??????
							dataLogDiff.setDiff("N");
						}else {
							dataLogDiff.setDiff("Y");
						}
					}else {
						dataLogDiff.setDiff("Y");
					}
				}
				dataLogDiffs.add(dataLogDiff);
			}

			if (version1 < version2) {
				request.setAttribute("versionNumber1", datalogEntity1.getVersionNumber());
				request.setAttribute("versionNumber2", datalogEntity2.getVersionNumber());
			}else {
				request.setAttribute("versionNumber1", datalogEntity2.getVersionNumber());
				request.setAttribute("versionNumber2", datalogEntity1.getVersionNumber());
			}
			request.setAttribute("dataLogDiffs", dataLogDiffs);
		}
		return new ModelAndView("system/dataLog/diffDataVersion");
	}


	/**
	 * ftpUploader
	 * ftp?????? ??????????????????/????????????
	 */
	@RequestMapping("/ftpUploader")
    @ResponseBody
    public AjaxJson ftpUploader(HttpServletRequest request, HttpServletResponse response) {
        AjaxJson j = new AjaxJson();
        String msg="????????????-??????????????????";
        String upFlag=request.getParameter("isup");
        String delFlag=request.getParameter("isdel");
        PropertiesUtil ftpConfig = new PropertiesUtil("sysConfig.properties");
        Properties prop = ftpConfig.getProperties();
        String ftpUrl=prop.getProperty("ftp.url");
        String port=prop.getProperty("ftp.port");
        String userName=prop.getProperty("ftp.userName");
        String passWord=prop.getProperty("ftp.passWord");
        try {
	        //?????????????????????
	        if("1".equals(upFlag)){
	        	String fileName = null;
	        	String bizType=request.getParameter("bizType");//??????????????????
	        	String bizPath=StoreUploadFilePathEnum.getPath(bizType);//????????????????????????????????????
	        	String nowday=new SimpleDateFormat("yyyyMMdd").format(new Date());
	        	String path=bizPath+File.separator+nowday;//ftp????????????
	            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	            MultipartFile mf=multipartRequest.getFile("file");// ????????????????????????
	    		fileName = mf.getOriginalFilename();// ???????????????
	    		if(uploadFtpFile(ftpUrl, Integer.valueOf(port), userName, passWord, path, fileName, mf.getInputStream())){
	    			msg="????????????";
	    			j.setObj(path+File.separator+fileName);
	    			//1???????????????????????????obj,??????????????????,???????????????,?????????????????????????????????
	    			//2???demo???????????????AjaxJson??????,?????????????????????????????????,?????????t????????????????????????????????????  obj??? filePath ???????????????????????????????????????????????????action????????????????????????????????????
	    		}else{
	    			msg="ftp????????????";
	    		}
				j.setMsg(msg);
	        }else if("1".equals(delFlag)){//?????????????????????
	        	String path=request.getParameter("path");
	        	path=path.replace("\\", "/");
    			if(delFtpFile(ftpUrl, Integer.valueOf(port), userName, passWord, path)){
    				msg="--------??????????????????---------"+path;
    			}else{
    				j.setSuccess(false);
    				msg="???????????????--???????????????";
    			}
	        }else{
	        	throw new BusinessException("?????????????????????????????????????????????");
	        }
        } catch (IOException e) {
			j.setSuccess(false);
			logger.info(e.getMessage());
		}catch (BusinessException b) {
			j.setSuccess(false);
			logger.info(b.getMessage());
		}
    	logger.info("-----systemController/filedeal.do------------"+msg);
		j.setMsg(msg);
        return j;
    }
	
	/**
	 * ftp????????????
	 * @param url ftp??????
	 * @param port ftp??????
	 * @param userName ?????????
	 * @param passWord ??????
	 * @param path ftp???????????????????????????
	 * @param fileName ????????????????????????
	 * @param file ?????????
	 * @return true ??????,false ??????
	 */
	private boolean uploadFtpFile(String url,int port,String userName, String passWord, String path, String fileName, InputStream file){
		boolean success=false;
		FTPClient ftp=new FTPClient();
		try {
			ftp.setControlEncoding("UTF-8");
			ftp.connect(url, port);//??????
			ftp.login(userName, passWord);//??????
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			int replyCode = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replyCode)){
				ftp.disconnect();
				return success;
			}
			//??????????????? 
			String[] dirs=path.replace(File.separator, "/").split("/");
			if(dirs!=null && dirs.length>0)//????????????????????????,??????????????????
				for (String dir : dirs) {
					ftp.makeDirectory(dir);
					ftp.changeWorkingDirectory(dir);
				}
			ftp.storeFile(new String(fileName.getBytes("UTF-8"),"iso-8859-1"), file);
			file.close();
			ftp.logout();
			success=true;
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}finally{
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}
	
	/**
	 * ftp????????????
	 * @param url ftp??????
	 * @param port ftp??????
	 * @param userName ?????????
	 * @param passWord ??????
	 * @param path ftp???????????????????????????
	 * @return true ??????,false ??????
	 */
	private boolean delFtpFile(String url,int port,String userName, String passWord,String path){
		boolean success=false;
		FTPClient ftp=new FTPClient();
		try {
			ftp.setControlEncoding("UTF-8");
			ftp.connect(url, port);//??????
			ftp.login(userName, passWord);//??????
			int replyCode = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replyCode)){
				ftp.disconnect();
				return success;
			}
			String fileName=path.substring(path.lastIndexOf("/")+1);
			ftp.changeWorkingDirectory(path.substring(0, path.lastIndexOf("/")));
			ftp.deleteFile(new String(fileName.getBytes("UTF-8"),"iso-8859-1"));
			ftp.logout();
			success=true;
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}finally{
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}
	
	/**
	 * ???????????????/????????????????????????(ftp??????)
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value="showOrDownByurlFTP",method = RequestMethod.GET)
	public void getImgByurlFTP(HttpServletResponse response,HttpServletRequest request) throws Exception{
		String flag=request.getParameter("down");//??????????????????????????????
		String dbpath = request.getParameter("dbPath");
		if("1".equals(flag)){
			response.setContentType("application/x-msdownload;charset=utf-8");
			String fileName=dbpath.substring(dbpath.lastIndexOf(File.separator)+1);
			String userAgent = request.getHeader("user-agent").toLowerCase();
			if (userAgent.contains("msie") || userAgent.contains("like gecko") ) {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}else {  
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");  
			} 
			response.setHeader("Content-disposition", "attachment; filename="+ fileName);
		}else{
			response.setContentType("image/jpeg;charset=utf-8");
		}
		
		OutputStream outputStream=null;
		try {
			outputStream = response.getOutputStream();
			downFtpFile(dbpath, outputStream);
			response.flushBuffer();
		} catch (Exception e) {
			logger.info("--????????????????????????????????????--"+e.getMessage());
		}finally{
			if(outputStream!=null){
				outputStream.close();
			}
		}
	}
	
	/**
	 * ???ftp??????????????????????????????outputStream???
	 * @param path ftp????????????
	 * @param out ???????????????
	 * @return true?????????false??????
	 */
	private boolean downFtpFile(String path,OutputStream out){
		//TODO ??????ftp?????? ?????????
		PropertiesUtil ftpConfig = new PropertiesUtil("sysConfig.properties");
        Properties prop = ftpConfig.getProperties();
        String ftpUrl=prop.getProperty("ftp.url");
        String port=prop.getProperty("ftp.port");
        String userName=prop.getProperty("ftp.userName");
        String passWord=prop.getProperty("ftp.passWord");
		boolean success=false;
		FTPClient ftp=new FTPClient();
		try {
			ftp.setControlEncoding("UTF-8");
			ftp.connect(ftpUrl, Integer.valueOf(port));//??????
			ftp.login(userName, passWord);//??????ftp
			int replyCode = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(replyCode)){
				ftp.disconnect();
				return success;
			}
			path=path.replace("\\", "/");
			String fileName=path.substring(path.lastIndexOf("/")+1);
			ftp.changeWorkingDirectory(path.substring(0, path.lastIndexOf("/")));
			ftp.retrieveFile(new String(fileName.getBytes("UTF-8"),"iso-8859-1"), out);
			ftp.logout();
			success=true;
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}finally{
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	
	/**
	 * WebUploader
	 * ??????????????????/????????????
	 */
	@RequestMapping("/filedeal")
    @ResponseBody
    public AjaxJson filedeal(HttpServletRequest request, HttpServletResponse response) {
        AjaxJson j = new AjaxJson();
        String msg="????????????-??????????????????";
        String upFlag=request.getParameter("isup");
        String delFlag=request.getParameter("isdel");
        //String ctxPath = request.getSession().getServletContext().getRealPath("")+File.separator+"webapps";
		String ctxPath=ResourceUtil.getConfigByName("webUploadpath");//demo????????????D://upFiles,???????????????????????????
		//String tempPath = request.getSession().getServletContext().getRealPath("/")+"/uploadFiles";//??????????????????
        try {
	        //?????????????????????
	        if("1".equals(upFlag)){
	        	String fileName = null;
	        	String bizType=request.getParameter("bizType");//??????????????????
	        	String bizPath=StoreUploadFilePathEnum.getPath(bizType);//????????????????????????????????????
				File file = new File(ctxPath);
				if (!file.exists()) {
					file.mkdirs();// ?????????????????????
				}
	        	/*String nowday=new SimpleDateFormat("yyyyMMdd").format(new Date());
	    		File file = new File(ctxPath+File.separator+bizPath+File.separator+nowday);
	    		*/
	            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	            MultipartFile mf=multipartRequest.getFile("file");// ????????????????????????
	    		fileName = mf.getOriginalFilename();// ???????????????
	    		String savePath = file.getPath() + File.separator + fileName;
	    		File savefile = new File(savePath);
				System.out.println("??????????????????========================================"+savefile);
				FileCopyUtils.copy(mf.getBytes(), savefile);
				msg="????????????";
				j.setMsg(msg);

				String scheme = request.getScheme();

				String requestURL =scheme+ "://" +request.getServerName()+ ":"+ request.getServerPort()+ request.getContextPath();
				j.setObj(requestURL+"/uploadFiles/"+fileName);
				System.out.println("????????????========="+requestURL+"/uploadFiles/"+fileName);
				//1???????????????????????????obj,??????????????????,???????????????,?????????????????????????????????
				//2???demo???????????????AjaxJson??????,?????????????????????????????????,?????????t????????????????????????????????????  obj??? filePath ???????????????????????????????????????????????????action????????????????????????????????????
	          //?????????????????????
	        }else if("1".equals(delFlag)){
	        	String path=request.getParameter("path");
	        	String delpath=ctxPath+File.separator+path;
	        	File fileDelete = new File(delpath);
	    		if (!fileDelete.exists() || !fileDelete.isFile()) {
	    			msg="??????: " + delpath + "?????????!";
	    			j.setSuccess(true);//??????????????????????????????
	    		}else{
	    			if(fileDelete.delete()){
	    				msg="--------??????????????????---------"+delpath;
	    			}else{
	    				j.setSuccess(false);
	    				msg="???????????????--jdk????????????????????????????????????????????????";
	    			}
	    		}
	        }else{
	        	throw new BusinessException("?????????????????????????????????????????????");
	        }
        } catch (IOException e) {
			j.setSuccess(false);
			logger.info(e.getMessage());
		}catch (BusinessException b) {
			j.setSuccess(false);
			logger.info(b.getMessage());
		}
    	logger.info("-----systemController/filedeal.do------------"+msg);
		j.setMsg(msg);
        return j;
    }
	/**
	 * ???????????????/????????????????????????
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value="showOrDownByurl",method = RequestMethod.GET)
	public void getImgByurl(HttpServletResponse response,HttpServletRequest request) throws Exception{
		String flag=request.getParameter("down");//??????????????????????????????
		String dbpath = request.getParameter("dbPath");

		if(oConvertUtils.isNotEmpty(dbpath)&&dbpath.endsWith(",")){
			dbpath = dbpath.substring(0, dbpath.length()-1);
		}

		if("1".equals(flag)){
			response.setContentType("application/x-msdownload;charset=utf-8");
			String fileName=dbpath.substring(dbpath.lastIndexOf(File.separator)+1);

			String userAgent = request.getHeader("user-agent").toLowerCase();
			if (userAgent.contains("msie") || userAgent.contains("like gecko") ) {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}else {  
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");  
			} 
			response.setHeader("Content-disposition", "attachment; filename="+ fileName);

		}else{
			response.setContentType("image/jpeg;charset=utf-8");
		}
	
		InputStream inputStream = null;
		OutputStream outputStream=null;
		try {
			String localPath=ResourceUtil.getConfigByName("webUploadpath");
			String imgurl = localPath+File.separator+dbpath;
			inputStream = new BufferedInputStream(new FileInputStream(imgurl));
			outputStream = response.getOutputStream();
			byte[] buf = new byte[1024];
	        int len;
	        while ((len = inputStream.read(buf)) > 0) {
	            outputStream.write(buf, 0, len);
	        }
	        response.flushBuffer();
		} catch (Exception e) {
			logger.info("--????????????????????????????????????--"+e.getMessage());
		}finally{
			if(inputStream!=null){
				inputStream.close();
			}
			if(outputStream!=null){
				outputStream.close();
			}
		}
	}


}
