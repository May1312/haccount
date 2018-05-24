package com.fengniao.data.center.controller;

import org.jeecgframework.core.enums.SysThemesEnum;
import org.jeecgframework.core.online.def.CgReportConstant;
import org.jeecgframework.core.online.exception.CgReportNotFoundException;
import org.jeecgframework.core.online.util.FreemarkerHelper;
import org.jeecgframework.core.util.ContextHolderUtils;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.core.util.SysThemesUtil;
import org.jeecgframework.web.graphreport.service.core.GraphReportServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/dataCenterController")
public class DataCenterController {

    @Autowired
    private GraphReportServiceI graphReportService;

    @Autowired
    private SystemService systemService;

    /**
     * 动态报表展现入口
     * @param request
     * @param response
     */
    @RequestMapping(params = "getUsersCount")
    public void getUsersCount(HttpServletRequest request,
                     HttpServletResponse response) {
        //step.1 根据id获取该动态报表的配置参数
        Map<String, Object> cgReportMap = null;
        String id="yhcztj";
        try{
            cgReportMap = graphReportService.queryCgReportConfig(id);
        }catch (Exception e) {
            throw new CgReportNotFoundException("动态报表配置不存在!");
        }
        //step.2 获取列表ftl模板路径
        FreemarkerHelper viewEngine = new FreemarkerHelper();
        //step.3 组合模板+数据参数，进行页面展现
        loadVars(cgReportMap);

        //step.4 页面css js引用
        cgReportMap.put(CgReportConstant.CONFIG_IFRAME, getHtmlHead(request));

        String html = viewEngine.parseTemplate("/org/jeecgframework/web/graphreport/engine/core/graphreportlist2.ftl", cgReportMap);
        PrintWriter writer = null;
        try {
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-store");
            writer = response.getWriter();
            writer.println(html);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
            } catch (Exception e2) {
                // TODO: handle exception
            }
        }
    }

    /**
     * 组装模版参数
     * @param cgReportMap
     */
    @SuppressWarnings("unchecked")
    private void loadVars(Map<String, Object> cgReportMap) {
        Map mainM = (Map) cgReportMap.get(CgReportConstant.MAIN);
        List<Map<String,Object>> fieldList = (List<Map<String, Object>>) cgReportMap.get(CgReportConstant.ITEMS);
        List<Map<String,Object>> queryList = new ArrayList<Map<String,Object>>(0);
        //图表数据
        List<Map<String,Object>> graphList = new ArrayList<Map<String,Object>>(0);
        //tab数据
        Set<String> tabSet = new HashSet<String>();
        List<String> tabList = new ArrayList<String>();

        for(Map<String,Object> fl:fieldList){
            fl.put(CgReportConstant.ITEM_FIELDNAME, ((String)fl.get(CgReportConstant.ITEM_FIELDNAME)).toLowerCase());
            String isQuery = (String) fl.get(CgReportConstant.ITEM_ISQUERY);
            if(CgReportConstant.BOOL_TRUE.equalsIgnoreCase(isQuery)){
                loadDic(fl,fl);
                queryList.add(fl);
            }
            if("y".equals(fl.get("is_graph")) || "Y".equals(fl.get("is_graph"))) {
                graphList.add(fl);
                String tabName = (fl.get("tab_name") == null ? "" : fl.get("tab_name").toString());
                if(!tabSet.contains(tabName)) {
                    tabList.add(tabName);
                    tabSet.add(tabName);
                }
            }
        }
        cgReportMap.put(CgReportConstant.CONFIG_ID, mainM.get("code"));
        cgReportMap.put(CgReportConstant.CONFIG_NAME, mainM.get("name"));
        cgReportMap.put(CgReportConstant.CONFIG_FIELDLIST, fieldList);
        cgReportMap.put(CgReportConstant.CONFIG_QUERYLIST, queryList);
        cgReportMap.put("graphList", graphList);
        cgReportMap.put("tabList", tabList);
    }

    private String getHtmlHead(HttpServletRequest request){
        HttpSession session = ContextHolderUtils.getSession();
        String lang = (String)session.getAttribute("lang");
        StringBuilder sb= new StringBuilder("");

        sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>");

        SysThemesEnum sysThemesEnum = SysThemesUtil.getSysTheme(request);
        sb.append(SysThemesUtil.getReportTheme(sysThemesEnum));
        sb.append(SysThemesUtil.getCommonTheme(sysThemesEnum));
        sb.append("<script type=\"text/javascript\" src=\"plug-in/jquery/jquery-1.8.3.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/tools/dataformat.js\"></script>");
        sb.append(SysThemesUtil.getEasyUiTheme(sysThemesEnum));
        sb.append("<link rel=\"stylesheet\" href=\"plug-in/easyui/themes/icon.css\" type=\"text/css\"></link>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"plug-in/accordion/css/accordion.css\">");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"plug-in/accordion/css/icons.css\">");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/easyui/jquery.easyui.min.1.3.2.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/easyui/locale/zh-cn.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/tools/syUtil.js\"></script>");
        sb.append(SysThemesUtil.getLhgdialogTheme(sysThemesEnum));

        sb.append("<script type=\"text/javascript\" src=\"plug-in/layer/layer.js\"></script>");

        sb.append(StringUtil.replace("<script type=\"text/javascript\" src=\"plug-in/tools/curdtools_{0}.js\"></script>", "{0}", lang));
        sb.append("<script type=\"text/javascript\" src=\"plug-in/tools/easyuiextend.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/easyui/extends/datagrid-scrollview.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/My97DatePicker/WdatePicker.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/graphreport/highcharts3.0.6.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/graphreport/spin.min.js\"></script>");
        sb.append("<script type=\"text/javascript\" src=\"plug-in/graphreport/report.js\"></script>");
        return sb.toString();
    }


    @SuppressWarnings("unchecked")
    private void loadDic(Map m, Map<String, Object> cgReportMap) {
        String dict_code = (String) cgReportMap.get("dict_code");
        if(StringUtil.isEmpty(dict_code)){
            m.put(CgReportConstant.FIELD_DICTLIST, new ArrayList(0));
            return;
        }
        List<Map<String, Object>> dicDatas = queryDicBySQL(dict_code);
        m.put(CgReportConstant.FIELD_DICTLIST, dicDatas);
    }

    /**
     * 查询数据字典，扩展了对SQL的支持
     * @param dictCodeOrSQL 字典编码或SQL
     * @author bit 2014-4-19
     */
    private List<Map<String, Object>> queryDicBySQL(String dictCodeOrSQL) {
        List<Map<String, Object>> dicDatas = null;
        dictCodeOrSQL = dictCodeOrSQL.trim();
        if(dictCodeOrSQL.toLowerCase().startsWith("select ")) {

            dictCodeOrSQL = dictCodeOrSQL.replaceAll("'[kK][eE][yY]'", "typecode").replaceAll("'[vV][aA][lL][uU][eE]'", "typename");

            dicDatas = systemService.findForJdbc(dictCodeOrSQL, null);
        }else {
            dicDatas = queryDic(dictCodeOrSQL);
        }
        return dicDatas;
    }

    /**
     * 查询数据字典
     * @param diccode 字典编码
     * @return
     */
    private List<Map<String, Object>> queryDic(String diccode) {
        StringBuilder dicSql = new StringBuilder();
        dicSql.append(" SELECT TYPECODE,TYPENAME FROM");
        dicSql.append(" "+CgReportConstant.SYS_DIC);
        dicSql.append(" "+"WHERE TYPEGROUPID = ");
        dicSql.append(" "+"(SELECT ID FROM "+CgReportConstant.SYS_DICGROUP+" WHERE TYPEGROUPCODE = '"+diccode+"' )");
        List<Map<String, Object>> dicDatas = graphReportService.findForJdbc(dicSql.toString());
        return dicDatas;
    }
}
