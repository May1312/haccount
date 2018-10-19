package com.fnjz.front.controller.api.homewindow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.homewindow.HomeWindowRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
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

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 首页弹框相关
 * @date 2018-10-19 11:18:36
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class HomeWindowRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(HomeWindowRestController.class);

    @Autowired
    private HomeWindowRestServiceI homeWindowRestServiceI;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 获取首页弹框
     * @param request
     * @return
     */
    @RequestMapping(value = {"/homeWindow", "/homeWindow/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean signInIntegral(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        //类型不为null情况下
        try {
            JSONObject jsonObject = homeWindowRestServiceI.listForWindow(userInfoId, shareCode);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 用户读取活动
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = {"/homeWindow/hasRead", "/homeWindow/hasRead/{type}"}, method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean hasRead(HttpServletRequest request, @RequestBody Map<String,String> map) {
        String shareCode = (String) request.getAttribute("shareCode");
        if(map.get("activityId")!=null){
            try {
                String cacheActivity = redisTemplateUtils.getForString(RedisPrefix.USER_HOME_WINDOW_READ + shareCode);
                JSONArray activity = JSONArray.parseArray(cacheActivity);
                if(activity!=null){
                    for (int i = 0; i < activity.size(); i++) {
                        JSONObject jsonObject = activity.getJSONObject(i);
                        if(StringUtils.equals(jsonObject.getString("activityId"),map.get("activityId"))){
                            jsonObject.put("hasRead",2);
                            //统计点击人数
                            statisticsHasRead(RedisPrefix.SYS_HOME_WINDOW_READ+map.get("activityId"));
                        }
                    }
                    //重置缓存
                    redisTemplateUtils.cacheForString(RedisPrefix.USER_HOME_WINDOW_READ + shareCode, activity.toJSONString());
                }
                return new ResultBean(ApiResultType.OK, null);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
        return new ResultBean(ApiResultType.OK, null);
    }

    private void statisticsHasRead(String key){
        redisTemplateUtils.incrementForHash(key,"hasRead",1);
    }

    /**
     * 轮播图
     * @param request
     * @return
     */
    @RequestMapping(value = {"/slideShow", "/slideShow/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean slideShow(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        try {
            JSONObject jsonObject = homeWindowRestServiceI.listForSlideShow(userInfoId, shareCode);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 轮播图已读
     * @param map
     * @return
     */
    @RequestMapping(value = {"/slideShow/hasRead", "/slideShow/hasRead/{type}"}, method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean slideShow(@RequestBody Map<String,String> map) {
        if(map.get("slideShowId")!=null){
            //统计点击人数
            statisticsHasRead(RedisPrefix.SYS_SLIDESHOW_READ+map.get("slideShowId"));
        }
        return new ResultBean(ApiResultType.OK, null);
    }
}
