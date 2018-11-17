package com.fnjz.front.controller.api.userprivatelabel;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.service.api.userprivatelabel.UserPrivateLabelRestService;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户自有标签获取
 * Created by yhang on 2018/11/7.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserPrivateLabelRestController {

    private static final Logger logger = Logger.getLogger(UserPrivateLabelRestController.class);

    @Autowired
    private UserPrivateLabelRestService userPrivateLabelRestService;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @RequestMapping(value = {"/getIncomeLabel/{type}", "/getSpendLabel/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIncomeTypeList(@PathVariable("type") String type, HttpServletRequest request, @RequestParam String abTypeId) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        if (StringUtils.isEmpty(abTypeId)) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR, null);
        }
        JSONObject jsonObject;
        try {
            if (StringUtils.contains(request.getRequestURI(), "/getIncomeLabel")) {
                //收入请求
                jsonObject = userPrivateLabelRestService.getCacheLabel(Integer.valueOf(abTypeId), userInfoId, shareCode, RedisPrefix.INCOME);
            } else {
                //支出请求
                jsonObject = userPrivateLabelRestService.getCacheLabel(Integer.valueOf(abTypeId), userInfoId, shareCode, RedisPrefix.SPEND);
            }
            return new ResultBean(ApiResultType.OK, jsonObject);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = {"/addIncomeLabel/{type}", "/addSpendLabel/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addIncomeLabel(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("labelId")) || StringUtils.isEmpty(map.get("abId"))) {
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL, null);
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            Integer status = userPrivateLabelRestService.checkExists(map.get("abId"), map.get("labelId"));
            if (status != null) {
                if (status > 0) {
                    return new ResultBean(ApiResultType.SPEND_TYPE_IS_ADDED, null);
                }
            }
            JSONObject resultmap = new JSONObject();
            if (StringUtils.contains(request.getRequestURI(), "/addIncomeLabel")) {
                //清空用户类目缓存
                if (redisTemplateUtils.hasKey(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.INCOME)) {
                    redisTemplateUtils.deleteHashValueV2(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.INCOME);
                }
                if (StringUtils.equalsIgnoreCase("ios", type) || StringUtils.equalsIgnoreCase("android", type)) {
                    resultmap = userPrivateLabelRestService.insertUserPrivateLabelForMap(shareCode, map.get("abId"), map.get("labelId"), userInfoId, RedisPrefix.INCOME);
                } else {
                    String version = userPrivateLabelRestService.insertUserPrivateLabelType(map.get("abId"), map.get("labelId"), userInfoId,RedisPrefix.INCOME);
                    resultmap.put("version", version);
                }
            } else {
                //清空用户类目缓存
                if (redisTemplateUtils.hasKey(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.SPEND)) {
                    redisTemplateUtils.deleteHashValueV2(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.SPEND);
                }
                if (StringUtils.equalsIgnoreCase("ios", type) || StringUtils.equalsIgnoreCase("android", type)) {
                    resultmap = userPrivateLabelRestService.insertUserPrivateLabelForMap(shareCode, map.get("abId"), map.get("labelId"), userInfoId, RedisPrefix.SPEND);
                } else {
                    String version = userPrivateLabelRestService.insertUserPrivateLabelType(map.get("abId"), map.get("labelId"), userInfoId, RedisPrefix.SPEND);
                    resultmap.put("version", version);
                }
            }
            return new ResultBean(ApiResultType.OK, resultmap);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = {"/deleteIncomeLabel/{type}", "/deleteSpendLabel/{type}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteIncomeLabel(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,Object> map) {
        if (map.get("labelIds")==null || map.get("abId")==null) {
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            JSONObject resultmap = new JSONObject();
            if (StringUtils.contains(request.getRequestURI(), "/deleteIncomeLabel")) {
                //清空用户类目缓存
                if (redisTemplateUtils.hasKey(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.INCOME)) {
                    redisTemplateUtils.deleteHashValueV2(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.INCOME);
                }
                if (StringUtils.equalsIgnoreCase("ios", type) || StringUtils.equalsIgnoreCase("android", type)) {
                    resultmap = userPrivateLabelRestService.deleteUserPrivateLabelForMap(shareCode,map, userInfoId,RedisPrefix.INCOME);
                } else {
                    String version = userPrivateLabelRestService.deleteUserPrivateLabelType(shareCode,map, userInfoId,RedisPrefix.INCOME);
                    resultmap.put("version", version);
                }
            } else {
                //清空用户类目缓存
                if (redisTemplateUtils.hasKey(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.SPEND)) {
                    redisTemplateUtils.deleteHashValueV2(RedisPrefix.USER_LABEL + shareCode + ":" + map.get("abId"), RedisPrefix.SPEND);
                }
                if (StringUtils.equalsIgnoreCase("ios", type) || StringUtils.equalsIgnoreCase("android", type)) {
                    resultmap = userPrivateLabelRestService.deleteUserPrivateLabelForMap(shareCode,map, userInfoId,RedisPrefix.SPEND);
                } else {
                    String version = userPrivateLabelRestService.deleteUserPrivateLabelType(shareCode,map, userInfoId,RedisPrefix.SPEND);
                    resultmap.put("version", version);
                }
            }
            return new ResultBean(ApiResultType.OK, resultmap);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = {"/getIncomeLabel", "/getSpendLabel"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIncomeTypeList(HttpServletRequest request, @RequestParam String abTypeId) {
        return this.getIncomeTypeList(null, request, abTypeId);
    }

    @RequestMapping(value = {"/addIncomeLabel", "/addSpendLabel"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addIncomeLabel(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return this.addIncomeLabel(null, request, map);
    }

    @RequestMapping(value = {"/deleteIncomeLabel", "/deleteSpendLabel"}, method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteIncomeLabel(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        return this.deleteIncomeLabel(null, request, map);
    }
}
