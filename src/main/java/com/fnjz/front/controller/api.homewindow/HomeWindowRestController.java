/*
package com.fnjz.front.controller.api.homewindow;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.homewindow.HomeWindowRestServiceI;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

*/
/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 首页弹框相关
 * @date 2018-10-19 11:18:36
 *//*

@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class HomeWindowRestController extends BaseController {
    */
/**
     * Logger for this class
     *//*

    private static final Logger logger = Logger.getLogger(HomeWindowRestController.class);

    @Autowired
    private HomeWindowRestServiceI homeWindowRestServiceI;

    */
/**
     * 获取首页弹框
     * @param request
     * @param map
     * @return
     *//*

    @RequestMapping(value = {"/signInIntegral", "/signInIntegral/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean signInIntegral(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        //类型不为null情况下
        try {
            JsonObject jsonObject = homeWindowRestServiceI.listForWindow(userInfoId, shareCode);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

}
*/
