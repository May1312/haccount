package com.fnjz.back.controller.user;

import com.fnjz.back.service.user.UserInfoServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
        String startDate = request.getParameter("startDate");
        request.getParameter("endDate");
        HashMap<String, Object> map = userInfoService.attributeStatistics("2018-06-01", "2018-06-09");
        request.setAttribute("map",map);
        return new ModelAndView("com/fnjz/back/user/userDataCenter");
    }

   
}
