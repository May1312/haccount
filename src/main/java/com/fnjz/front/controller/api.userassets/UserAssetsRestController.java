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
import org.springframework.web.bind.annotation.*;

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
     * @flag  flag=1 获取账户列表
     * @param request
     * @return
     */
    @RequestMapping(value = {"/assets/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getAssets(@PathVariable String type,HttpServletRequest request, @RequestParam(required = false) String flag) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        if(StringUtils.equals(type,"ios")||StringUtils.equals(type,"android")){
            try {
                JSONObject jsonObject = userAssetsRestServiceI.getAssetsv2(userInfoId,shareCode,flag);
                return new ResultBean(ApiResultType.OK, jsonObject);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }else{
            //旧版资产接口
            try {
                JSONObject jsonObject = userAssetsRestServiceI.getAssets(userInfoId,shareCode,flag);
                return new ResultBean(ApiResultType.OK, jsonObject);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
    }

    /**
     * 添加到用户默认账户类型+移除用户默认账户类型
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = {"/addAT2Mark", "/addAT2Mark/{type}","/deleteAT2Mark", "/deleteAT2Mark/{type}"}, method = {RequestMethod.POST,RequestMethod.DELETE})
    @ResponseBody
    public ResultBean addAT2Mark(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        if(StringUtils.isNotEmpty(map.get("ats")+"")){
            if (StringUtils.contains(request.getRequestURI(), "/addAT2Mark")) {
                try {
                    userAssetsRestServiceI.addAT2Mark(userInfoId,map);
                    return new ResultBean(ApiResultType.OK, null);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            }else{
                try {
                    userAssetsRestServiceI.deleteAT2Mark(userInfoId,map);
                    return new ResultBean(ApiResultType.OK, null);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            }
        }else{
            return new ResultBean(ApiResultType.MY_PARAMS_ERROR,null);
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

    @RequestMapping(value = "/assets", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean assets(HttpServletRequest request, @RequestParam(required = false) String flag) {
        return this.getAssets(null, request, flag);
    }
}
