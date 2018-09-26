package com.fnjz.back.controller.user;

import com.fnjz.back.entity.user.ChannelBehaviorEntity;
import com.fnjz.back.service.user.DataCenterServiceI;
import com.fnjz.back.service.user.UserInfoServiceI;
import com.fnjz.front.utils.ShareCodeUtil;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.util.DateUtils;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping("/dataCenterController")
public class DataCenterController {
    @Autowired
    private UserInfoServiceI userInfoService;

    @Autowired
    private DataCenterServiceI dataCenterService;

    private SystemService systemService;

    /**
     * 用户信息列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {
        String startDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        if (StringUtil.isEmpty(startDate) && StringUtil.isEmpty(endDate)) {
            startDate = "2018-01-01";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            endDate = DateUtils.getDate("yyyy-MM-dd");
        } else {
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
        }
        HashMap<String, Object> map = userInfoService.attributeStatistics(startDate, endDate);
        request.setAttribute("map", map);


        return new ModelAndView("com/fnjz/back/user/userDataCenter");
    }

    @RequestMapping(params = "channelBehaviorlist")
    public ModelAndView channelBehaviorlist(HttpServletRequest request) {
        String sql = "SELECT DISTINCT android_channel FROM hbird_user_info where android_channel IS NOT NULL ;";
        List<String> channellistbySql = dataCenterService.findListbySql(sql);
        channellistbySql.add("ios");
        request.setAttribute("channelList",channellistbySql);
        return new ModelAndView("com/fnjz/back/user/channelBehaviorList");
    }
    /**
     * 功能描述:
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/9/19 14:17
     * @since  JDK1.8
     */
    @RequestMapping(params = "datagrid")

    public void datagrid(ChannelBehaviorEntity channelBehaviorEntity, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        String fengniaoId = request.getParameter("fengniaoId");
        String userId = "";
        //转换查询条件
        if (StringUtil.isNotEmpty(fengniaoId)){
            userId=String.valueOf(ShareCodeUtil.sharecode2id(fengniaoId));
        }
        String downloadChannel = request.getParameter("downloadChannel");
        String registerstartDate = request.getParameter("registerDate_begin");
        String registerendDate = request.getParameter("registerDate_end");
        if (StringUtil.isNotEmpty(downloadChannel)){
            if (downloadChannel.equals("all")){
                downloadChannel="全部";
            }else if (downloadChannel.equals("xiaochengxu")){
                downloadChannel="小程序";
            }
        }
        int pageSize=1;
        int rows = 100;
        //排序
        String sort = dataGrid.getSort();
        String order = dataGrid.getOrder();
        if (StringUtil.isNotEmpty(request.getParameter("page")) && StringUtil.isNotEmpty(request.getParameter("rows"))){
             pageSize = Integer.parseInt(request.getParameter("page"));
             rows = Integer.parseInt(request.getParameter("rows"));
        }
        List<ChannelBehaviorEntity> list =  dataCenterService.queryListByPage(pageSize,rows,registerstartDate,registerendDate,userId,downloadChannel, sort, order);

        //总记录条数
        dataGrid.setResults(list);
        dataGrid.setTotal(dataCenterService.getCount(registerstartDate,registerendDate,userId,downloadChannel).intValue());
        TagUtil.datagrid(response, dataGrid);
    }


    /**
     * 导出excel

     */
    @RequestMapping(params = "exportXls")
    public String exportXls(ChannelBehaviorEntity channelBehaviorEntity, HttpServletRequest request, HttpServletResponse response
            , DataGrid dataGrid, ModelMap modelMap) {
        String userId = request.getParameter("userId");
        String downloadChannel = request.getParameter("downloadChannel");
        String registerstartDate = request.getParameter("registerDate_begin");
        String registerendDate = request.getParameter("registerDate_end");
        System.out.println("download--------------====================-----------------"+downloadChannel);
        if (StringUtil.isNotEmpty(downloadChannel)){
            if (downloadChannel.equals("all")){
                downloadChannel="全部";
            }else if (downloadChannel.equals("xiaochengxu")){
                downloadChannel="小程序";
            }
        }
        int pageSize=1;
        int rows = 100;
        if (StringUtil.isNotEmpty(request.getParameter("page")) && StringUtil.isNotEmpty(request.getParameter("rows"))){
            pageSize = Integer.parseInt(request.getParameter("page"));
            rows = Integer.parseInt(request.getParameter("rows"));
        }

        List<ChannelBehaviorEntity> list =  dataCenterService.queryListByPage(pageSize,rows,registerstartDate,registerendDate,userId,downloadChannel,null,null);

        modelMap.put(NormalExcelConstants.FILE_NAME,"用户行为统计表");
        modelMap.put(NormalExcelConstants.CLASS,ChannelBehaviorEntity.class);
        modelMap.put(NormalExcelConstants.PARAMS,new ExportParams("用户行为统计列表", "导出人:"+ResourceUtil.getSessionUser().getRealName(),
                "导出信息"));
        modelMap.put(NormalExcelConstants.DATA_LIST,list);
        return NormalExcelConstants.JEECG_EXCEL_VIEW;
    }




}
