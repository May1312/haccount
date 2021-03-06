package org.jeecgframework.web.system.controller.core;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.enums.SysThemesEnum;
import org.jeecgframework.core.util.ContextHolderUtils;
import org.jeecgframework.core.util.IpUtil;
import org.jeecgframework.core.util.JSONHelper;
import org.jeecgframework.core.util.ListtoMenu;
import org.jeecgframework.core.util.LogUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.NumberComparator;
import org.jeecgframework.core.util.PasswordUtil;
import org.jeecgframework.core.util.PropertiesUtil;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.SysThemesUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.system.manager.ClientManager;
import org.jeecgframework.web.system.pojo.base.Client;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.pojo.base.TSFunction;
import org.jeecgframework.web.system.pojo.base.TSPasswordResetkey;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSRoleFunction;
import org.jeecgframework.web.system.pojo.base.TSRoleUser;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.MutiLangServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.service.UserService;
import org.jeecgframework.web.system.sms.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.SSOToken;
import com.baomidou.kisso.common.util.HttpUtil;



/**
 * ????????????????????????
 * @author ?????????
 *
 */
//@Scope("prototype")
@Controller
@RequestMapping("/loginController")
public class LoginController extends BaseController{
	private Logger log = Logger.getLogger(LoginController.class);
	private SystemService systemService;
	private UserService userService;

	@Autowired
	private MutiLangServiceI mutiLangService;

	@Autowired
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	@Autowired
	public void setUserService(UserService userService) {

		this.userService = userService;
	}

	/**
	 * ???????????????????????????
	 * @param key
	 * @return
	 */
	@RequestMapping(params = "goResetPwd")
	public ModelAndView goResetPwd(String key){
		return new ModelAndView("login/resetPwd")
				.addObject("key", key);
	}

	/**
	 * ????????????
	 * @param key
	 * @param password
	 * @return
	 */
	@RequestMapping(params = "resetPwd")
	@ResponseBody
	public AjaxJson resetPwd(String key,String password){
		AjaxJson ajaxJson = new AjaxJson();
		TSPasswordResetkey passwordResetkey = systemService.get(TSPasswordResetkey.class, key);
		Date now = new Date();
		if(passwordResetkey != null && passwordResetkey.getIsReset() != 1 && (now.getTime() - passwordResetkey.getCreateDate().getTime()) < 1000*60*60*3){
			TSUser user = systemService.findUniqueByProperty(TSUser.class, "userName", passwordResetkey.getUsername());
			user.setPassword(PasswordUtil.encrypt(user.getUserName(), password, PasswordUtil.getStaticSalt()));
			systemService.updateEntitie(user);
			passwordResetkey.setIsReset(1);
			systemService.updateEntitie(passwordResetkey);
			ajaxJson.setMsg("??????????????????");
		}else{
			ajaxJson.setSuccess(false);
			ajaxJson.setMsg("??????????????????KEY");
		}

		return ajaxJson;
	}

	/**
	 * ???????????????????????????????????????
	 * @return
	 */
	@RequestMapping(params="goResetPwdMail")
	public ModelAndView goResetPwdMail(){
		return new ModelAndView("login/goResetPwdMail");
	}

	/**
	 * ????????????????????????
	 * @return
	 */
	@RequestMapping(params="sendResetPwdMail")
	@ResponseBody
	public AjaxJson sendResetPwdMail(String email,HttpServletRequest request){
		AjaxJson ajaxJson = new AjaxJson();
		try {

			if(StringUtils.isEmpty(email)){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("????????????????????????");
				return ajaxJson;
			}
			TSUser user = systemService.findUniqueByProperty(TSUser.class, "email", email);
			if(user == null){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("???????????????????????????????????????");
				return ajaxJson;
			}

			//??????????????????????????????
			String hql = "from TSPasswordResetkey bean where bean.username = '" + user.getUserName() + "' and bean.isReset = 0 order by bean.createDate desc limit 1";
			List<TSPasswordResetkey> resetKeyList = systemService.findHql(hql);
			if(resetKeyList != null && !resetKeyList.isEmpty()){
				TSPasswordResetkey resetKey = resetKeyList.get(0);
				Date now = new Date();
				if(resetKey.getEmail().equals(email) && (now.getTime() - resetKey.getCreateDate().getTime()) < (1000*60*60*3 - 1000*60*5)){
					ajaxJson.setSuccess(false);
					ajaxJson.setMsg("?????????????????????????????????????????????????????????");
					return ajaxJson;

				}
			}

			TSPasswordResetkey passwordResetKey = new TSPasswordResetkey();
			passwordResetKey.setEmail(email);
			passwordResetKey.setUsername(user.getUserName());
			passwordResetKey.setCreateDate(new Date());
			passwordResetKey.setIsReset(0);
			userService.save(passwordResetKey);


			PropertiesUtil util = new PropertiesUtil("sysConfig.properties");
			StringBuffer contentBuffer = new StringBuffer();
			contentBuffer.append("<div id=\"contentDiv\" onmouseover=\"getTop().stopPropagation(event);\" onclick=\"getTop().preSwapLink(event, 'spam', 'ZC4218-CzCkK82QMqgXIghRxZ93S79');\"");
			contentBuffer.append("style=\"position:relative;font-size:14px;height:auto;padding:15px 15px 10px 15px;z-index:1;zoom:1;line-height:1.7;\" class=\"body\">");
			contentBuffer.append("<div id=\"qm_con_body\"><div id=\"mailContentContainer\" class=\"qmbox qm_con_body_content qqmail_webmail_only\" style=\"\">");
			contentBuffer.append("<table style=\"margin: 25px auto;\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"648\" align=\"center\">");
			String title = util.readProperty("resetpwd.mail.title");
			contentBuffer.append("<tbody><tr><td style=\"color:#40AA53;\"><h1 style=\"margin-bottom:10px;\">"+title +"</h1></td></tr>");
			contentBuffer.append("<tr><td style=\"border-left: 1px solid #D1FFD1; padding: 20px 20px 0px; background: none repeat scroll 0% 0% #ffffff; border-top: 5px solid #40AA53; border-right: 1px solid #D1FFD1;\">");
			contentBuffer.append("<p>?????? </p></td></tr>");
			contentBuffer.append("<tr><td style=\"border-left: 1px solid #D1FFD1; padding: 0px 45px 0px; background: none repeat scroll 0% 0% #ffffff; border-right: 1px solid #D1FFD1;\">");
			String content = util.readProperty("resetpwd.mail.content");
			if(content.indexOf("${username}") > -1){
				content = content.replace("${username}", user.getUserName());
			}
			contentBuffer.append("<p>"+content+"</p></td></tr>");
			contentBuffer.append("<tr><td style=\"border-left: 1px solid #D1FFD1; padding: 10px 20px; background: none repeat scroll 0% 0% #ffffff; border-right: 1px solid #D1FFD1;\">");
			contentBuffer.append("<p style=\"font-weight:bold\">??????????????????????????????????????????<br><br>");
			String url = request.getScheme() + "://" + request.getServerName()+ ":" + request.getServerPort() + request.getContextPath() +"/loginController.do?goResetPwd&key=" + passwordResetKey.getId();
			contentBuffer.append("<a href=\"" + url + "\" target=\"_blank\">");
			contentBuffer.append(url);
			contentBuffer.append("</a></p></td></tr>");
			contentBuffer.append("<tr><td style=\"border-bottom: 1px solid #D1FFD1; border-left: 1px solid #D1FFD1; padding: 0px 20px 20px; background: none repeat scroll 0% 0% #ffffff; border-right: 1px solid #D1FFD1;\">");
			contentBuffer.append("<hr style=\"color:#ccc;\">");
			String commentUrl = "http://www.jeecg.org";
			contentBuffer.append("<p style=\"color:#060;font-size:9pt;\">????????????????????????????????? <a href=\""+commentUrl+"\" target=\"_blank\">"+commentUrl+"</a></p>");
			contentBuffer.append("</td></tr></tbody></table>");
			contentBuffer.append("<br><br><div style=\"width:1px;height:0px;overflow:hidden\"><img style=\"width:0;height:0\" src=\"javascript:;\"></div>");
			contentBuffer.append("<style type=\"text/css\">.qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta {display: none !important;}</style></div></div><!-- --><style>#mailContentContainer .txt {height:auto;}</style> ");
			MailUtil.sendEmail(util.readProperty("mail.smtpHost"), email,"??????????????????",
					contentBuffer.toString(), util.readProperty("mail.sender"),
					util.readProperty("mail.user"), util.readProperty("mail.pwd"));
			ajaxJson.setMsg("??????????????????????????????");


		} catch (Exception e) {
			if("javax.mail.AuthenticationFailedException".equals(e.getClass().getName())){
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("???????????????????????????????????????????????????" );
				log.error("???????????????????????????????????????????????????????????????",e);
			}else{
				ajaxJson.setSuccess(false);
				ajaxJson.setMsg("?????????????????????" + e.getMessage());
				log.error("?????????????????????" + e.getMessage(),e);
			}

		}
		return ajaxJson;
	}

	@RequestMapping(params = "goPwdInit")
	public String goPwdInit() {
		return "login/pwd_init";
	}


	/**
	 * ??????????????????
	 *
	 * @param user
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "checkuser")
	@ResponseBody
	public AjaxJson checkuser(TSUser user, HttpServletRequest req) {
		HttpSession session = req.getSession();
		AjaxJson j = new AjaxJson();
		//????????????
		if (req.getParameter("langCode")!=null) {
			req.getSession().setAttribute("lang", req.getParameter("langCode"));
		}

		//??????????????????????????????
		String returnURL = req.getParameter("ReturnURL");
		if(StringUtils.isNotEmpty(returnURL)){
			req.getSession().setAttribute("ReturnURL", returnURL);
		}


		//?????????
		String randCode = req.getParameter("randCode");
		if (StringUtils.isEmpty(randCode)) {
			j.setMsg(mutiLangService.getLang("common.enter.verifycode"));
			j.setSuccess(false);
		} else if (!randCode.equalsIgnoreCase(String.valueOf(session.getAttribute("randCode")))) {
			j.setMsg(mutiLangService.getLang("common.verifycode.error"));
			j.setSuccess(false);

		} else if (isInBlackList(IpUtil.getIpAddr(req))){
			j.setMsg(mutiLangService.getLang("common.blacklist.error"));
			j.setSuccess(false);
		}

		else {
			//????????????????????????
			TSUser u = userService.checkUserExits(user);
			if (u == null) {
				u = userService.findUniqueByProperty(TSUser.class, "email", user.getUserName());
				if(u == null || u.getPassword().equals(PasswordUtil.encrypt(u.getUserName(), u.getPassword(), PasswordUtil.getStaticSalt()))){
					j.setMsg(mutiLangService.getLang("common.username.or.password.error"));
					j.setSuccess(false);
					return j;
				}
			}
			if (u != null && u.getStatus() != 0) {
				// ?????????????????????????????????????????????????????????????????????????????????
				Map<String, Object> attrMap = new HashMap<String, Object>();
				j.setAttributes(attrMap);

				String orgId = req.getParameter("orgId");
				if (oConvertUtils.isEmpty(orgId)) {
					// ??????????????????????????????????????????????????????????????????
					Long orgNum = systemService.getCountForJdbc("select count(1) from t_s_user_org where user_id = '" + u.getId() + "'");
					if (orgNum > 1) {
						attrMap.put("orgNum", orgNum);
						attrMap.put("user", u);
					} else {
						Map<String, Object> userOrgMap = systemService.findOneForJdbc("select org_id as orgId from t_s_user_org where user_id=?", u.getId());
						saveLoginSuccessInfo(req, u, (String) userOrgMap.get("orgId"));
					}
				} else {
					attrMap.put("orgNum", 1);
					saveLoginSuccessInfo(req, u, orgId);
				}
			} else {

				j.setMsg(mutiLangService.getLang("common.lock.user"));

				j.setSuccess(false);
			}
		}
		return j;
	}

	private boolean isInBlackList(String ip){
		Long orgNum =systemService.getCountForJdbc("select count(*) from t_s_black_list where ip =  '" + ip + "'");
		return orgNum!=0?true:false;
	}

	/**
	 * ????????????????????????
	 *
	 * @param user
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "changeDefaultOrg")
	@ResponseBody
	public AjaxJson changeDefaultOrg(TSUser user, HttpServletRequest req) {
		AjaxJson j = new AjaxJson();
		Map<String, Object> attrMap = new HashMap<String, Object>();
		String orgId = req.getParameter("orgId");
		TSUser u = userService.checkUserExits(user);
		if(u == null){
			u = userService.findUniqueByProperty(TSUser.class, "email", user.getUserName());
		}
		if (oConvertUtils.isNotEmpty(orgId)) {
			attrMap.put("orgNum", 1);
			saveLoginSuccessInfo(req, u, orgId);
		}
		return j;
	}

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     * @param req request
     * @param user ??????????????????
     * @param orgId ????????????
     */
    private void saveLoginSuccessInfo(HttpServletRequest req, TSUser user, String orgId) {
    	String message = null;
        TSDepart currentDepart = systemService.get(TSDepart.class, orgId);
        user.setCurrentDepart(currentDepart);

        HttpSession session = ContextHolderUtils.getSession();

		user.setDepartid(orgId);

		session.setAttribute(ResourceUtil.LOCAL_CLINET_USER, user);
       message = mutiLangService.getLang("common.user") + ": " + user.getUserName() + "["+ currentDepart.getDepartname() + "]" + mutiLangService.getLang("common.login.success");

        String browserType = "";
        Cookie[] cookies = req.getCookies();
        for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if("BROWSER_TYPE".equals(cookie.getName())){
				browserType = cookie.getValue();
			}
		}
        session.setAttribute("brower_type", browserType);

        //??????session?????? ?????? ??????session???????????????????????????????????????????????????????????????Client??????
        Client clientOld = ClientManager.getInstance().getClient(session.getId());
		if(clientOld == null || clientOld.getUser() ==null ||user.getUserName().equals(clientOld.getUser().getUserName())){
			Client client = new Client();
	        client.setIp(IpUtil.getIpAddr(req));
	        client.setLogindatetime(new Date());
	        client.setUser(user);
	        ClientManager.getInstance().addClinet(session.getId(), client);
		} else {//???????????????????????????session?????????session=req.getSession(true)?????????session
			ClientManager.getInstance().removeClinet(session.getId());
			session.invalidate();
			session = req.getSession(true);//session?????????
			session.setAttribute(ResourceUtil.LOCAL_CLINET_USER, user);
			session.setAttribute("randCode",req.getParameter("randCode"));//???????????????
			checkuser(user,req);
		}



        // ??????????????????
        systemService.addLog(message, Globals.Log_Type_LOGIN, Globals.Log_Leavel_INFO);
    }


    /**
	 * ????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "login")
	public String login(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response) {
		TSUser user = ResourceUtil.getSessionUser();
		String roles = "";
		if (user != null) {
			List<TSRoleUser> rUsers = systemService.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
			for (TSRoleUser ru : rUsers) {
				TSRole role = ru.getTSRole();
				roles += role.getRoleName() + ",";
			}
			if (roles.length() > 0) {
				roles = roles.substring(0, roles.length() - 1);
			}

            modelMap.put("roleName", roles.length()>3?roles.substring(0,3)+"...":roles);
            modelMap.put("userName", user.getUserName().length()>5?user.getUserName().substring(0, 5)+"...":user.getUserName());
            modelMap.put("portrait", user.getPortrait());

            modelMap.put("currentOrgName", ClientManager.getInstance().getClient().getUser().getCurrentDepart().getDepartname());


			SysThemesEnum sysTheme = SysThemesUtil.getSysTheme(request);
			if("fineui".equals(sysTheme.getStyle())|| "ace".equals(sysTheme.getStyle())||"diy".equals(sysTheme.getStyle())||"acele".equals(sysTheme.getStyle())||"hplus".equals(sysTheme.getStyle())){
				request.setAttribute("menuMap", getFunctionMap(user));
			}

			Cookie cookie = new Cookie("JEECGINDEXSTYLE", sysTheme.getStyle());
			//??????cookie?????????????????????
			cookie.setMaxAge(3600*24*30);
			response.addCookie(cookie);

			Cookie zIndexCookie = new Cookie("ZINDEXNUMBER", "1990");
			zIndexCookie.setMaxAge(3600*24);//??????
			response.addCookie(zIndexCookie);

			/*
			 * ???????????? - ???????????????????????????????????????????????? ReturnURL ??????
			 * HttpUtil.decodeURL(xx) ??????????????????
			 */
			String returnURL = (String)request.getSession().getAttribute("ReturnURL");
			log.info("login ????????????returnURL???"+returnURL);
			if(StringUtils.isNotEmpty(returnURL)){
				SSOToken st = new SSOToken(request);
				st.setId(UUID.randomUUID().getMostSignificantBits());
				st.setUid(user.getUserName());
				st.setType(1);
				//request.setAttribute(SSOConfig.SSO_COOKIE_MAXAGE, maxAge);
				// ?????????????????? Cookie maxAge ???????????? ???????????????????????????????????????????????? - ?????????????????????????????? ???
				//  maxAge ?????????-1 ?????????????????????????????? 0 ???????????? 120 ??????Cookie?????????2??????(???????????????)
//				request.setAttribute(SSOConfig.SSO_COOKIE_MAXAGE, 60);
				SSOHelper.setSSOCookie(request, response, st, true);
				returnURL = HttpUtil.decodeURL(returnURL);
				log.info("login ????????????returnURL???"+returnURL);
				request.getSession().removeAttribute("ReturnURL");
				try {
					response.sendRedirect(returnURL);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			return sysTheme.getIndexPath();
		} else {

			//???????????? - ????????????
			String returnURL = (String)request.getSession().getAttribute("ReturnURL");
			if(StringUtils.isNotEmpty(returnURL)){
				request.setAttribute("ReturnURL", returnURL);
			}

			return "login/login";
		}

	}

	/**
	 * ????????????
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "logout")
	public ModelAndView logout(HttpServletRequest request) {
		HttpSession session = ContextHolderUtils.getSession();
		TSUser user = ResourceUtil.getSessionUser();

		try {
			systemService.addLog("??????" + user!=null?user.getUserName():"" + "?????????",Globals.Log_Type_EXIT, Globals.Log_Leavel_INFO);
		} catch (Exception e) {
			LogUtil.error(e.toString());
		}

		ClientManager.getInstance().removeClinet(session.getId());
		session.invalidate();
		ModelAndView modelAndView = new ModelAndView(new RedirectView("loginController.do?login"));
		return modelAndView;
	}

	/**
	 * ????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "left")
	public ModelAndView left(HttpServletRequest request) {
		TSUser user = ResourceUtil.getSessionUser();
		HttpSession session = ContextHolderUtils.getSession();
        ModelAndView modelAndView = new ModelAndView();
		// ??????????????????
		if (user.getId() == null) {
			session.removeAttribute(Globals.USER_SESSION);
            modelAndView.setView(new RedirectView("loginController.do?login"));
		}else{
            modelAndView.setViewName("main/left");
            request.setAttribute("menuMap", getFunctionMap(user));
        }
		return modelAndView;
	}

	/**
	 * ???????????????map
	 *
	 * @param user
	 * @return
	 */
	private Map<Integer, List<TSFunction>> getFunctionMap(TSUser user) {
		HttpSession session = ContextHolderUtils.getSession();
		Client client = ClientManager.getInstance().getClient(session.getId());
		if (client.getFunctionMap() == null || client.getFunctionMap().size() == 0) {
			Map<Integer, List<TSFunction>> functionMap = new HashMap<Integer, List<TSFunction>>();
			Map<String, TSFunction> loginActionlist = getUserFunction(user);
			if (loginActionlist.size() > 0) {
				Collection<TSFunction> allFunctions = loginActionlist.values();
				for (TSFunction function : allFunctions) {

		            if(function.getFunctionType().intValue()==Globals.Function_TYPE_FROM.intValue()){
						//??????????????????????????? ??????????????????????????????
						continue;
					}

					if (!functionMap.containsKey(function.getFunctionLevel() + 0)) {
						functionMap.put(function.getFunctionLevel() + 0,
								new ArrayList<TSFunction>());
					}
					functionMap.get(function.getFunctionLevel() + 0).add(function);
				}
				// ???????????????
				Collection<List<TSFunction>> c = functionMap.values();
				for (List<TSFunction> list : c) {

					for (TSFunction function : list) {
						//????????????????????? ??????????????????
						if(function.hasSubFunction(functionMap))function.setFunctionUrl("");
					}

					Collections.sort(list, new NumberComparator());
				}
			}
			client.setFunctionMap(functionMap);

			//?????????????????????????????????
			loginActionlist.clear();

			return functionMap;
		}else{
			return client.getFunctionMap();
		}
	}

	/**
	 * ?????????????????????????????????
	 */
	@RequestMapping(params = "getAutocomplete",method ={RequestMethod.GET, RequestMethod.POST})
	public void getAutocomplete(HttpServletRequest request, HttpServletResponse response) {
		String searchVal = request.getParameter("q");
		//?????????session??????????????????
		HttpSession session = ContextHolderUtils.getSession();
		Client client = ClientManager.getInstance().getClient(session.getId());
		//?????????????????????map??????
		Map<Integer, List<TSFunction>> map=client.getFunctionMap();
		//??????list??????????????????
		List<TSFunction>autoList = new ArrayList<TSFunction>();
		//??????map??????????????????
		for(int t=0;t<map.size();t++){
			//??????map??????????????????TSFuction ???List??????
			List<TSFunction> list = map.get(t);
			//??????List??????TSFuction??????functionname
			for(int i =0;i<list.size();i++){
				//??????functionname??????????????????????????????????????????????????????????????????MutiLangUtil??????getLang()?????????
				String name=MutiLangUtil.getLang(list.get(i).getFunctionName());
				if(name.indexOf(searchVal)!= -1 ){
					TSFunction  ts =new TSFunction();
					ts.setFunctionName(MutiLangUtil.getLang(list.get(i).getFunctionName()));
					autoList.add(ts);
				}
			}
		}
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.getWriter().write(JSONHelper.listtojson(new String[]{"functionName"},1,autoList));
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
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "getUrlpage")
	@ResponseBody
	public String getUrlpage(HttpServletRequest request,HttpServletResponse response) {
		String urlname = request.getParameter("urlname");
		HttpSession session = ContextHolderUtils.getSession();
		Client client = ClientManager.getInstance().getClient(session.getId());
		Map<Integer, List<TSFunction>> map=client.getFunctionMap();
		List<TSFunction>autoList = new ArrayList<TSFunction>();
		for(int t=0;t<map.size();t++){
			List<TSFunction> list = map.get(t);
			for(int i =0;i<list.size();i++){
				String funname=MutiLangUtil.getLang(list.get(i).getFunctionName());
				if(urlname.equals(funname)){
					TSFunction ts =new TSFunction();
					ts.setFunctionUrl(list.get(i).getFunctionUrl());
					autoList.add(ts);
				}
			}
		}
		if(autoList.size()==0){
			return null;
		}else{
			String name =autoList.get(0).getFunctionUrl();
			return name;
		}

	}


	/**
	 * ????????????????????????
	 *
	 * @param user
	 * @return
	 */
	private Map<String, TSFunction> getUserFunction(TSUser user) {
		HttpSession session = ContextHolderUtils.getSession();
		Client client = ClientManager.getInstance().getClient(session.getId());

		if (client.getFunctions() == null || client.getFunctions().size() == 0) {

			Map<String, TSFunction> loginActionlist = new HashMap<String, TSFunction>();

			 /*String hql="from TSFunction t where t.id in  (select d.TSFunction.id from TSRoleFunction d where d.TSRole.id in(select t.TSRole.id from TSRoleUser t where t.TSUser.id ='"+
	           user.getId()+"' ))";
	           String hql2="from TSFunction t where t.id in  ( select b.tsRole.id from TSRoleOrg b where b.tsDepart.id in(select a.tsDepart.id from TSUserOrg a where a.tsUser.id='"+
	           user.getId()+"'))";
	           List<TSFunction> list = systemService.findHql(hql);
	           log.info("role functions:  "+list.size());
	           for(TSFunction function:list){
	              loginActionlist.put(function.getId(),function);
	           }
	           List<TSFunction> list2 = systemService.findHql(hql2);
	           log.info("org functions: "+list2.size());
	           for(TSFunction function:list2){
	              loginActionlist.put(function.getId(),function);
	           }*/

	           StringBuilder hqlsb1=new StringBuilder("select distinct f from TSFunction f,TSRoleFunction rf,TSRoleUser ru  ").append("where ru.TSRole.id=rf.TSRole.id and rf.TSFunction.id=f.id and ru.TSUser.id=? ");

	           StringBuilder hqlsb2=new StringBuilder("select distinct c from TSFunction c,TSRoleFunction rf,TSRoleOrg b,TSUserOrg a ")
	           							.append("where a.tsDepart.id=b.tsDepart.id and b.tsRole.id=rf.TSRole.id and rf.TSFunction.id=c.id and a.tsUser.id=?");
	           //TODO hql??????????????? ?????????????????????

	           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	           log.info("================================????????????:"+sdf.format(new Date())+"==============================");
	           long start = System.currentTimeMillis();
	           List<TSFunction> list1 = systemService.findHql(hqlsb1.toString(),user.getId());
	           List<TSFunction> list2 = systemService.findHql(hqlsb2.toString(),user.getId());
	           long end = System.currentTimeMillis();
	           log.info("================================????????????:"+sdf.format(new Date())+"==============================");
	           log.info("================================??????:"+(end-start)+"ms==============================");
	           for(TSFunction function:list1){
		              loginActionlist.put(function.getId(),function);
		       }
	           for(TSFunction function:list2){
		              loginActionlist.put(function.getId(),function);
		       }
            client.setFunctions(loginActionlist);

            //?????????????????????????????????
            list2.clear();
            list1.clear();

		}
		return client.getFunctions();
	}

    /**
     * ?????? ???????????? ?????? ??????????????????
     * @param loginActionlist ???????????????????????????
     * @param role ????????????
     * @deprecated
     */
    private void assembleFunctionsByRole(Map<String, TSFunction> loginActionlist, TSRole role) {
        List<TSRoleFunction> roleFunctionList = systemService.findByProperty(TSRoleFunction.class, "TSRole.id", role.getId());
        for (TSRoleFunction roleFunction : roleFunctionList) {
            TSFunction function = roleFunction.getTSFunction();
           if(function.getFunctionType().intValue()==Globals.Function_TYPE_FROM.intValue()){
				//??????????????????????????? ??????????????????????????????
				continue;
			}
           loginActionlist.put(function.getId(), function);
        }
    }


    /**
	 * ????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "home")
	public ModelAndView home(HttpServletRequest request) {

		SysThemesEnum sysTheme = SysThemesUtil.getSysTheme(request);
		//ACE ACE2 DIY????????????home.jsp?????????????????????js???css??????
		if("ace".equals(sysTheme.getStyle())||"diy".equals(sysTheme.getStyle())||"acele".equals(sysTheme.getStyle())){
			request.setAttribute("show", "1");
		} else {//default???shortcut????????????????????????????????????????????????
			request.setAttribute("show", "0");
		}

		return new ModelAndView("main/home");
	}

	  /**
	 * ACE????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "acehome")
	public ModelAndView acehome(HttpServletRequest request) {

		SysThemesEnum sysTheme = SysThemesUtil.getSysTheme(request);
		//ACE ACE2 DIY????????????home.jsp?????????????????????js???css??????
		if("ace".equals(sysTheme.getStyle())||"diy".equals(sysTheme.getStyle())||"acele".equals(sysTheme.getStyle())){
			request.setAttribute("show", "1");
		} else {//default???shortcut????????????????????????????????????????????????
			request.setAttribute("show", "0");
		}

		return new ModelAndView("main/acehome");
	}
	/**
	 * HPLUS????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "hplushome")
	public ModelAndView hplushome(HttpServletRequest request) {

		SysThemesEnum sysTheme = SysThemesUtil.getSysTheme(request);
		//ACE ACE2 DIY????????????home.jsp?????????????????????js???css??????
		/*if("ace".equals(sysTheme.getStyle())||"diy".equals(sysTheme.getStyle())||"acele".equals(sysTheme.getStyle())){
			request.setAttribute("show", "1");
		} else {//default???shortcut????????????????????????????????????????????????
			request.setAttribute("show", "0");
		}*/

		return new ModelAndView("main/hplushome");
	}

	/**
	 * fineUI????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "fineuiHome")
	public ModelAndView fineuiHome(HttpServletRequest request) {
		return new ModelAndView("main/fineui_home");
	}

	/**
	 * ???????????????????????????
	 *
	 * @return
	 */
	@RequestMapping(params = "noAuth")
	public ModelAndView noAuth(HttpServletRequest request) {
		return new ModelAndView("common/noAuth");
	}
	/**
	 * @Title: top
	 * @Description: bootstrap??????????????????
	 * @param request
	 * @return ModelAndView
	 * @throws
	 */
	@RequestMapping(params = "top")
	public ModelAndView top(HttpServletRequest request) {
		TSUser user = ResourceUtil.getSessionUser();
		HttpSession session = ContextHolderUtils.getSession();
		// ??????????????????
		if (user.getId() == null) {
			session.removeAttribute(Globals.USER_SESSION);
			return new ModelAndView(
					new RedirectView("loginController.do?login"));
		}
		request.setAttribute("menuMap", getFunctionMap(user));
		return new ModelAndView("main/bootstrap_top");
	}
	/**
	 * @Title: top
	 * @author gaofeng
	 * @Description: shortcut??????????????????
	 * @param request
	 * @return ModelAndView
	 * @throws
	 */
	@RequestMapping(params = "shortcut_top")
	public ModelAndView shortcut_top(HttpServletRequest request) {
		TSUser user = ResourceUtil.getSessionUser();
		HttpSession session = ContextHolderUtils.getSession();
		// ??????????????????
		if (user.getId() == null) {
			session.removeAttribute(Globals.USER_SESSION);
			return new ModelAndView(
					new RedirectView("loginController.do?login"));
		}
		request.setAttribute("menuMap", getFunctionMap(user));
		return new ModelAndView("main/shortcut_top");
	}

	/**
	 * @Title: top
	 * @author:gaofeng
	 * @Description: shortcut?????????????????????????????????????????????ajax???????????????????????????????????????????????????
	 * @return AjaxJson
	 * @throws
	 */
    @RequestMapping(params = "primaryMenu")
    @ResponseBody
	public String getPrimaryMenu() {
		List<TSFunction> primaryMenu = getFunctionMap(ResourceUtil.getSessionUser()).get(0);
        String floor = "";

        if (primaryMenu == null) {
            return floor;
        }

        for (TSFunction function : primaryMenu) {
            if(function.getFunctionLevel() == 0) {
            	String lang_key = function.getFunctionName();
            	String lang_context = mutiLangService.getLang(lang_key);
            	lang_context=lang_context.trim();

            	if("????????????".equals(lang_context)){

                	String ss = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
                    floor += " <li style='position: relative;'>"+ss+"<img class='imag1' src='plug-in/login/images/ywsq.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/ywsq-up.png' style='display: none;' /></li> ";
                }else if("????????????".equals(lang_context)){

                	String ss = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
                    floor += " <li style='position: relative;'>"+ss+"<img class='imag1' src='plug-in/login/images/grbg.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/grbg-up.png' style='display: none;' /></li> ";
                }else if("????????????".equals(lang_context)){

                	String ss = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
                    floor += " <li style='position: relative;'>"+ss+"<img class='imag1' src='plug-in/login/images/lcsj.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/lcsj-up.png' style='display: none;' /></li> ";
                }else if("Online ??????".equals(lang_context)){

                    floor += " <li><img class='imag1' src='plug-in/login/images/online.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/online_up.png' style='display: none;' />" + " </li> ";
                }else if("???????????????".equals(lang_context)){

                	String ss = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
                    floor += " <li style='position: relative;'>"+ss+"<img class='imag1' src='plug-in/login/images/zdybd.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/zdybd-up.png' style='display: none;' /></li> ";
                }else if("????????????".equals(lang_context)){

                    floor += " <li><img class='imag1' src='plug-in/login/images/xtjk.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/xtjk_up.png' style='display: none;' />" + " </li> ";
                }else if("????????????".equals(lang_context)){

                    floor += " <li><img class='imag1' src='plug-in/login/images/tjbb.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/tjbb_up.png' style='display: none;' />" + " </li> ";
                }else if("???????????????".equals(lang_context)){
                	String ss = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
                    floor += " <li style='position: relative;'>"+ss+"<img class='imag1' src='plug-in/login/images/msg.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/msg_up.png' style='display: none;' /></li> ";
                }else if("????????????".equals(lang_context)){

                    floor += " <li><img class='imag1' src='plug-in/login/images/xtgl.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/xtgl_up.png' style='display: none;' />" + " </li> ";
                }else if("????????????".equals(lang_context)){

                    floor += " <li><img class='imag1' src='plug-in/login/images/cysl.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/cysl_up.png' style='display: none;' />" + " </li> ";
                }else if(lang_context.contains("????????????")){

                	String s = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'>????????????</div>";
                    floor += " <li style='position: relative;'>"+s+"<img class='imag1' src='plug-in/login/images/msg.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/msg_up.png' style='display: none;' /></li> ";
                }else{
                    //???????????????????????????????????????
                	String s="";
                    if(lang_context.length()>=5 && lang_context.length()<7){
                        s = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
                    }else if(lang_context.length()<5){
                        s = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'>"+ lang_context +"</div>";
                    }else if(lang_context.length()>=7){
                        s = "<div style='width:67px;position: absolute;top:39px;text-align:center;color:#909090;font-size:13px;'><span style='letter-spacing:-1px;'>"+ lang_context.substring(0, 6) +"</span></div>";
                    }
                    floor += " <li style='position: relative;'>"+s+"<img class='imag1' src='plug-in/login/images/default.png' /> "
                            + " <img class='imag2' src='plug-in/login/images/default_up.png' style='display: none;' />"
                            +"</li> ";
                }

            }
        }

		return floor;
	}

	/**
	 * @Title: top
	 * @author:wangkun
	 * @Description: shortcut?????????????????????????????????????????????ajax???????????????????????????????????????????????????
	 * @return AjaxJson
	 * @throws
	 */
	@RequestMapping(params = "primaryMenuDiy")
	@ResponseBody
	public String getPrimaryMenuDiy() {
		//???????????????
		List<TSFunction> primaryMenu = getFunctionMap(ResourceUtil.getSessionUser()).get(1);
		String floor = "";
		if (primaryMenu == null) {
			return floor;
		}
		String menuString = "user.manage role.manage department.manage menu.manage";
		for (TSFunction function : primaryMenu) {
			if(menuString.contains(function.getFunctionName())){
				if(function.getFunctionLevel() == 1) {

					String lang_key = function.getFunctionName();
					String lang_context = mutiLangService.getLang(lang_key);
					if("??????".equals(lang_key)){
						lang_context = "??????";
						String s = "";
						s = "<div style='width:67px;position: absolute;top:47px;text-align:center;color:#000000;font-size:12px;'>"+ lang_context +"</div>";
						floor += " <li><img class='imag1' src='plug-in/login/images/head_icon1.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/head_icon1.png' style='display: none;' />" + s + " </li> ";
					} else if("Online ??????".equals(lang_context)){

						floor += " <li><img class='imag1' src='plug-in/login/images/online.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/online_up.png' style='display: none;' />" + " </li> ";
					}else if("????????????".equals(lang_context)){

						floor += " <li><img class='imag1' src='plug-in/login/images/guanli.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/guanli_up.png' style='display: none;' />" + " </li> ";
					}else if("????????????".equals(lang_context)){

						floor += " <li><img class='imag1' src='plug-in/login/images/xtgl.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/xtgl_up.png' style='display: none;' />" + " </li> ";
					}else if("????????????".equals(lang_context)){

						floor += " <li><img class='imag1' src='plug-in/login/images/cysl.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/cysl_up.png' style='display: none;' />" + " </li> ";
					}else if("????????????".equals(lang_context)){

						floor += " <li><img class='imag1' src='plug-in/login/images/xtjk.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/xtjk_up.png' style='display: none;' />" + " </li> ";
					}else if(lang_context.contains("????????????")){
						String s = "<div style='width:67px;position: absolute;top:40px;text-align:center;color:#909090;font-size:12px;'>????????????</div>";
						floor += " <li style='position: relative;'><img class='imag1' src='plug-in/login/images/msg.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/msg_up.png' style='display: none;' />"
								+ s +"</li> ";
					}else{
						//???????????????????????????????????????
						String s = "";
						if(lang_context.length()>=5 && lang_context.length()<7){
							s = "<div style='width:67px;position: absolute;top:40px;text-align:center;color:#000000;font-size:12px;'><span style='letter-spacing:-1px;'>"+ lang_context +"</span></div>";
						}else if(lang_context.length()<5){
							s = "<div style='width:67px;position: absolute;top:40px;text-align:center;color:#000000;font-size:12px;'>"+ lang_context +"</div>";
						}else if(lang_context.length()>=7){
							s = "<div style='width:67px;position: absolute;top:40px;text-align:center;color:#000000;font-size:12px;'><span style='letter-spacing:-1px;'>"+ lang_context.substring(0, 6) +"</span></div>";
						}
						floor += " <li style='position: relative;'><img class='imag1' src='plug-in/login/images/head_icon2.png' /> "
								+ " <img class='imag2' src='plug-in/login/images/default_up.png' style='display: none;' />"
								+ s +"</li> ";
					}
				}
			}
		}

		return floor;
	}
	/**
	 * ????????????????????????????????????
	 */
	@RequestMapping(params = "getPrimaryMenuForWebos")
	@ResponseBody
	public AjaxJson getPrimaryMenuForWebos() {
		AjaxJson j = new AjaxJson();
		//??????????????????Session??????????????????????????????????????????
		Object getPrimaryMenuForWebos =  ContextHolderUtils.getSession().getAttribute("getPrimaryMenuForWebos");
		if(oConvertUtils.isNotEmpty(getPrimaryMenuForWebos)){
			j.setMsg(getPrimaryMenuForWebos.toString());
		}else{
			String PMenu = ListtoMenu.getWebosMenu(getFunctionMap(ResourceUtil.getSessionUser()));
			ContextHolderUtils.getSession().setAttribute("getPrimaryMenuForWebos", PMenu);
			j.setMsg(PMenu);
		}
		return j;
	}

	/**
	 * ACE????????????
	 * @return
	 */
	@RequestMapping(params = "login3")
	public String login3(){
		return "login/login3";
	}
}