package com.fnjz.front.controller.api.userintegral;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.utils.CreateTokenUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 用户积分流水表相关
 * @date 2018-10-12 11:31:58
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserIntegralRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserIntegralRestController.class);

    @Autowired
    private UserIntegralRestServiceI userIntegralRestServiceI;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private CreateTokenUtils createTokenUtils;

    /**
     * 领取签到积分接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/signInIntegral", "/signInIntegral/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean signInIntegral(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        //类型不为null情况下
        if (map.size() > 0) {
            try {
                userIntegralRestServiceI.signInIntegral(userInfoId, shareCode, map);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return new ResultBean(ApiResultType.OK, null);
    }

    /**
     * 获取积分历史记录列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/historyIntegral", "/historyIntegral/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean historyIntegral(HttpServletRequest request, @RequestParam(value="curPage",required = false) Integer curPage, @RequestParam(value="pageSize",required = false) Integer pageSize, @RequestParam(value="type",required = false) Integer type) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            PageRest page = userIntegralRestServiceI.listForPage(userInfoId, curPage, pageSize,type);
            return new ResultBean(ApiResultType.OK,page);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取今日任务/新手任务完成情况
     * @param request
     * @return
     */
    @RequestMapping(value = {"/integralTask", "/integralTask/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean integralTask(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        try {
            JSONObject jsonObject = userIntegralRestServiceI.integralTask2(userInfoId,shareCode);
            return new ResultBean(ApiResultType.OK,jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取积分排行榜
     * @param request
     * @return
     */
    @RequestMapping(value = {"/integralTop", "/integralTop/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean integralTop(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            JSONObject jsonObject = userIntegralRestServiceI.integralTop(userInfoId);
            return new ResultBean(ApiResultType.OK,jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取积分排行榜
     * @param request
     * @return
     */
    @RequestMapping(value = {"/integralTop10", "/integralTop10/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean integralTop10(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            JSONObject jsonObject = userIntegralRestServiceI.integralTop10(userInfoId);
            return new ResultBean(ApiResultType.OK,jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 小程序添加到我的小程序
     * @param request
     * @return
     */
    @RequestMapping(value = {"/add2wxapplet"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean add2wxapplet(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        taskExecutor.execute(()->{
            createTokenUtils.integralTask(userInfoId,null, CategoryOfBehaviorEnum.NewbieTask,AcquisitionModeEnum.Add_to_my_applet);
        });
        return new ResultBean(ApiResultType.OK,null);
    }
}
