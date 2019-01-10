package com.fnjz.front.controller.api.userinvite;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.api.userinvite.UserInviteRestServiceI;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 邀请好友
 * Created by yhang on 2018/10/17.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserInviteRestController {

    private static final Logger logger = Logger.getLogger(UserInviteRestController.class);

    @Autowired
    private UserInviteRestServiceI userInviteRestServiceI;

    /**
     * 获取历史邀请好友
     * @param request
     * @return
     */
    @RequestMapping(value = {"/historyInviteUsers", "/historyInviteUsers/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean historyIntegral(HttpServletRequest request, @RequestParam(value="curPage",required = false) Integer curPage, @RequestParam(value="pageSize",required = false) Integer pageSize) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            return new ResultBean(ApiResultType.OK,userInviteRestServiceI.listForPage(userInfoId, curPage, pageSize));
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取历史邀请好友  加入返回积分数
     * @param request
     * @return
     */
    @RequestMapping(value = {"/historyInviteUsersv2", "/historyInviteUsersv2/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean historyInviteUsersv2(HttpServletRequest request, @RequestParam(value="curPage",required = false) Integer curPage, @RequestParam(value="pageSize",required = false) Integer pageSize) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            return new ResultBean(ApiResultType.OK,userInviteRestServiceI.listForPagev2(userInfoId, curPage, pageSize));
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }
}
