package com.fnjz.front.controller.api.userbadge;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户-徽章完成情况
 * Created by yhang on 2018/12/17.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserBadgeRestController {

    private static final Logger logger = Logger.getLogger(UserBadgeRestController.class);

    @Autowired
    private UserBadgeRestService userBadgeRestService;

    /**
     * 获取我的------>徽章获取情况
     * @param request
     * @return
     */
    @RequestMapping(value = {"/myBadges/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean myBadges(@PathVariable("type")String type, HttpServletRequest request) {
        logger.info("访问终端:"+type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        List<UserBadgeRestDTO> list = userBadgeRestService.getMyBadges(userInfoId);
        return null;
    }

    @RequestMapping(value = {"/myBadges"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean myBadges(HttpServletRequest request) {
        return this.myBadges(null,request);
    }
}
