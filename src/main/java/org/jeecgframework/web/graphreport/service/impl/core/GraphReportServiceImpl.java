package org.jeecgframework.web.graphreport.service.impl.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jeecgframework.core.common.dao.jdbc.JdbcDao;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.online.def.CgReportConstant;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.oConvertUtils;
import org.jeecgframework.web.graphreport.service.core.GraphReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value="graphReportService")
@Transactional
public class GraphReportServiceImpl extends CommonServiceImpl implements
		GraphReportServiceI {
	private Logger log = Logger.getLogger(GraphReportServiceImpl.class);
	@Autowired
	private JdbcDao jdbcDao;

//	@Autowired
//	private CgReportDao cgReportDao;

	
	
	public Map<String, Object> queryCgReportConfig(String reportId) {
		Map<String,Object> cgReportM = new HashMap<String, Object>(0);
		Map<String,Object> mainM = jdbcDao.findForJdbc("SELECT * from jform_graphreport_head where code=?", new Object[]{reportId}).get(0);
		List<Map<String,Object>> itemsM = jdbcDao.findForJdbc("SELECT * from jform_graphreport_item where cgreport_head_id=? order by order_num asc", new Object[]{mainM.get("id")});
		cgReportM.put(CgReportConstant.MAIN, mainM);
		cgReportM.put(CgReportConstant.ITEMS, itemsM);
		return cgReportM;
	}

//	public Map<String,Object> queryCgReportMainConfig(String reportId){
////		String sql = JeecgSqlUtil.getMethodSql(JeecgSqlUtil.getMethodUrl());
////		Map<String,Object> parameters = new LinkedHashMap<String,Object>();
////		parameters.put("id", reportId);
////		Map mainM = jdbcDao.findForMap(sql, parameters);
//		
//		//??????MiniDao????????????
//		return cgReportDao.queryCgReportMainConfig(reportId);
//	}
//	
//	public List<Map<String,Object>> queryCgReportItems(String reportId){
////		String sql = JeecgSqlUtil.getMethodSql(JeecgSqlUtil.getMethodUrl());
////		Map<String,Object> parameters = new LinkedHashMap<String,Object>();
////		parameters.put("configId", reportId);
////		List<Map<String,Object>> items = jdbcDao.findForListMap(sql, parameters);
//		
//		//??????MiniDao????????????
//		return cgReportDao.queryCgReportItems(reportId);
//	}

	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryByCgReportSql(String sql, Map params,
			int page, int rows) {
		sql = handleElInSQL(sql, params);
		String querySql = getFullSql(sql,params);
		log.info("-------------??????????????????--------querySql--" + querySql);
		List<Map<String,Object>> result = null;
		if(page==-1 && rows==-1){
			result = jdbcDao.findForJdbc(querySql);
		}else{
			result = jdbcDao.findForJdbc(querySql, page, rows);
		}
		return result;
	}
	
	/**
	 * ??????SQL???{}???????????????
	 */
	public String handleElInSQL(String sql, Map params) {
		Pattern p = Pattern.compile("\\{[^}]+\\}");
		Matcher m = p.matcher(sql);
		//???????????????????????????????????????
		while (m.find()) {
			String oel = m.group();
			String el = oel.replace("{", "").replace("}", "").trim();
			//??????{xx:xx}??????
			if(el.indexOf(":") != -1) {
				String[] elSplit = el.split(":");
				String elKey = elSplit[0].trim();
				String elValue = elSplit[1].trim();
				//???????????????????????????????????????
				Object condValue = params.get(elSplit[1].trim()); 
				if(condValue != null) {
					sql = sql.replace(oel, elKey + condValue.toString().replace(" " + elValue + " ", " " + elKey + " "));
				}else {
					sql = sql.replace(oel, "1=1");
				}
				params.remove(elValue);
			}else {
				//??????{xx}??????
				Object condValue = params.get(el); 
				if(condValue != null) {
					sql = sql.replace(oel, el + condValue.toString());
				}else {
					sql = sql.replace(oel, "1=1");
				}
				params.remove(el);
			}
		}
		return sql;
	}
	
	/**
	 * ?????????????????????????????????sql
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getFullSql(String sql,Map params){
		//??????sql??????order by???????????????????????????SQL??????
		String orderBy = "";
		Pattern p = Pattern.compile("order +by.*",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		if(m.find()) {
			orderBy = " " + m.group();
			sql = sql.replace(orderBy, "");
		}
		
		StringBuilder sqlB =  new StringBuilder();
		sqlB.append("SELECT t.* FROM ( ");
		sqlB.append(sql+" ");
		sqlB.append(") t ");
		if (params.size() >= 1) {
			sqlB.append("WHERE 1=1  ");
			Iterator it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = String.valueOf(it.next());
				String value = String.valueOf(params.get(key));
				if (!StringUtil.isEmpty(value) && !"null".equals(value)) {
						sqlB.append(" AND ");
						sqlB.append(" " + key +  value );
				}
			}
		}
		//order by?????????SQL??????
		sqlB.append(orderBy);
		return sqlB.toString();
	}
	
	@SuppressWarnings("unchecked")
	public long countQueryByCgReportSql(String sql, Map params) {
		String querySql = getFullSql(sql,params);
		querySql = "SELECT COUNT(*) FROM ("+querySql+") t2";
		long result = jdbcDao.findForLong(querySql,new HashMap(0));
		return result;
	}
	
	@SuppressWarnings( "unchecked" )
	public List<String> getSqlFields(String sql) {
		if(oConvertUtils.isEmpty(sql)){
			return null;
		}
		log.info("-------------??????????????????--------getSqlFields--" + sql);
		List<Map<String, Object>> result = jdbcDao.findForJdbc(sql, 1, 1);
		if(result.size()<1){
			throw new BusinessException("?????????sql????????????");
		}
		Set fieldsSet= result.get(0).keySet();
		List<String> fileds = new ArrayList<String>(fieldsSet);
		return fileds;
	}
}
