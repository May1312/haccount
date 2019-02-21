package com.fnjz.front.controller.api.userbadge;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param btId    徽章类型id
     * @param request
     * @return
     */
    @RequestMapping(value = {"/myBadges/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean myBadges(@PathVariable("type") String type, HttpServletRequest request, @RequestParam(required = false) Integer btId) {
        logger.info("访问终端:" + type);
        String userInfoId = (String) request.getAttribute("userInfoId");
        //定义 1 已登录   2 未登录
        int status = 1;
        if(request.getAttribute("containsToken")!=null){
            status=2;
        }
        if (btId == null) {
            try {
                List<UserBadgeRestDTO> list = userBadgeRestService.getMyBadges(userInfoId,status);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else {
            List<UserBadgeInfoRestDTO> list = userBadgeRestService.getMyBadgeInfo(userInfoId,btId);
            return new ResultBean(ApiResultType.OK, list);
        }
    }


    @RequestMapping(value = {"/myBadges"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean myBadges(HttpServletRequest request, @RequestParam Integer btId) {
        return this.myBadges(null, request, btId);
    }
}
