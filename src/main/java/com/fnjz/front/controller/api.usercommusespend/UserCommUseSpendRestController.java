package com.fnjz.front.controller.api.usercommusespend;

import com.alibaba.fastjson.JSONObject;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.spendtype.SpendTypeRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.spendtype.SpendTypeRestServiceI;
import com.fnjz.front.service.api.usercommusespend.UserCommUseSpendRestServiceI;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 用户常用支出类目表
 * @date 2018-06-06 13:25:22
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserCommUseSpendRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserCommUseSpendRestController.class);

    @Autowired
    private UserCommUseSpendRestServiceI userCommUseSpendRestService;
    @Autowired
    private SpendTypeRestServiceI spendTypeRestServiceI;
    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @ApiOperation(value = "获取支出类目列表")
    @RequestMapping(value = "/getSpendTypeList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSpendTypeList(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            Map<String, Object> map = redisTemplateUtils.getCacheLabelType(RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            if (map.size() > 0) {
                return new ResultBean(ApiResultType.OK, map);
            } else {
                map = userCommUseSpendRestService.getListById(userInfoId);
                //缓存类目数据
                redisTemplateUtils.cacheLabelType(map, RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
                return new ResultBean(ApiResultType.OK, map);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "用户常用支出类目添加")
    @RequestMapping(value = "/addCommSpendType/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommSpendType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("spendTypeId"))) {
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL, null);
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            //获取accountBookId
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            //判断用户常用标签表里是否已存在
            boolean flag = userCommUseSpendRestService.findByUserInfoIdAndId(userInfoId, map.get("spendTypeId"));
            if (flag) {
                return new ResultBean(ApiResultType.SPEND_TYPE_IS_ADDED, null);
            }
            //判断类目是否存在  TODO 如何区分是用户创建类目还是系统类目？？
            SpendTypeRestEntity task = spendTypeRestServiceI.findUniqueByProperty(SpendTypeRestEntity.class, "id", map.get("spendTypeId"));
            if (task == null) {
                return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NOT_EXIST, null);
            }
            if (task != null && StringUtils.isEmpty(task.getParentId())) {
                return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_ERROR, null);
            }
            String version = userCommUseSpendRestService.insertCommSpendType(userAccountBookRestEntityCache.getAccountBookId(),userInfoId, task);
            //清空用户类目缓存
            redisTemplateUtils.deleteKey(RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            return new ResultBean(ApiResultType.OK, new JSONObject().put("version",version));
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "用户常用支出类目删除")
    @RequestMapping(value = "/deleteCommSpendType/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteCommSpendType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, List<String>> map) {
        ResultBean rb = ParamValidateUtils.checkDeleteCommSpendType(map);
        if (rb != null) {
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            //获取accountBookId
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            String version = userCommUseSpendRestService.deleteCommSpendType(userAccountBookRestEntityCache.getAccountBookId(),userInfoId, map.get("spendTypeIds"));
            //清空用户类目缓存
            redisTemplateUtils.deleteKey(RedisPrefix.USER_SPEND_LABEL_TYPE + shareCode);
            return new ResultBean(ApiResultType.OK, new JSONObject().put("version",version));
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = "/getSpendTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSpendTypeList(HttpServletRequest request) {
        return this.getSpendTypeList(null, request);
    }

    @RequestMapping(value = "/addCommSpendType", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommSpendType(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return this.addCommSpendType(null, request, map);
    }

    @RequestMapping(value = "/deleteCommSpendType", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteCommSpendType(HttpServletRequest request, @RequestBody Map<String, List<String>> map) {
        return this.deleteCommSpendType(null, request, map);
    }
}
