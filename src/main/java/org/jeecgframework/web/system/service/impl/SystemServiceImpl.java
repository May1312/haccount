package org.jeecgframework.web.system.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.BrowserUtils;
import org.jeecgframework.core.util.ContextHolderUtils;
import org.jeecgframework.core.util.IpUtil;
import org.jeecgframework.core.util.MutiLangUtil;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.system.dao.JeecgDictDao;
import org.jeecgframework.web.system.pojo.base.DictEntity;
import org.jeecgframework.web.system.pojo.base.TSDatalogEntity;
import org.jeecgframework.web.system.pojo.base.TSDepartAuthGroupEntity;
import org.jeecgframework.web.system.pojo.base.TSDepartAuthgFunctionRelEntity;
import org.jeecgframework.web.system.pojo.base.TSFunction;
import org.jeecgframework.web.system.pojo.base.TSIcon;
import org.jeecgframework.web.system.pojo.base.TSLog;
import org.jeecgframework.web.system.pojo.base.TSOperation;
import org.jeecgframework.web.system.pojo.base.TSRole;
import org.jeecgframework.web.system.pojo.base.TSRoleFunction;
import org.jeecgframework.web.system.pojo.base.TSRoleUser;
import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSTypegroup;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.util.OrgConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("systemService")
public class SystemServiceImpl extends CommonServiceImpl implements SystemService {
	@Autowired
	private JeecgDictDao jeecgDictDao;

	@Transactional(readOnly = true)
	public TSUser checkUserExits(TSUser user) throws Exception {
		return this.commonDao.getUserByUserIdAndUserNameExits(user);
	}

	@Transactional(readOnly = true)
	public List<DictEntity> queryDict(String dicTable, String dicCode,String dicText){
		List<DictEntity> dictList = null;
		//step.1 ?????????????????????????????????????????????
		if(StringUtil.isEmpty(dicTable)){
			dictList = jeecgDictDao.querySystemDict(dicCode);
			for(DictEntity t:dictList){
				t.setTypename(MutiLangUtil.getLang(t.getTypename()));
			}
		}else {
			dicText = StringUtil.isEmpty(dicText, dicCode);
			dictList = jeecgDictDao.queryCustomDict(dicTable, dicCode, dicText);
		}
		return dictList;
	}

	/**
	 * ????????????
	 */
	public void addLog(String logcontent, Short loglevel, Short operatetype) {
		HttpServletRequest request = ContextHolderUtils.getRequest();
		String broswer = BrowserUtils.checkBrowse(request);
		TSLog log = new TSLog();
		log.setLogcontent(logcontent);
		log.setLoglevel(loglevel);
		log.setOperatetype(operatetype);

		log.setNote(IpUtil.getIpAddr(request));

		log.setBroswer(broswer);
		/*start dangzhenghui 201703016TASK #1784 ???online bug???Online ??????????????????????????????*/
		log.setOperatetime(new Date());
		/* end dangzhenghui 201703016TASK #1784 ???online bug???Online ??????????????????????????????*/
//		log.setTSUser(ResourceUtil.getSessionUser());
		/*start chenqian 201708031TASK #2317 ????????????????????????????????????????????????????????????????????? [???????????????] [???????????????]*/
		TSUser u = ResourceUtil.getSessionUser();
		if(u!=null){
			log.setUserid(u.getId());
			log.setUsername(u.getUserName());
			log.setRealname(u.getRealName());
		}

		commonDao.save(log);
	}

	/**
	 * ???????????????????????????????????????Type,???????????????????????????
	 *
	 * @param typecode
	 * @param typename
	 * @return
	 */
	@Transactional(readOnly = true)
	public TSType getType(String typecode, String typename, TSTypegroup tsTypegroup) {
		//TSType actType = commonDao.findUniqueByProperty(TSType.class, "typecode", typecode,tsTypegroup.getId());
		List<TSType> ls = commonDao.findHql("from TSType where typecode = ? and typegroupid = ?",typecode,tsTypegroup.getId());
		TSType actType = null;
		if (ls == null || ls.size()==0) {
			actType = new TSType();
			actType.setTypecode(typecode);
			actType.setTypename(typename);
			actType.setTSTypegroup(tsTypegroup);
			commonDao.save(actType);
		}else{
			actType = ls.get(0);
		}
		return actType;

	}

	/**
	 * ???????????????????????????????????????TypeGroup,???????????????????????????
	 *
	 * @param typecode
	 * @param typename
	 * @return
	 */
	@Transactional(readOnly = true)
	public TSTypegroup getTypeGroup(String typegroupcode, String typgroupename) {
		TSTypegroup tsTypegroup = commonDao.findUniqueByProperty(TSTypegroup.class, "typegroupcode", typegroupcode);
		if (tsTypegroup == null) {
			tsTypegroup = new TSTypegroup();
			tsTypegroup.setTypegroupcode(typegroupcode);
			tsTypegroup.setTypegroupname(typgroupename);
			commonDao.save(tsTypegroup);
		}
		return tsTypegroup;
	}

	@Transactional(readOnly = true)
	public TSTypegroup getTypeGroupByCode(String typegroupCode) {
		TSTypegroup tsTypegroup = commonDao.findUniqueByProperty(TSTypegroup.class, "typegroupcode", typegroupCode);
		return tsTypegroup;
	}


	@Transactional(readOnly = true)
	public void initAllTypeGroups() {
		List<TSTypegroup> typeGroups = this.commonDao.loadAll(TSTypegroup.class);
		for (TSTypegroup tsTypegroup : typeGroups) {
			ResourceUtil.allTypeGroups.put(tsTypegroup.getTypegroupcode().toLowerCase(), tsTypegroup);
			List<TSType> types = this.commonDao.findByProperty(TSType.class, "TSTypegroup.id", tsTypegroup.getId());
			ResourceUtil.allTypes.put(tsTypegroup.getTypegroupcode().toLowerCase(), types);
		}
	}

	@Transactional(readOnly = true)
	public void refleshTypesCach(TSType type) {
		TSTypegroup tsTypegroup = type.getTSTypegroup();
		TSTypegroup typeGroupEntity = this.commonDao.get(TSTypegroup.class, tsTypegroup.getId());
		List<TSType> types = this.commonDao.findByProperty(TSType.class, "TSTypegroup.id", tsTypegroup.getId());
		ResourceUtil.allTypes.put(typeGroupEntity.getTypegroupcode().toLowerCase(), types);
	}

	@Transactional(readOnly = true)
	public void refleshTypeGroupCach() {
		ResourceUtil.allTypeGroups.clear();
		List<TSTypegroup> typeGroups = this.commonDao.loadAll(TSTypegroup.class);
		for (TSTypegroup tsTypegroup : typeGroups) {
			ResourceUtil.allTypeGroups.put(tsTypegroup.getTypegroupcode().toLowerCase(), tsTypegroup);
		}
	}

	/**
	 * ????????????????????????&????????????
	 */
	@Transactional(readOnly = true)
	public void refreshTypeGroupAndTypes() {
		ResourceUtil.allTypeGroups.clear();
		List<TSTypegroup> typeGroups = this.commonDao.loadAll(TSTypegroup.class);
		for (TSTypegroup tsTypegroup : typeGroups) {
			ResourceUtil.allTypeGroups.put(tsTypegroup.getTypegroupcode().toLowerCase(), tsTypegroup);
			List<TSType> types = this.commonDao.findByProperty(TSType.class, "TSTypegroup.id", tsTypegroup.getId());
			ResourceUtil.allTypes.put(tsTypegroup.getTypegroupcode().toLowerCase(), types);
		}
	}



	/**
	 * ????????????ID ??? ??????Id ?????? ???????????????????????????Codes
	 * @param roleId
	 * @param functionId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<String> getOperationCodesByRoleIdAndFunctionId(String roleId, String functionId) {
		Set<String> operationCodes = new HashSet<String>();
		TSRole role = commonDao.get(TSRole.class, roleId);
		CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
		cq1.eq("TSRole.id", role.getId());
		cq1.eq("TSFunction.id", functionId);
		cq1.add();
		List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
		if (null != rFunctions && rFunctions.size() > 0) {
			TSRoleFunction tsRoleFunction = rFunctions.get(0);
			if (null != tsRoleFunction.getOperation()) {
				String[] operationArry = tsRoleFunction.getOperation().split(",");
				for (int i = 0; i < operationArry.length; i++) {
					operationCodes.add(operationArry[i]);
				}
			}
		}
		return operationCodes;
	}

	/**
	 * ???????????? ?????????-???????????????????????????Code???????????????????????????????????????
	 * ????????????ID ??? ??????Id ?????? ???????????????????????????Codes
	 * @param roleId ??????ID
	 * @param functionId ??????ID
	 * @return
	 */
	@Transactional(readOnly = true)
	public Set<String> getOperationCodesByUserIdAndFunctionId(String userId, String functionId) {
		Set<String> operationCodes = new HashSet<String>();
		List<TSRoleUser> rUsers = findByProperty(TSRoleUser.class, "TSUser.id", userId);
		for (TSRoleUser ru : rUsers) {
			TSRole role = ru.getTSRole();
			CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
			cq1.eq("TSRole.id", role.getId());
			cq1.eq("TSFunction.id", functionId);
			cq1.add();
			List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
			if (null != rFunctions && rFunctions.size() > 0) {
				TSRoleFunction tsRoleFunction = rFunctions.get(0);
				if (null != tsRoleFunction.getOperation()) {
					String[] operationArry = tsRoleFunction.getOperation().split(",");
					for (int i = 0; i < operationArry.length; i++) {
						operationCodes.add(operationArry[i]);
					}
				}
			}
		}
		return operationCodes;
	}

	/**
	 * ???????????? ???????????????????????????????????????button?????????????????????
	 *  @param userId ??????ID
	 *  @param functionId ??????ID
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TSOperation> getOperationsByUserIdAndFunctionId(TSUser currLoginUser, String functionId) {
		String hql="FROM TSOperation where functionid = '"+functionId+"'";
		List<TSOperation> operations = findHql(hql);
		if(operations == null || operations.size()<1){
			return null;
		}
		List<TSRoleUser> rUsers = findByProperty(TSRoleUser.class, "TSUser.id", currLoginUser.getId());
		for(TSRoleUser ru : rUsers){
			TSRole role = ru.getTSRole();
			CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
			cq1.eq("TSRole.id", role.getId());
			cq1.eq("TSFunction.id", functionId);
			cq1.add();
			List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
			if (null != rFunctions && rFunctions.size() > 0) {
				TSRoleFunction tsRoleFunction = rFunctions.get(0);
				if (oConvertUtils.isNotEmpty(tsRoleFunction.getOperation())) {
					String[] operationArry = tsRoleFunction.getOperation().split(",");
					for (int i = 0; i < operationArry.length; i++) {
						for(int j=0;j<operations.size();j++){
							if(operationArry[i].equals(operations.get(j).getId())){
								operations.remove(j);
								break;
							}
						}
						
					}
				}
			}
		}
		return operations;
	}

	/**
	 * ?????????????????????????????????
	 * JS??????
	 * @param out
	 */
	@Transactional(readOnly = true)
	public String getAuthFilterJS() {
		StringBuilder out = new StringBuilder();
		out.append("<script type=\"text/javascript\">");
		out.append("$(document).ready(function(){");

		if(ResourceUtil.getSessionUser().getUserName().equals("admin")|| !Globals.BUTTON_AUTHORITY_CHECK){
			return "";
		}else{
			HttpServletRequest request = ContextHolderUtils.getRequest();
			Set<String> operationCodes = (Set<String>) request.getAttribute(Globals.OPERATIONCODES);
			if (null!=operationCodes) {
				for (String MyoperationCode : operationCodes) {
					if (oConvertUtils.isEmpty(MyoperationCode))
						break;
					TSOperation operation = this.getEntity(TSOperation.class, MyoperationCode);
					if (operation.getOperationcode().startsWith(".") || operation.getOperationcode().startsWith("#")){
						if (operation.getOperationType().intValue()==Globals.OPERATION_TYPE_HIDE){
							//out.append("$(\""+name+"\").find(\"#"+operation.getOperationcode().replaceAll(" ", "")+"\").hide();");
							out.append("$(\""+operation.getOperationcode().replaceAll(" ", "")+"\").hide();");
						}else {
							//out.append("$(\""+name+"\").find(\"#"+operation.getOperationcode().replaceAll(" ", "")+"\").find(\":input\").attr(\"disabled\",\"disabled\");");
							out.append("$(\""+operation.getOperationcode().replaceAll(" ", "")+"\").attr(\"disabled\",\"disabled\");");
							out.append("$(\""+operation.getOperationcode().replaceAll(" ", "")+"\").find(\":input\").attr(\"disabled\",\"disabled\");");
						}
					}
				}
			}else{
				return "";
			}
			
		}

		out.append("});");
		out.append("</script>");
		return out.toString();
	}

	
	@Transactional(readOnly = true)
	public void flushRoleFunciton(String id, TSFunction newFunction) {
		TSFunction functionEntity = this.getEntity(TSFunction.class, id);
		if (functionEntity.getTSIcon() == null || !StringUtil.isNotEmpty(functionEntity.getTSIcon().getId())) {
			return;
		}
		TSIcon oldIcon = this.getEntity(TSIcon.class, functionEntity.getTSIcon().getId());
		if (!oldIcon.getIconClas().equals(newFunction.getTSIcon().getIconClas())) {
			// ????????????
			HttpSession session = ContextHolderUtils.getSession();
			TSUser user = ResourceUtil.getSessionUser();
			List<TSRoleUser> rUsers = this.findByProperty(TSRoleUser.class, "TSUser.id", user.getId());
			for (TSRoleUser ru : rUsers) {
				TSRole role = ru.getTSRole();
				session.removeAttribute(role.getId());
			}
		}
	}
	
	@Transactional(readOnly = true)
    public String generateOrgCode(String id, String pid) {

        int orgCodeLength = 2; // ??????????????????
        if ("3".equals(ResourceUtil.getOrgCodeLengthType())) { // ??????2-???????????????3??????001
            orgCodeLength = 3;
        }


        String  newOrgCode = "";
        if(!StringUtils.hasText(pid)) { // ???????????????
            String sql = "select max(t.org_code) orgCode from t_s_depart t where t.parentdepartid is null";
            Map<String, Object> pOrgCodeMap = commonDao.findOneForJdbc(sql);
            if(pOrgCodeMap.get("orgCode") != null) {
                String curOrgCode = pOrgCodeMap.get("orgCode").toString();
                newOrgCode = String.format("%0" + orgCodeLength + "d", Integer.valueOf(curOrgCode) + 1);
            } else {
                newOrgCode = String.format("%0" + orgCodeLength + "d", 1);
            }
        } else { // ????????????
            String sql = "select max(t.org_code) orgCode from t_s_depart t where t.parentdepartid = ?";
            Map<String, Object> orgCodeMap = commonDao.findOneForJdbc(sql, pid);
            if(orgCodeMap.get("orgCode") != null) { // ????????????????????????
                String curOrgCode = orgCodeMap.get("orgCode").toString();
                String pOrgCode = curOrgCode.substring(0, curOrgCode.length() - orgCodeLength);
                String subOrgCode = curOrgCode.substring(curOrgCode.length() - orgCodeLength, curOrgCode.length());
                newOrgCode = pOrgCode + String.format("%0" + orgCodeLength + "d", Integer.valueOf(subOrgCode) + 1);
            } else { // ???????????????????????????
                String pOrgCodeSql = "select max(t.org_code) orgCode from t_s_depart t where t.id = ?";
                Map<String, Object> pOrgCodeMap = commonDao.findOneForJdbc(pOrgCodeSql, pid);
                String curOrgCode = pOrgCodeMap.get("orgCode").toString();
                newOrgCode = curOrgCode + String.format("%0" + orgCodeLength + "d", 1);
            }
        }

        return newOrgCode;
    }

	@Transactional(readOnly = true)
	public Set<String> getOperationCodesByRoleIdAndruleDataId(String roleId,
			String functionId) {
		Set<String> operationCodes = new HashSet<String>();
		TSRole role = commonDao.get(TSRole.class, roleId);
		CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
		cq1.eq("TSRole.id", role.getId());
		cq1.eq("TSFunction.id", functionId);
		cq1.add();
		List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
		if (null != rFunctions && rFunctions.size() > 0) {
			TSRoleFunction tsRoleFunction = rFunctions.get(0);
			if (null != tsRoleFunction.getDataRule()) {
				String[] operationArry = tsRoleFunction.getDataRule().split(",");
				for (int i = 0; i < operationArry.length; i++) {
					operationCodes.add(operationArry[i]);
				}
			}
		}
		return operationCodes;
	}

	@Transactional(readOnly = true)
	public Set<String> getOperationCodesByUserIdAndDataId(TSUser currLoginUser,
			String functionId) {
		// TODO Auto-generated method stub
		Set<String> dataRulecodes = new HashSet<String>();
		List<TSRoleUser> rUsers = findByProperty(TSRoleUser.class, "TSUser.id", currLoginUser.getId());
		for (TSRoleUser ru : rUsers) {
			TSRole role = ru.getTSRole();
			CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
			cq1.eq("TSRole.id", role.getId());
			cq1.eq("TSFunction.id", functionId);
			cq1.add();
			List<TSRoleFunction> rFunctions = getListByCriteriaQuery(cq1, false);
			if (null != rFunctions && rFunctions.size() > 0) {
				TSRoleFunction tsRoleFunction = rFunctions.get(0);
				if (oConvertUtils.isNotEmpty(tsRoleFunction.getDataRule())) {
					String[] operationArry = tsRoleFunction.getDataRule().split(",");
					for (int i = 0; i < operationArry.length; i++) {
						dataRulecodes.add(operationArry[i]);
					}
				}
			}
		}
		return dataRulecodes;
	}
	/**
	 * ??????????????????
	 * @return
	 */
	@Transactional(readOnly = true)
	public  void initAllTSIcons() {
		List<TSIcon> list = this.loadAll(TSIcon.class);
		for (TSIcon tsIcon : list) {
			ResourceUtil.allTSIcons.put(tsIcon.getId(), tsIcon);
		}
	}
	/**
	 * ????????????
	 * @param icon
	 */
	public void upTSIcons(TSIcon icon) {
		ResourceUtil.allTSIcons.put(icon.getId(), icon);
	}
	/**
	 * ????????????
	 * @param icon
	 */
	public void delTSIcons(TSIcon icon) {
		ResourceUtil.allTSIcons.remove(icon.getId());
	}

	@Override
	public void addDataLog(String tableName, String dataId, String dataContent) {

		int versionNumber = 0;

		Integer integer = commonDao.singleResult("select max(versionNumber) from TSDatalogEntity where tableName = '" + tableName + "' and dataId = '" + dataId + "'");
		if (integer != null) {
			versionNumber = integer.intValue();
		}

		TSDatalogEntity tsDatalogEntity = new TSDatalogEntity();
		tsDatalogEntity.setTableName(tableName);
		tsDatalogEntity.setDataId(dataId);
		tsDatalogEntity.setDataContent(dataContent);
		tsDatalogEntity.setVersionNumber(versionNumber + 1);
		commonDao.save(tsDatalogEntity);
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????????????????????????????
	 * @param groupId ???????????????ID
	 * @param functionId ????????????ID
	 * @Param type 0:??????????????????/1:????????????
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getDepartAuthGroupOperationSet(String groupId,String functionId,String type) {
		Set<String> operationCodes = new HashSet<String>();
		TSDepartAuthGroupEntity functionGroup = null;
		if(OrgConstants.GROUP_DEPART_ROLE.equals(type)) {
			TSRole role = commonDao.get(TSRole.class, groupId);
			CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
			cq1.eq("TSRole.id", role.getId());
			cq1.eq("TSFunction.id", functionId);
			cq1.add();
			List<TSRoleFunction> functionGroups = getListByCriteriaQuery(cq1, false);
			if (null != functionGroups && functionGroups.size() > 0) {
				TSRoleFunction tsFunctionGroup = functionGroups.get(0);
				if (null != tsFunctionGroup.getOperation()) {
					String[] operationArry = tsFunctionGroup.getOperation().split(",");
					for (int i = 0; i < operationArry.length; i++) {
						operationCodes.add(operationArry[i]);
					}
				}
			}
		} else {
			functionGroup = commonDao.get(TSDepartAuthGroupEntity.class, groupId);
			CriteriaQuery cq1 = new CriteriaQuery(TSDepartAuthgFunctionRelEntity.class);
			cq1.eq("tsDepartAuthGroup.id", functionGroup.getId());
			cq1.eq("tsFunction.id", functionId);
			cq1.add();
			List<TSDepartAuthgFunctionRelEntity> functionGroups = getListByCriteriaQuery(cq1, false);
			if (null != functionGroups && functionGroups.size() > 0) {
				TSDepartAuthgFunctionRelEntity tsFunctionGroup = functionGroups.get(0);
				if (null != tsFunctionGroup.getOperation()) {
					String[] operationArry = tsFunctionGroup.getOperation().split(",");
					for (int i = 0; i < operationArry.length; i++) {
						operationCodes.add(operationArry[i]);
					}
				}
			}
		}
		return operationCodes;
	}

	/**
	 * ??????????????????????????????????????????????????????????????????????????????????????????
	 * @param groupId ???????????????ID
	 * @param functionId ????????????ID
	 * @Param type  0:??????????????????/1:????????????
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<String> getDepartAuthGroupDataRuleSet(String groupId, String functionId,String type) {
		Set<String> dataRuleCodes = new HashSet<String>();
		TSDepartAuthGroupEntity functionGroup = null;
		if(OrgConstants.GROUP_DEPART_ROLE.equals(type)) {
			TSRole role = commonDao.get(TSRole.class, groupId);
			CriteriaQuery cq1 = new CriteriaQuery(TSRoleFunction.class);
			cq1.eq("TSRole.id", role.getId());
			cq1.eq("TSFunction.id", functionId);
			cq1.add();
			List<TSRoleFunction> functionGroups = getListByCriteriaQuery(cq1, false);
			if (null != functionGroups && functionGroups.size() > 0) {
				TSRoleFunction tsFunctionGroup = functionGroups.get(0);
				if (null != tsFunctionGroup.getDataRule()) {
					String[] dataRuleArry = tsFunctionGroup.getDataRule().split(",");
					for (int i = 0; i < dataRuleArry.length; i++) {
						dataRuleCodes.add(dataRuleArry[i]);
					}
				}
			}
		} else {
			functionGroup = commonDao.get(TSDepartAuthGroupEntity.class, groupId);
			CriteriaQuery cq1 = new CriteriaQuery(TSDepartAuthgFunctionRelEntity.class);
			cq1.eq("tsDepartAuthGroup.id", functionGroup.getId());
			cq1.eq("tsFunction.id", functionId);
			cq1.add();
			List<TSDepartAuthgFunctionRelEntity> functionGroups = getListByCriteriaQuery(cq1, false);
			if (null != functionGroups && functionGroups.size() > 0) {
				TSDepartAuthgFunctionRelEntity tsFunctionGroup = functionGroups.get(0);
				if (null != tsFunctionGroup.getDatarule()) {
					String[] dataRuleArry = tsFunctionGroup.getDatarule().split(",");
					for (int i = 0; i < dataRuleArry.length; i++) {
						dataRuleCodes.add(dataRuleArry[i]);
					}
				}
			}
		}
		return dataRuleCodes;
	}

}
