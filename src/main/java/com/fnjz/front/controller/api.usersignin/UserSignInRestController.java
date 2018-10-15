package com.fnjz.front.controller.api.usersignin;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.usersignin.UserSignInRestServiceI;
import com.fnjz.front.utils.ParamValidateUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.*;
import java.util.Date;
import java.util.Map;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 用户签到表相关
 * @date 2018-10-10 14:23:20
 */
@RestController
@RequestMapping(RedisPrefix.BASE_URL)
public class UserSignInRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserSignInRestController.class);

    @Autowired
    private UserSignInRestServiceI userSignInRestServiceI;

    /**
     * 签到
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/signIn", "/signIn/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toSignIn(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        try {
            Integer integer = userSignInRestServiceI.signIn(userInfoId, shareCode);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("signInAware", integer);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 补签
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/reSignIn", "/reSignIn/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean reSignIn(HttpServletRequest request, @RequestBody Map<String, Date> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        //周一
        LocalDateTime monday = LocalDate.now().with(DayOfWeek.MONDAY).atTime(0, 0, 0);
        //昨天
        LocalDateTime yesterday = LocalDate.now().atTime(0, 0, 0);
        LocalDateTime signInDate = LocalDateTime.ofInstant( map.get("signInDate").toInstant(), ZoneId.systemDefault());
        //校验日期是否在本周
        if (signInDate.isAfter(monday) && signInDate.isBefore(yesterday)) {
            try {
                userSignInRestServiceI.reSignIn(userInfoId, shareCode, signInDate);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return new ResultBean(ApiResultType.OK, null);
    }

    /**
     * 获取签到情况
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/signIn", "/signIn/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSignIn(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        try {
            JSONObject json = userSignInRestServiceI.getSignIn(userInfoId, shareCode);
            return new ResultBean(ApiResultType.OK, json);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取日历签到情况
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/signInForMonth", "/signIn/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSignInForMonth(HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String time = ParamValidateUtils.getTime(year, month);
        try {
            JSONObject json = userSignInRestServiceI.getSignInForMonth(userInfoId, time);
            return new ResultBean(ApiResultType.OK, json);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }
}
