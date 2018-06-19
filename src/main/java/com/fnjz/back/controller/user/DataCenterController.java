package com.fnjz.back.controller.user;

import com.fnjz.back.service.user.UserInfoServiceI;
import org.jeecgframework.core.util.DateUtils;
import org.jeecgframework.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;


@Controller
@RequestMapping("/dataCenterController")
public class DataCenterController {
    @Autowired
    private UserInfoServiceI userInfoService;

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


}
