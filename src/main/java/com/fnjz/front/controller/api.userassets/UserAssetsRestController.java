package com.fnjz.front.controller.api.userassets;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userassets.UserAssetsRestServiceI;
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
 * @Description: 用户资产相关
 * @date 2018-10-20 11:11:26
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserAssetsRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserAssetsRestController.class);

    @Autowired
    private UserAssetsRestServiceI userAssetsRestServiceI;

    /**
     * 获取用户资产接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/assets", "/assets/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getAssets(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        try {
            JSONObject jsonObject = userAssetsRestServiceI.getAssets(userInfoId,shareCode);
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 设置/修改资产
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = {"/saveOrUpdateAssets", "/saveOrUpdateAssets/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean saveOrUpdateAssets(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        if(StringUtils.isNotEmpty(map.get("assetsType")+"")){
            try {
                userAssetsRestServiceI.saveOrUpdateAssets(userInfoId,map);
                return new ResultBean(ApiResultType.OK, null);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }else{
            return new ResultBean(ApiResultType.MY_PARAMS_ERROR,null);
        }
    }

    /**
     * 修改初始时间
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = {"/initDate", "/initDate/{type}"}, method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateInitDate(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        if(StringUtils.isNotEmpty(map.get("initDate")+"")){
            try {
                userAssetsRestServiceI.updateInitDate(userInfoId,map);
                return new ResultBean(ApiResultType.OK, null);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }else{
            return new ResultBean(ApiResultType.MY_PARAMS_ERROR,null);
        }
    }
}