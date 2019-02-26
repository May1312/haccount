package com.fnjz.front.controller.api.userbadge;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoCheckRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import com.fnjz.front.service.api.userbadge.UserBadgeRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.ShareCodeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 获取我的页面------>徽章获取情况
     * 获取我的------>指定徽章类型获取情况
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
        if (request.getAttribute("containsToken") != null) {
            status = 2;
        }
        if (btId == null) {
            try {
                List<UserBadgeRestDTO> list = userBadgeRestService.getMyBadges(userInfoId, status);
                return new ResultBean(ApiResultType.OK, list);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        } else {
            List<UserBadgeInfoRestDTO> list = userBadgeRestService.getMyBadgeInfo(userInfoId, btId,null);
            return new ResultBean(ApiResultType.OK, list);
        }
    }

    /**
     * 检查用户是否存在未读解锁徽章
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = {"/checkBadges/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkBadges(@PathVariable("type") String type, HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = ShareCodeUtil.id2sharecode(Integer.parseInt(userInfoId));
        try {
            Long size = redisTemplateUtils.getSize(RedisPrefix.PREFIX_USER_NEW_UNLOCK_BADGE + shareCode);
            List<UserBadgeInfoCheckRestDTO> list = new ArrayList<>(Integer.parseInt(size+""));
            if(size>0){
                for(int i = 0 ; i<size;i++){
                    String string =(String) redisTemplateUtils.popListRight(RedisPrefix.PREFIX_USER_NEW_UNLOCK_BADGE + shareCode);
                    list.add(JSONObject.parseObject(string,UserBadgeInfoCheckRestDTO.class));
                }
            }
            return new ResultBean(ApiResultType.OK, list);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = {"/myBadges"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean myBadges(HttpServletRequest request, @RequestParam Integer btId) {
        return this.myBadges(null, request, btId);
    }

    @RequestMapping(value = {"/checkBadges"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkBadges(HttpServletRequest request) {
        return this.checkBadges(null, request);
    }
}
