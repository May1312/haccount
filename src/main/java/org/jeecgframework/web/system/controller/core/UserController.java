//package org.jeecgframework.web.system.controller.core;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//import org.hibernate.criterion.Property;
//import org.jeecgframework.core.common.controller.BaseController;
//import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
//import org.jeecgframework.core.common.model.common.UploadFile;
//import org.jeecgframework.core.common.model.json.AjaxJson;
//import org.jeecgframework.core.common.model.json.ComboBox;
//import org.jeecgframework.core.common.model.json.DataGrid;
//import org.jeecgframework.core.common.model.json.ValidForm;
//import org.jeecgframework.core.constant.Globals;
//import org.jeecgframework.core.enums.SysThemesEnum;
//import org.jeecgframework.core.util.ExceptionUtil;
//import org.jeecgframework.core.util.IpUtil;
//import org.jeecgframework.core.util.ListtoMenu;
//import org.jeecgframework.core.util.MyBeanUtils;
//import org.jeecgframework.core.util.PasswordUtil;
//import org.jeecgframework.core.util.ResourceUtil;
//import org.jeecgframework.core.util.RoletoJson;
//import org.jeecgframework.core.util.SetListSort;
//import org.jeecgframework.core.util.StringUtil;
//import org.jeecgframework.core.util.SysThemesUtil;
//import org.jeecgframework.core.util.oConvertUtils;
//import org.jeecgframework.poi.excel.ExcelImportUtil;
//import org.jeecgframework.poi.excel.entity.ExportParams;
//import org.jeecgframework.poi.excel.entity.ImportParams;
//import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
//import org.jeecgframework.tag.core.easyui.TagUtil;
//import org.jeecgframework.tag.vo.datatable.DataTableReturn;
//import org.jeecgframework.tag.vo.datatable.DataTables;
//import org.jeecgframework.web.system.manager.ClientManager;
//import org.jeecgframework.web.system.pojo.base.InterroleEntity;
//import org.jeecgframework.web.system.pojo.base.InterroleUserEntity;
//import org.jeecgframework.web.system.pojo.base.TSDepart;
//import org.jeecgframework.web.system.pojo.base.TSFunction;
//import org.jeecgframework.web.system.pojo.base.TSRole;
//import org.jeecgframework.web.system.pojo.base.TSRoleFunction;
//import org.jeecgframework.web.system.pojo.base.TSRoleUser;
//import org.jeecgframework.web.system.pojo.base.TSUser;
//import org.jeecgframework.web.system.pojo.base.TSUserOrg;
//import org.jeecgframework.web.system.service.SystemService;
//import org.jeecgframework.web.system.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.web.servlet.ModelAndView;
//
//
///**
// * @ClassName: UserController
// * @Description: TODO(?????????????????????)
// * @author ?????????
// */
////@Scope("prototype")
//@Controller
//@RequestMapping("/userController")
//public class UserController extends BaseController {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = Logger.getLogger(UserController.class);
//
//	private UserService userService;
//	private SystemService systemService;
//
//	@Autowired
//	public void setSystemService(SystemService systemService) {
//		this.systemService = systemService;
//	}
//
//	@Autowired
//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
//
//
//	/**
//	 * ????????????
//	 *
//	 * @param request
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	@RequestMapping(params = "menu")
//	public void menu(HttpServletRequest request, HttpServletResponse response) {
//		SetListSort sort = new SetListSort();
//		TSUser u = ResourceUtil.getSessionUser();
//		// ??????????????????
//		Set<TSFunction> loginActionlist = new HashSet<TSFunction>();// ??????????????????
//		List<TSRoleUser> rUsers = systemService.findByProperty(TSRoleUser.class, "TSUser.id", u.getId());
//		for (TSRoleUser ru : rUsers) {
//			TSRole role = ru.getTSRole();
//			List<TSRoleFunction> roleFunctionList = systemService.findByProperty(TSRoleFunction.class, "TSRole.id", role.getId());
//			if (roleFunctionList.size() > 0) {
//				for (TSRoleFunction roleFunction : roleFunctionList) {
//					TSFunction function = (TSFunction) roleFunction.getTSFunction();
//					loginActionlist.add(function);
//				}
//			}
//		}
//		List<TSFunction> bigActionlist = new ArrayList<TSFunction>();// ??????????????????
//		List<TSFunction> smailActionlist = new ArrayList<TSFunction>();// ??????????????????
//		if (loginActionlist.size() > 0) {
//			for (TSFunction function : loginActionlist) {
//				if (function.getFunctionLevel() == 0) {
//					bigActionlist.add(function);
//				} else if (function.getFunctionLevel() == 1) {
//					smailActionlist.add(function);
//				}
//			}
//		}
//		// ???????????????
//		Collections.sort(bigActionlist, sort);
//		Collections.sort(smailActionlist, sort);
//		String logString = ListtoMenu.getMenu(bigActionlist, smailActionlist);
//		// request.setAttribute("loginMenu",logString);
//		try {
//			response.getWriter().write(logString);
//			response.getWriter().flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			try {
//				response.getWriter().close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//
//	/**
//	 * ????????????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "user")
//	public String user(HttpServletRequest request) {
//		// ????????????????????????????????????????????????
//		List<TSDepart> departList = systemService.getList(TSDepart.class);
//		request.setAttribute("departsReplace", RoletoJson.listToReplaceStr(departList, "departname", "id"));
//		departList.clear();
//		return "system/user/userList";
//	}
//
//	/**
//	 * ????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "userinfo")
//	public String userinfo(HttpServletRequest request) {
//		TSUser user = ResourceUtil.getSessionUser();
//		request.setAttribute("user", user);
//		return "system/user/userinfo";
//	}
//
//	/**
//	 * ????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "changepassword")
//	public String changepassword(HttpServletRequest request) {
//		TSUser user = ResourceUtil.getSessionUser();
//		request.setAttribute("user", user);
//		return "system/user/changepassword";
//	}
//
//	@RequestMapping(params = "changeportrait")
//	public String changeportrait(HttpServletRequest request) {
//		TSUser user = ResourceUtil.getSessionUser();
//		request.setAttribute("user", user);
//		return "system/user/changeportrait";
//	}
//	/**
//	 * ????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "saveportrait")
//	@ResponseBody
//	public AjaxJson saveportrait(HttpServletRequest request,String fileName) {
//		AjaxJson j = new AjaxJson();
//		TSUser user = ResourceUtil.getSessionUser();
//		user.setPortrait(fileName);
//		j.setMsg("????????????");
//		try {
//			systemService.updateEntitie(user);
//		} catch (Exception e) {
//			j.setMsg("????????????");
//			e.printStackTrace();
//		}
//		return j;
//	}
//
//
//
//	/**
//	 * ????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "savenewpwd")
//	@ResponseBody
//	public AjaxJson savenewpwd(HttpServletRequest request) {
//		AjaxJson j = new AjaxJson();
//		TSUser user = ResourceUtil.getSessionUser();
//		logger.info("["+IpUtil.getIpAddr(request)+"][????????????] start");
//		String password = oConvertUtils.getString(request.getParameter("password"));
//		String newpassword = oConvertUtils.getString(request.getParameter("newpassword"));
//		String pString = PasswordUtil.encrypt(user.getUserName(), password, PasswordUtil.getStaticSalt());
//		if (!pString.equals(user.getPassword())) {
//			j.setMsg("??????????????????");
//			j.setSuccess(false);
//		} else {
//			try {
//				user.setPassword(PasswordUtil.encrypt(user.getUserName(), newpassword, PasswordUtil.getStaticSalt()));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			systemService.updateEntitie(user);
//			j.setMsg("????????????");
//			logger.info("["+IpUtil.getIpAddr(request)+"][????????????]???????????? userId:"+user.getUserName());
//
//		}
//		return j;
//	}
//
//
//	/**
//	 *
//	 * ??????????????????????????????
//	 * @author Chj
//	 */
//
//	@RequestMapping(params = "changepasswordforuser")
//	public ModelAndView changepasswordforuser(TSUser user, HttpServletRequest req) {
//		logger.info("["+IpUtil.getIpAddr(req)+"][??????????????????????????????]["+user.getUserName()+"]");
//		if (StringUtil.isNotEmpty(user.getId())) {
//			user = systemService.getEntity(TSUser.class, user.getId());
//			req.setAttribute("user", user);
//			idandname(req, user);
//			//System.out.println(user.getPassword()+"-----"+user.getRealName());
//		}
//		return new ModelAndView("system/user/adminchangepwd");
//	}
//
//
//
//	/**
//	 * ????????????
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(params = "savenewpwdforuser")
//	@ResponseBody
//	public AjaxJson savenewpwdforuser(HttpServletRequest req) {
//		logger.info("["+IpUtil.getIpAddr(req)+"][????????????] start");
//		String message = null;
//		AjaxJson j = new AjaxJson();
//		String id = oConvertUtils.getString(req.getParameter("id"));
//		String password = oConvertUtils.getString(req.getParameter("password"));
//
//		if (StringUtil.isNotEmpty(id)) {
//			TSUser users = systemService.getEntity(TSUser.class,id);
//			if("admin".equals(users.getUserName()) && !"admin".equals(ResourceUtil.getSessionUser().getUserName())){
//				message = "???????????????[admin]?????????admin????????????????????????????????????!";
//				logger.info("["+IpUtil.getIpAddr(req)+"]"+message);
//				j.setMsg(message);
//				return j;
//			}
//
//			//System.out.println(users.getUserName());
//			users.setPassword(PasswordUtil.encrypt(users.getUserName(), password, PasswordUtil.getStaticSalt()));
//			users.setStatus(Globals.User_Normal);
//			users.setActivitiSync(users.getActivitiSync());
//			systemService.updateEntitie(users);
//			message = "??????: " + users.getUserName() + "??????????????????";
//			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
//			logger.info("["+IpUtil.getIpAddr(req)+"][????????????]"+message);
//		}
//
//		j.setMsg(message);
//
//		return j;
//	}
//	/**
//	 * ????????????
//
//	 *
//	 * @author pu.chen
//	 */
//	@RequestMapping(params = "lock")
//	@ResponseBody
//	public AjaxJson lock(String id, HttpServletRequest req) {
//		AjaxJson j = new AjaxJson();
//		String message = null;
//		TSUser user = systemService.getEntity(TSUser.class, id);
//		if("admin".equals(user.getUserName())){
//			message = "???????????????[admin]????????????";
//			j.setMsg(message);
//			return j;
//		}
//		String lockValue=req.getParameter("lockvalue");
//
//		user.setStatus(new Short(lockValue));
//		try{
//		userService.updateEntitie(user);
//		if("0".equals(lockValue)){
//			message = "?????????" + user.getUserName() + "????????????!";
//		}else if("1".equals(lockValue)){
//			message = "?????????" + user.getUserName() + "????????????!";
//		}
//		systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
//		logger.info("["+IpUtil.getIpAddr(req)+"][????????????]"+message);
//		}catch(Exception e){
//			message = "????????????!";
//		}
//		j.setMsg(message);
//		return j;
//	}
//
//
//	/**
//	 * ??????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "role")
//	@ResponseBody
//	public List<ComboBox> role(HttpServletResponse response, HttpServletRequest request, ComboBox comboBox) {
//		String id = request.getParameter("id");
//		List<ComboBox> comboBoxs = new ArrayList<ComboBox>();
//		List<TSRole> roles = new ArrayList<TSRole>();
//		if (StringUtil.isNotEmpty(id)) {
//			List<TSRoleUser> roleUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", id);
//			if (roleUser.size() > 0) {
//				for (TSRoleUser ru : roleUser) {
//					roles.add(ru.getTSRole());
//				}
//			}
//		}
//		List<TSRole> roleList = systemService.getList(TSRole.class);
//		comboBoxs = TagUtil.getComboBox(roleList, roles, comboBox);
//
//		roleList.clear();
//		roles.clear();
//
//		return comboBoxs;
//	}
//
//	/**
//	 * ??????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "depart")
//	@ResponseBody
//	public List<ComboBox> depart(HttpServletResponse response, HttpServletRequest request, ComboBox comboBox) {
//		String id = request.getParameter("id");
//		List<ComboBox> comboBoxs = new ArrayList<ComboBox>();
//		List<TSDepart> departs = new ArrayList();
//		if (StringUtil.isNotEmpty(id)) {
//			TSUser user = systemService.get(TSUser.class, id);
////			if (user.getTSDepart() != null) {
////				TSDepart depart = systemService.get(TSDepart.class, user.getTSDepart().getId());
////				departs.add(depart);
////			}
//            // todo zhanggm ???????????????????????????????????????
//            List<TSDepart[]> resultList = systemService.findHql("from TSDepart d,TSUserOrg uo where d.id=uo.orgId and uo.id=?", id);
//            for (TSDepart[] departArr : resultList) {
//                departs.add(departArr[0]);
//            }
//        }
//		List<TSDepart> departList = systemService.getList(TSDepart.class);
//		comboBoxs = TagUtil.getComboBox(departList, departs, comboBox);
//		return comboBoxs;
//	}
//
//	/**
//	 * easyuiAJAX????????????????????????
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 */
//	@RequestMapping(params = "datagrid")
//	public void datagrid(TSUser user,HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//        CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);
//        //?????????????????????
//        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, user);
//
//        Short[] userstate = new Short[]{Globals.User_Normal, Globals.User_ADMIN, Globals.User_Forbidden};
//        cq.in("status", userstate);
//        cq.eq("deleteFlag", Globals.Delete_Normal);
//
//        cq.eq("userType", Globals.USER_TYPE_SYSTEM);
//
//        String orgIds = request.getParameter("orgIds");
//        List<String> orgIdList = extractIdListByComma(orgIds);
//        // ?????? ?????????????????????????????????
//        if (!CollectionUtils.isEmpty(orgIdList)) {
//            CriteriaQuery subCq = new CriteriaQuery(TSUserOrg.class);
//            subCq.setProjection(Property.forName("tsUser.id"));
//            subCq.in("tsDepart.id", orgIdList.toArray());
//            subCq.add();
//
//            cq.add(Property.forName("id").in(subCq.getDetachedCriteria()));
//        }
//
//
//        cq.add();
//        this.systemService.getDataGridReturn(cq, true);
//
//        List<TSUser> cfeList = new ArrayList<TSUser>();
//        for (Object o : dataGrid.getResults()) {
//            if (o instanceof TSUser) {
//                TSUser cfe = (TSUser) o;
//                if (cfe.getId() != null && !"".equals(cfe.getId())) {
//                    List<TSRoleUser> roleUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", cfe.getId());
//                    if (roleUser.size() > 0) {
//                        String roleName = "";
//                        for (TSRoleUser ru : roleUser) {
//                            roleName += ru.getTSRole().getRoleName() + ",";
//                        }
//                        roleName = roleName.substring(0, roleName.length() - 1);
//                        cfe.setUserKey(roleName);
//                    }
//                }
//                cfeList.add(cfe);
//            }
//        }
//
//        TagUtil.datagrid(response, dataGrid);
//    }
//
//	/**
//	 * ???????????????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "deleteDialog")
//	public String deleteDialog(TSUser user,HttpServletRequest request) {
//		request.setAttribute("user", user);
//		return "system/user/user-delete";
//	}
//
//	@RequestMapping(params = "delete")
//	@ResponseBody
//	public AjaxJson delete(TSUser user, @RequestParam String deleteType, HttpServletRequest req) {
//
//		if (deleteType.equals("delete")) {
//			return this.del(user, req);
//		}else if (deleteType.equals("deleteTrue")) {
//			return this.trueDel(user, req);
//		}else{
//			AjaxJson j = new AjaxJson();
//
//			j.setMsg("????????????????????????,?????????.");
//			return j;
//		}
//	}
//
//
//	/**
//	 * ???????????????????????????
//	 *
//	 * @param user
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(params = "del")
//	@ResponseBody
//	public AjaxJson del(TSUser user, HttpServletRequest req) {
//		String message = null;
//		AjaxJson j = new AjaxJson();
//		if("admin".equals(user.getUserName())){
//			message = "???????????????[admin]????????????";
//			j.setMsg(message);
//			return j;
//		}
//		user = systemService.getEntity(TSUser.class, user.getId());
////		List<TSRoleUser> roleUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
//		if (!user.getStatus().equals(Globals.User_ADMIN)) {
//
//			user.setDeleteFlag(Globals.Delete_Forbidden);
//			userService.updateEntitie(user);
//			message = "?????????" + user.getUserName() + "????????????";
//			logger.info("["+IpUtil.getIpAddr(req)+"][??????????????????]"+message);
//
//
///**
//			if (roleUser.size()>0) {
//				// ????????????????????????????????????????????????
//				delRoleUser(user);
//
//                systemService.executeSql("delete from t_s_user_org where user_id=?", user.getId()); // ?????? ??????-?????? ??????
//
//                userService.delete(user);
//				message = "?????????" + user.getUserName() + "????????????";
//				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
//			} else {
//				userService.delete(user);
//				message = "?????????" + user.getUserName() + "????????????";
//			}
//**/
//		} else {
//			message = "???????????????????????????";
//		}
//
//		j.setMsg(message);
//		return j;
//	}
//
//	/**
//	 * ????????????
//	 * @param user
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(params = "trueDel")
//	@ResponseBody
//	public AjaxJson trueDel(TSUser user, HttpServletRequest req) {
//		String message = null;
//		AjaxJson j = new AjaxJson();
//		if("admin".equals(user.getUserName())){
//			message = "???????????????[admin]????????????";
//			j.setMsg(message);
//			return j;
//		}
//		user = systemService.getEntity(TSUser.class, user.getId());
//
//		/*List<TSRoleUser> roleUser = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
//		if (!user.getStatus().equals(Globals.User_ADMIN)) {
//			if (roleUser.size()>0) {
//				// ????????????????????????????????????????????????
//				delRoleUser(user);
//                systemService.executeSql("delete from t_s_user_org where user_id=?", user.getId()); // ?????? ??????-?????? ??????
//                userService.delete(user);
//				message = "?????????" + user.getUserName() + "????????????";
//				systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);
//			} else {
//				userService.delete(user);
//				message = "?????????" + user.getUserName() + "????????????";
//			}
//		} else {
//			message = "???????????????????????????";
//		}*/
//
//		try {
//			message = userService.trueDel(user);
//			logger.info("["+IpUtil.getIpAddr(req)+"][??????????????????]"+message);
//		} catch (Exception e) {
//			e.printStackTrace();
//			message ="????????????";
//		}
//
//
//		j.setMsg(message);
//		return j;
//	}
//
//	/*public void delRoleUser(TSUser user) {
//		// ?????????????????????????????????
//		List<TSRoleUser> roleUserList = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
//		if (roleUserList.size() >= 1) {
//			for (TSRoleUser tRoleUser : roleUserList) {
//				systemService.delete(tRoleUser);
//			}
//		}
//	}*/
//	/**
//	 * ???????????????
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@RequestMapping(params = "checkUser")
//	@ResponseBody
//	public ValidForm checkUser(HttpServletRequest request) {
//		ValidForm v = new ValidForm();
//		String userName=oConvertUtils.getString(request.getParameter("param"));
//		String code=oConvertUtils.getString(request.getParameter("code"));
//		List<TSUser> roles=systemService.findByProperty(TSUser.class,"userName",userName);
//		if(roles.size()>0&&!code.equals(userName))
//		{
//			v.setInfo("??????????????????");
//			v.setStatus("n");
//		}
//		return v;
//	}
//
//	/**
//	 * ??????????????????
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(params="checkUserEmail")
//	@ResponseBody
//	public ValidForm checkUserEmail(HttpServletRequest request){
//		ValidForm validForm = new ValidForm();
//		String email=oConvertUtils.getString(request.getParameter("param"));
//		String code=oConvertUtils.getString(request.getParameter("code"));
//		List<TSUser> userList=systemService.findByProperty(TSUser.class,"email",email);
//		if(userList.size()>0&&!code.equals(email))
//		{
//			validForm.setInfo("?????????????????????????????????");
//			validForm.setStatus("n");
//		}
//		return validForm;
//	}
//
//
//	/**
//	 * ????????????
//	 *
//	 * @param user
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(params = "saveUser")
//	@ResponseBody
//	public AjaxJson saveUser(HttpServletRequest req, TSUser user) {
//		String message = null;
//		AjaxJson j = new AjaxJson();
//
//		Short logType=Globals.Log_Type_UPDATE;
//		// ?????????????????????
//		String roleid = oConvertUtils.getString(req.getParameter("roleid"));
//		String orgid=oConvertUtils.getString(req.getParameter("orgIds"));
//		if (StringUtil.isNotEmpty(user.getId())) {
//			TSUser users = systemService.getEntity(TSUser.class, user.getId());
//			users.setEmail(user.getEmail());
//			users.setOfficePhone(user.getOfficePhone());
//			users.setMobilePhone(user.getMobilePhone());
//			users.setDevFlag(user.getDevFlag());
//			users.setRealName(user.getRealName());
//			users.setStatus(Globals.User_Normal);
//			users.setActivitiSync(user.getActivitiSync());
//			this.userService.saveOrUpdate(users, orgid.split(","), roleid.split(","));
//			message = "??????: " + users.getUserName() + "????????????";
//		} else {
//			TSUser users = systemService.findUniqueByProperty(TSUser.class, "userName",user.getUserName());
//			if (users != null) {
//				message = "??????: " + users.getUserName() + "????????????";
//			} else {
//				user.setPassword(PasswordUtil.encrypt(user.getUserName(), oConvertUtils.getString(req.getParameter("password")), PasswordUtil.getStaticSalt()));
//				user.setStatus(Globals.User_Normal);
//				user.setDeleteFlag(Globals.Delete_Normal);
//				//???????????????????????????
//				user.setUserType(Globals.USER_TYPE_SYSTEM);
//				this.userService.saveOrUpdate(user, orgid.split(","), roleid.split(","));
//				message = "??????: " + user.getUserName() + "????????????";
//				logType=Globals.Log_Type_INSERT;
//			}
//		}
//		systemService.addLog(message, logType, Globals.Log_Leavel_INFO);
//		j.setMsg(message);
//		logger.info("["+IpUtil.getIpAddr(req)+"][??????????????????]"+message);
//		return j;
//
//	}
//
//    /**
//     * ?????? ??????-???????????? ????????????
//     * @param request request
//     * @param user user
//     */
//    private void saveUserOrgList(HttpServletRequest request, TSUser user) {
//        String orgIds = oConvertUtils.getString(request.getParameter("orgIds"));
//
//        List<TSUserOrg> userOrgList = new ArrayList<TSUserOrg>();
//        List<String> orgIdList = extractIdListByComma(orgIds);
//        for (String orgId : orgIdList) {
//            TSDepart depart = new TSDepart();
//            depart.setId(orgId);
//
//            TSUserOrg userOrg = new TSUserOrg();
//            userOrg.setTsUser(user);
//            userOrg.setTsDepart(depart);
//
//            userOrgList.add(userOrg);
//        }
//        if (!userOrgList.isEmpty()) {
//            systemService.batchSave(userOrgList);
//        }
//    }
//
//
//    protected void saveRoleUser(TSUser user, String roleidstr) {
//		String[] roleids = roleidstr.split(",");
//		for (int i = 0; i < roleids.length; i++) {
//			TSRoleUser rUser = new TSRoleUser();
//			TSRole role = systemService.getEntity(TSRole.class, roleids[i]);
//			rUser.setTSRole(role);
//			rUser.setTSUser(user);
//			systemService.save(rUser);
//
//		}
//	}
//
//	/**
//	 * ??????????????????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "roles")
//	public ModelAndView roles(HttpServletRequest request) {
//		//--author???zhoujf-----start----date:20150531--------for: ???????????????????????????,?????????????????????????????????????????????
//		ModelAndView mv = new ModelAndView("system/user/users");
//		String ids = oConvertUtils.getString(request.getParameter("ids"));
//		mv.addObject("ids", ids);
//		return mv;
//	}
//
//	/**
//	 * ??????????????????
//	 *
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 */
//	@RequestMapping(params = "datagridRole")
//	public void datagridRole(TSRole tsRole, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//		CriteriaQuery cq = new CriteriaQuery(TSRole.class, dataGrid);
//		//?????????????????????
//		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, tsRole);
//		this.systemService.getDataGridReturn(cq, true);
//		TagUtil.datagrid(response, dataGrid);
//	}
//
//	/**
//	 * easyuiAJAX??????????????? ????????????????????????
//	 *
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 * @param user
//	 */
//	@RequestMapping(params = "addorupdate")
//	public ModelAndView addorupdate(TSUser user, HttpServletRequest req) {
//
//		/*		List<TSDepart> departList = new ArrayList<TSDepart>();
//		String departid = oConvertUtils.getString(req.getParameter("departid"));
//		if(!StringUtil.isEmpty(departid)){
//			departList.add((TSDepart)systemService.getEntity(TSDepart.class,departid));
//		}else {
//			departList.addAll((List)systemService.getList(TSDepart.class));
//		}
//		req.setAttribute("departList", departList);*/
//
//        List<String> orgIdList = new ArrayList<String>();
//        TSDepart tsDepart = new TSDepart();
//		if (StringUtil.isNotEmpty(user.getId())) {
//			user = systemService.getEntity(TSUser.class, user.getId());
//
//			req.setAttribute("user", user);
//			idandname(req, user);
//			getOrgInfos(req, user);
//		}
//		req.setAttribute("tsDepart", tsDepart);
//        //req.setAttribute("orgIdList", JSON.toJSON(orgIdList));
//
//
//        return new ModelAndView("system/user/user");
//	}
//
//	/**
//	 * ???????????????????????????
//	 *
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 * @param user
//	 */
//	@RequestMapping(params = "addorupdateInterfaceUser")
//	public ModelAndView addorupdateInterfaceUser(TSUser user, HttpServletRequest req) {
//        TSDepart tsDepart = new TSDepart();
//		if (StringUtil.isNotEmpty(user.getId())) {
//			user = systemService.getEntity(TSUser.class, user.getId());
//			req.setAttribute("user", user);
//			interfaceroleidandname(req, user);
//		}else{
//			String roleId = req.getParameter("roleId");
//	        InterroleEntity role = systemService.getEntity(InterroleEntity.class, roleId);
//	        req.setAttribute("roleId", roleId);
//			req.setAttribute("roleName", role.getRoleName());
//		}
//		req.setAttribute("tsDepart", tsDepart);
//        return new ModelAndView("system/user/interfaceUser");
//	}
//
//	public void interfaceroleidandname(HttpServletRequest req, TSUser user) {
//		List<InterroleUserEntity> roleUsers = systemService.findByProperty(InterroleUserEntity.class, "TSUser.id", user.getId());
//		String roleId = "";
//		String roleName = "";
//		if (roleUsers.size() > 0) {
//			for (InterroleUserEntity interroleUserEntity : roleUsers) {
//				roleId += interroleUserEntity.getInterroleEntity().getId() + ",";
//				roleName += interroleUserEntity.getInterroleEntity().getRoleName() + ",";
//			}
//		}
//		req.setAttribute("roleId", roleId);
//		req.setAttribute("roleName", roleName);
//
//	}
//
//	/**
//	 * ??????????????????
//	 *
//	 * @param user
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(params = "saveInterfaceUser")
//	@ResponseBody
//	public AjaxJson saveInterfaceUser(HttpServletRequest req, TSUser user) {
//		String message = null;
//		AjaxJson j = new AjaxJson();
//		// ?????????????????????
//		String roleid = oConvertUtils.getString(req.getParameter("roleid"));
//		String password = oConvertUtils.getString(req.getParameter("password"));
//		if (StringUtil.isNotEmpty(user.getId())) {
//			TSUser users = systemService.getEntity(TSUser.class, user.getId());
//			users.setEmail(user.getEmail());
//			users.setOfficePhone(user.getOfficePhone());
//			users.setMobilePhone(user.getMobilePhone());
//			users.setDevFlag(user.getDevFlag());
//
////            systemService.executeSql("delete from t_s_user_org where user_id=?", user.getId());
////            saveUserOrgList(req, user);
////            users.setTSDepart(user.getTSDepart());
//
//			users.setRealName(user.getRealName());
//			users.setStatus(Globals.User_Normal);
//			users.setActivitiSync(user.getActivitiSync());
//
//			users.setUserNameEn(user.getUserNameEn());
//			users.setUserType(user.getUserType());
////			users.setPersonType(user.getPersonType());
//			users.setSex(user.getSex());
//			users.setEmpNo(user.getEmpNo());
//			users.setCitizenNo(user.getCitizenNo());
//			users.setFax(user.getFax());
//			users.setAddress(user.getAddress());
//			users.setPost(user.getPost());
//			users.setMemo(user.getMemo());
//
//			systemService.updateEntitie(users);
//			List<TSRoleUser> ru = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
//			systemService.deleteAllEntitie(ru);//TODO ?
//			message = "??????: " + users.getUserName() + "????????????";
////			if (StringUtil.isNotEmpty(roleid)) {
////				saveInterfaceRoleUser(users, roleid);
////			}
//			systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
//		} else {
//			TSUser users = systemService.findUniqueByProperty(TSUser.class, "userName",user.getUserName());
//			if (users != null) {
//				message = "??????: " + users.getUserName() + "????????????";
//			} else {
//				user.setPassword(PasswordUtil.encrypt(user.getUserName(), password, PasswordUtil.getStaticSalt()));
////				if (user.getTSDepart().equals("")) {
////					user.setTSDepart(null);
////				}
//				user.setStatus(Globals.User_Normal);
//				user.setDeleteFlag(Globals.Delete_Normal);
//				systemService.save(user);
//                // todo zhanggm ????????????????????????
////                saveUserOrgList(req, user);
//				message = "??????: " + user.getUserName() + "????????????";
//				if (StringUtil.isNotEmpty(roleid)) {
//					saveInterfaceRoleUser(user, roleid);
//				}
//				systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
//			}
//
//		}
//		j.setMsg(message);
//		logger.info("["+IpUtil.getIpAddr(req)+"][??????????????????]"+message);
//		return j;
//	}
//
//	protected void saveInterfaceRoleUser(TSUser user, String roleidstr) {
//		String[] roleids = roleidstr.split(",");
//		for (int i = 0; i < roleids.length; i++) {
//			InterroleUserEntity rUser = new InterroleUserEntity();
//			InterroleEntity role = systemService.getEntity(InterroleEntity.class, roleids[i]);
//			rUser.setInterroleEntity(role);
//			rUser.setTSUser(user);
//			systemService.save(rUser);
//		}
//	}
//
//
//    /**
//     * ?????????????????????????????????????????????
//     * @param request request
//     * @return ??????????????????????????????
//     */
//	@RequestMapping(params = "userOrgSelect")
//	public ModelAndView userOrgSelect(HttpServletRequest request) {
//		List<TSDepart> orgList = new ArrayList<TSDepart>();
//		String userId = oConvertUtils.getString(request.getParameter("userId"));
//
//        List<Object[]> orgArrList = systemService.findHql("from TSDepart d,TSUserOrg uo where d.id=uo.tsDepart.id and uo.tsUser.id=?", new String[]{userId});
//        for (Object[] departs : orgArrList) {
//            orgList.add((TSDepart) departs[0]);
//        }
//        request.setAttribute("orgList", orgList);
//
//        TSUser user = systemService.getEntity(TSUser.class, userId);
//        request.setAttribute("user", user);
//
//		return new ModelAndView("system/user/userOrgSelect");
//    }
//
//
//	public void idandname(HttpServletRequest req, TSUser user) {
//		List<TSRoleUser> roleUsers = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
//		String roleId = "";
//		String roleName = "";
//		if (roleUsers.size() > 0) {
//			for (TSRoleUser tRoleUser : roleUsers) {
//				roleId += tRoleUser.getTSRole().getId() + ",";
//				roleName += tRoleUser.getTSRole().getRoleName() + ",";
//			}
//		}
//		req.setAttribute("id", roleId);
//		req.setAttribute("roleName", roleName);
//
//	}
//
//	public void getOrgInfos(HttpServletRequest req, TSUser user) {
//		List<TSUserOrg> tSUserOrgs = systemService.findByProperty(TSUserOrg.class, "tsUser.id", user.getId());
//		String orgIds = "";
//		String departname = "";
//		if (tSUserOrgs.size() > 0) {
//			for (TSUserOrg tSUserOrg : tSUserOrgs) {
//				orgIds += tSUserOrg.getTsDepart().getId() + ",";
//				departname += tSUserOrg.getTsDepart().getDepartname() + ",";
//			}
//		}
//		req.setAttribute("orgIds", orgIds);
//		req.setAttribute("departname", departname);
//
//	}
//
//	/**
//	 * ?????????????????????????????????????????????
//	 */
//	@RequestMapping(params = "choose")
//	public String choose(HttpServletRequest request) {
//		List<TSRole> roles = systemService.loadAll(TSRole.class);
//		request.setAttribute("roleList", roles);
//		return "system/membership/checkuser";
//	}
//
//	/**
//	 * ??????????????????????????????panel????????????
//	 *
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(params = "chooseUser")
//	public String chooseUser(HttpServletRequest request) {
//		String departid = request.getParameter("departid");
//		String roleid = request.getParameter("roleid");
//		request.setAttribute("roleid", roleid);
//		request.setAttribute("departid", departid);
//		return "system/membership/userlist";
//	}
//
//	/**
//	 * ????????????????????????????????????????????????
//	 *
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 */
//	@RequestMapping(params = "datagridUser")
//	public void datagridUser(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//		String departid = request.getParameter("departid");
//		String roleid = request.getParameter("roleid");
//		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);
//		if (departid.length() > 0) {
//			cq.eq("TDepart.departid", oConvertUtils.getInt(departid, 0));
//			cq.add();
//		}
//		String userid = "";
//		if (roleid.length() > 0) {
//			List<TSRoleUser> roleUsers = systemService.findByProperty(TSRoleUser.class, "TRole.roleid", oConvertUtils.getInt(roleid, 0));
//			if (roleUsers.size() > 0) {
//				for (TSRoleUser tRoleUser : roleUsers) {
//					userid += tRoleUser.getTSUser().getId() + ",";
//				}
//			}
//			cq.in("userid", oConvertUtils.getInts(userid.split(",")));
//			cq.add();
//		}
//		this.systemService.getDataGridReturn(cq, true);
//		TagUtil.datagrid(response, dataGrid);
//	}
//
//	/**
//	 * ?????????????????????????????????????????????
//	 */
//	@RequestMapping(params = "roleDepart")
//	public String roleDepart(HttpServletRequest request) {
//		List<TSRole> roles = systemService.loadAll(TSRole.class);
//		request.setAttribute("roleList", roles);
//		return "system/membership/roledepart";
//	}
//
//	/**
//	 * ??????????????????????????????panel????????????
//	 *
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(params = "chooseDepart")
//	public ModelAndView chooseDepart(HttpServletRequest request) {
//		String nodeid = request.getParameter("nodeid");
//		ModelAndView modelAndView = null;
//		if (nodeid.equals("role")) {
//			modelAndView = new ModelAndView("system/membership/users");
//		} else {
//			modelAndView = new ModelAndView("system/membership/departList");
//		}
//		return modelAndView;
//	}
//
//	/**
//	 * ????????????????????????????????????????????????
//	 *
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 */
//	@RequestMapping(params = "datagridDepart")
//	public void datagridDepart(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
//		CriteriaQuery cq = new CriteriaQuery(TSDepart.class, dataGrid);
//		systemService.getDataGridReturn(cq, true);
//		TagUtil.datagrid(response, dataGrid);
//	}
//
//	/**
//	 * ??????
//	 *
//	 * @param user
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(params = "test")
//	public void test(HttpServletRequest request, HttpServletResponse response) {
//		String jString = request.getParameter("_dt_json");
//		DataTables dataTables = new DataTables(request);
//		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataTables);
//		String username = request.getParameter("userName");
//		if (username != null) {
//			cq.like("userName", username);
//			cq.add();
//		}
//		DataTableReturn dataTableReturn = systemService.getDataTableReturn(cq, true);
//		TagUtil.datatable(response, dataTableReturn, "id,userName,mobilePhone,TSDepart_departname");
//	}
//
//	/**
//	 * ????????????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "index")
//	public String index() {
//		return "bootstrap/main";
//	}
//
//	/**
//	 * ????????????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "main")
//	public String main() {
//		return "bootstrap/test";
//	}
//
//	/**
//	 * ??????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "testpage")
//	public String testpage(HttpServletRequest request) {
//		return "test/test";
//	}
//
//	/**
//	 * ????????????????????????
//	 *
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(params = "addsign")
//	public ModelAndView addsign(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		request.setAttribute("id", id);
//		return new ModelAndView("system/user/usersign");
//	}
//
//	/**
//	 * ????????????
//	 *
//	 * @param user
//	 * @param req
//	 * @return
//	 */
//
//	@RequestMapping(params = "savesign", method = RequestMethod.POST)
//	@ResponseBody
//	public AjaxJson savesign(HttpServletRequest req) {
//		String message = null;
//		UploadFile uploadFile = new UploadFile(req);
//		String id = uploadFile.get("id");
//		TSUser user = systemService.getEntity(TSUser.class, id);
//		uploadFile.setRealPath("signatureFile");
//		uploadFile.setCusPath("signature");
//		uploadFile.setByteField("signature");
//		uploadFile.setBasePath("resources");
//		uploadFile.setRename(false);
//		uploadFile.setObject(user);
//		AjaxJson j = new AjaxJson();
//		message = user.getUserName() + "??????????????????";
//		systemService.uploadFile(uploadFile);
//		systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
//		j.setMsg(message);
//
//		return j;
//	}
//	/**
//	 * ????????????????????????
//	 * @param user
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 */
//	@RequestMapping(params = "testSearch")
//	public void testSearch(TSUser user, HttpServletRequest request,HttpServletResponse response,DataGrid dataGrid) {
//		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);
//		if(user.getUserName()!=null){
//			cq.like("userName", user.getUserName());
//		}
//		if(user.getRealName()!=null){
//			cq.like("realName", user.getRealName());
//		}
//		cq.add();
//		this.systemService.getDataGridReturn(cq, true);
//		TagUtil.datagrid(response, dataGrid);
//	}
//	@RequestMapping(params = "changestyle")
//	public String changeStyle(HttpServletRequest request) {
//		TSUser user = ResourceUtil.getSessionUser();
//		if(user==null){
//			return "login/login";
//		}
////		String indexStyle = "shortcut";
////		String cssTheme="";
////		Cookie[] cookies = request.getCookies();
////		for (Cookie cookie : cookies) {
////			if(cookie==null || StringUtils.isEmpty(cookie.getName())){
////				continue;
////			}
////			if(cookie.getName().equalsIgnoreCase("JEECGINDEXSTYLE")){
////				indexStyle = cookie.getValue();
////			}
////			if(cookie.getName().equalsIgnoreCase("JEECGCSSTHEME")){
////				cssTheme = cookie.getValue();
////			}
////		}
//		SysThemesEnum sysThemesEnum = SysThemesUtil.getSysTheme(request);
//		request.setAttribute("indexStyle", sysThemesEnum.getStyle());
////		request.setAttribute("cssTheme", cssTheme);
//		return "system/user/changestyle";
//	}
//	/**
//	* @Title: saveStyle
//	* @Description: ??????????????????
//	* @param request
//	* @return AjaxJson
//	* @throws
//	 */
//	@RequestMapping(params = "savestyle")
//	@ResponseBody
//	public AjaxJson saveStyle(HttpServletRequest request,HttpServletResponse response) {
//		AjaxJson j = new AjaxJson();
//		j.setSuccess(Boolean.FALSE);
//		TSUser user = ResourceUtil.getSessionUser();
//		if(user!=null){
//			String indexStyle = request.getParameter("indexStyle");
////			String cssTheme = request.getParameter("cssTheme");
//
////			if(StringUtils.isNotEmpty(cssTheme)){
////				Cookie cookie4css = new Cookie("JEECGCSSTHEME", cssTheme);
////				cookie4css.setMaxAge(3600*24*30);
////				response.addCookie(cookie4css);
////				logger.info("cssTheme:"+cssTheme);
////			}else if("ace".equals(indexStyle)){
////				Cookie cookie4css = new Cookie("JEECGCSSTHEME", "metro");
////				cookie4css.setMaxAge(3600*24*30);
////				response.addCookie(cookie4css);
////				logger.info("cssTheme:metro");
//
////			}else {
////				Cookie cookie4css = new Cookie("JEECGCSSTHEME", "");
////				cookie4css.setMaxAge(3600*24*30);
////				response.addCookie(cookie4css);
////				logger.info("cssTheme:default");
////			}
//
//
//			if(StringUtils.isNotEmpty(indexStyle)){
//				Cookie cookie = new Cookie("JEECGINDEXSTYLE", indexStyle);
//				//??????cookie?????????????????????
//				cookie.setMaxAge(3600*24*30);
//				response.addCookie(cookie);
//				logger.debug(" ----- ????????????: indexStyle ----- "+indexStyle);
//				j.setSuccess(Boolean.TRUE);
//				j.setMsg("????????????????????????????????????");
//			}
//
//			try {
//				 ClientManager.getInstance().getClient().getFunctions().clear();
//			} catch (Exception e) {
//			}
//
//		}else{
//			j.setMsg("?????????????????????");
//		}
//		return j;
//	}
//
//	/**
//	 * ??????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "upload")
//	public ModelAndView upload(HttpServletRequest req) {
//		req.setAttribute("controller_name","userController");
//		return new ModelAndView("common/upload/pub_excel_upload");
//	}
//
//	/**
//	 * ??????excel
//	 *
//	 * @param request
//	 * @param response
//	 */
//	@RequestMapping(params = "exportXls")
//	public String exportXls(TSUser tsUser,HttpServletRequest request,HttpServletResponse response
//			, DataGrid dataGrid,ModelMap modelMap) {
//		CriteriaQuery cq = new CriteriaQuery(TSUser.class, dataGrid);
//		org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, tsUser, request.getParameterMap());
//		List<TSUser> tsUsers = this.userService.getListByCriteriaQuery(cq,false);
//		//????????????????????????????????????????????????????????????
//		for(int i=0;i<tsUsers.size();i++){
//			TSUser user = tsUsers.get(i);
//			//??????
//			systemService.getSession().evict(user);
//			String id = user.getId();
//
//			String queryRole = "select * from t_s_role where id in (select roleid from t_s_role_user where userid=:userid)";
//			List<TSRole> roles = systemService.getSession().createSQLQuery(queryRole).addEntity(TSRole.class).setString("userid",id).list();
//			String roleCodes = "";
//			for(TSRole role:roles){
//				roleCodes += ","+role.getRoleCode();
//			}
//			user.setUserKey(roleCodes.replaceFirst(",", ""));
//			String queryDept = "select * from t_s_depart where id in (select org_id from t_s_user_org where user_id=:userid)";
//			List<TSDepart> departs = systemService.getSession().createSQLQuery(queryDept).addEntity(TSDepart.class).setString("userid",id).list();
//			String departCodes = "";
//			for(TSDepart depart:departs){
//				departCodes += ","+depart.getOrgCode();
//			}
//			user.setDepartid(departCodes.replaceFirst(",", ""));
//
//		}
//		modelMap.put(NormalExcelConstants.FILE_NAME,"?????????");
//		modelMap.put(NormalExcelConstants.CLASS,TSUser.class);
//		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("???????????????", "?????????:"+ResourceUtil.getSessionUser().getRealName(),
//				"????????????"));
//		modelMap.put(NormalExcelConstants.DATA_LIST,tsUsers);
//		return NormalExcelConstants.JEECG_EXCEL_VIEW;
//	}
//
//	/**
//	 * ??????excel ?????????
//	 *
//	 * @param request
//	 * @param response
//	 */
//	@RequestMapping(params = "exportXlsByT")
//	public String exportXlsByT(TSUser tsUser,HttpServletRequest request,HttpServletResponse response
//			, DataGrid dataGrid,ModelMap modelMap) {
//		modelMap.put(NormalExcelConstants.FILE_NAME,"?????????");
//		modelMap.put(NormalExcelConstants.CLASS,TSUser.class);
//		modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("???????????????", "?????????:"+ResourceUtil.getSessionUser().getRealName(),
//				"????????????"));
//		modelMap.put(NormalExcelConstants.DATA_LIST,new ArrayList());
//		return NormalExcelConstants.JEECG_EXCEL_VIEW;
//	}
//
//	@SuppressWarnings("unchecked")
//	@RequestMapping(params = "importExcel", method = RequestMethod.POST)
//	@ResponseBody
//	public AjaxJson importExcel(HttpServletRequest request, HttpServletResponse response) {
//		AjaxJson j = new AjaxJson();
//
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
//		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
//			MultipartFile file = entity.getValue();// ????????????????????????
//			ImportParams params = new ImportParams();
//			params.setTitleRows(2);
//			params.setHeadRows(1);
//			params.setNeedSave(true);
//			try {
//				List<TSUser> tsUsers = ExcelImportUtil.importExcel(file.getInputStream(),TSUser.class,params);
//				for (TSUser tsUser : tsUsers) {
//
//					String username = tsUser.getUserName();
//					if(username==null||username.equals("")){
//						j.setMsg("???????????????????????????????????????");
//						return j;
//					}
//
//					tsUser.setStatus(new Short("1"));
//					tsUser.setDevFlag("0");
//					tsUser.setDeleteFlag(new Short("0"));
//					String roleCodes = tsUser.getUserKey();
//					String deptCodes = tsUser.getDepartid();
//
//					tsUser.setPassword(PasswordUtil.encrypt(username, "123456", PasswordUtil.getStaticSalt()));
//					tsUser.setUserType(Globals.USER_TYPE_SYSTEM);//???????????? ??????????????????????????????
//
//					if((roleCodes==null||roleCodes.equals(""))||(deptCodes==null||deptCodes.equals(""))){
//						List<TSUser> users = systemService.findByProperty(TSUser.class,"userName",username);
//						if(users.size()!=0){
//							//??????????????????
//							TSUser user = users.get(0);
//							MyBeanUtils.copyBeanNotNull2Bean(tsUser,user);
//							user.setDepartid(null);
//							systemService.saveOrUpdate(user);
//						}else{
//							tsUser.setDepartid(null);
//							systemService.save(tsUser);
//						}
//					}else{
//						String[] roles = roleCodes.split(",");
//						String[] depts = deptCodes.split(",");
//						boolean flag = true;
//						//???????????????????????????????????????????????????????????????????????????????????????
//						for(String roleCode:roles){
//							List<TSRole> roleList = systemService.findByProperty(TSRole.class,"roleCode",roleCode);
//							if(roleList.size()==0){
//								flag = false;
//							}
//						}
//
//						for(String deptCode:depts){
//							List<TSDepart> departList = systemService.findByProperty(TSDepart.class,"orgCode",deptCode);
//							if(departList.size()==0){
//								flag = false;
//							}
//						}
//
//						if(flag){
//							//????????????????????????
//							List<TSUser> users = systemService.findByProperty(TSUser.class,"userName",username);
//							if(users.size()!=0){
//								//??????????????????
//								TSUser user = users.get(0);
//								MyBeanUtils.copyBeanNotNull2Bean(tsUser,user);
//								user.setDepartid(null);
//								systemService.saveOrUpdate(user);
//
//								String id = user.getId();
//								systemService.executeSql("delete from t_s_role_user where userid='"+id+"'");
//								for(String roleCode:roles){
//									//????????????????????????roleid
//									List<TSRole> roleList = systemService.findByProperty(TSRole.class,"roleCode",roleCode);
//									TSRoleUser tsRoleUser = new TSRoleUser();
//									tsRoleUser.setTSUser(user);
//									tsRoleUser.setTSRole(roleList.get(0));
//									systemService.save(tsRoleUser);
//								}
//
//								systemService.executeSql("delete from t_s_user_org where user_id='"+id+"'");
//								for(String orgCode:depts){
//									//????????????????????????roleid
//									List<TSDepart> departList = systemService.findByProperty(TSDepart.class,"orgCode",orgCode);
//									TSUserOrg tsUserOrg = new TSUserOrg();
//									tsUserOrg.setTsDepart(departList.get(0));
//									tsUserOrg.setTsUser(user);
//									systemService.save(tsUserOrg);
//								}
//							}else{
//								//??????????????????
//								//TSUser user = users.get(0);
//								tsUser.setDepartid(null);
//								systemService.save(tsUser);
//								for(String roleCode:roles){
//									//????????????????????????roleid
//									List<TSRole> roleList = systemService.findByProperty(TSRole.class,"roleCode",roleCode);
//									TSRoleUser tsRoleUser = new TSRoleUser();
//									tsRoleUser.setTSUser(tsUser);
//									tsRoleUser.setTSRole(roleList.get(0));
//									systemService.save(tsRoleUser);
//								}
//
//								for(String orgCode:depts){
//									//????????????????????????roleid
//									List<TSDepart> departList = systemService.findByProperty(TSDepart.class,"orgCode",orgCode);
//									TSUserOrg tsUserOrg = new TSUserOrg();
//									tsUserOrg.setTsDepart(departList.get(0));
//									tsUserOrg.setTsUser(tsUser);
//									systemService.save(tsUserOrg);
//								}
//							}
//							j.setMsg("?????????????????????");
//						}else {
//							j.setMsg("?????????????????????????????????????????????");
//						}
//					}
//				}
//			} catch (Exception e) {
//				j.setMsg("?????????????????????");
//				logger.error(ExceptionUtil.getExceptionMessage(e));
//			}finally{
//				try {
//					file.getInputStream().close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return j;
//	}
//
//
//	/**
//	 * ????????????????????????
//	 *
//	 * @return
//	 */
//	@RequestMapping(params = "userSelect")
//	public String userSelect() {
//		return "system/user/userSelect";
//	}
//
//	/**
//	 * ?????????????????????????????????
//	 *
//	 * @param request
//	 * @param response
//	 * @param dataGrid
//	 * @param user
//	 */
//	@RequestMapping(params = "addorupdateMyOrgUser")
//	public ModelAndView addorupdateMyOrgUser(TSUser user, HttpServletRequest req) {
//        List<String> orgIdList = new ArrayList<String>();
//        TSDepart tsDepart = new TSDepart();
//		if (StringUtil.isNotEmpty(user.getId())) {
//			user = systemService.getEntity(TSUser.class, user.getId());
//
//			req.setAttribute("user", user);
//			idandname(req, user);
//			getOrgInfos(req, user);
//		}else{
//			String departid = oConvertUtils.getString(req.getParameter("departid"));
//			TSDepart org = systemService.getEntity(TSDepart.class,departid);
//			req.setAttribute("orgIds", departid);
//			req.setAttribute("departname", org.getDepartname());
//		}
//		req.setAttribute("tsDepart", tsDepart);
//        return new ModelAndView("system/user/myOrgUser");
//	}
//}