package com.fnjz.front.controller.api.usercommuseincome;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.incometype.IncomeTypeRestServiceI;
import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 用户常用收入类目表
 * @date 2018-06-06 13:24:06
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class UserCommUseIncomeRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserCommUseIncomeRestController.class);

    @Autowired
    private UserCommUseIncomeRestServiceI userCommUseIncomeRestService;
    @Autowired
    private IncomeTypeRestServiceI incomeTypeRestServiceI;
    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @ApiOperation(value = "获取收入类目列表")
    @RequestMapping(value = "/getIncomeTypeList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIncomeTypeList(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            Map<String, Object> map = redisTemplateUtils.getCacheLabelType(RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            if (map.size() > 0) {
                return new ResultBean(ApiResultType.OK, map);
            } else {
                map = userCommUseIncomeRestService.getListById(userInfoId);
                redisTemplateUtils.cacheLabelType(map, RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
                return new ResultBean(ApiResultType.OK, map);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "用户常用收入类目添加")
    @RequestMapping(value = "/addCommIncomeType/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommIncomeType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, String> map) {
        if (StringUtils.isEmpty(map.get("incomeTypeId"))) {
            return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NULL, null);
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            //获取accountBookId
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            //传入当前用户详情id
            boolean flag = userCommUseIncomeRestService.findByUserInfoIdAndId(userInfoId, map.get("incomeTypeId"));
            if (flag) {
                return new ResultBean(ApiResultType.SPEND_TYPE_IS_ADDED, null);
            }
            //判断类目是否存在  TODO 如何区分是用户创建类目还是系统类目？？
            IncomeTypeRestEntity task = incomeTypeRestServiceI.findUniqueByProperty(IncomeTypeRestEntity.class, "id", map.get("incomeTypeId"));
            if (task == null) {
                return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_NOT_EXIST, null);
            }
            if (task != null && StringUtils.isEmpty(task.getParentId())) {
                return new ResultBean(ApiResultType.SPEND_TYPE_ID_IS_ERROR, null);
            }
            //清空用户类目缓存
            redisTemplateUtils.deleteKey(RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);
            Map<String,Object> resultmap = new HashMap<>();
            if(StringUtils.equalsIgnoreCase("ios",type) || StringUtils.equalsIgnoreCase("android",type)){
                resultmap = userCommUseIncomeRestService.insertCommIncomeTypeForMap(shareCode,userAccountBookRestEntityCache.getAccountBookId(), userInfoId, task);
            }else{
                String version = userCommUseIncomeRestService.insertCommIncomeType(userAccountBookRestEntityCache.getAccountBookId(), userInfoId, task);
                resultmap.put("version",version);
            }
            return new ResultBean(ApiResultType.OK,resultmap);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "用户常用收入类目删除")
    @RequestMapping(value = "/deleteCommIncomeType/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteCommIncomeType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, List<String>> map) {
        ResultBean rb = ParamValidateUtils.checkDeleteCommIncomeType(map);
        if (rb != null) {
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            //获取accountBookId
            UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
            //清空用户类目缓存
            redisTemplateUtils.deleteKey(RedisPrefix.USER_INCOME_LABEL_TYPE + shareCode);

            Map<String,Object> resultmap = new HashMap<>();
            if(StringUtils.equalsIgnoreCase("ios",type) || StringUtils.equalsIgnoreCase("android",type)){
                resultmap = userCommUseIncomeRestService.deleteCommIncomeTypeForMap(shareCode,userAccountBookRestEntityCache.getAccountBookId(), userInfoId, map.get("incomeTypeIds"));
            }else{
                String version = userCommUseIncomeRestService.deleteCommIncomeType(userAccountBookRestEntityCache.getAccountBookId(), userInfoId, map.get("incomeTypeIds"));
                resultmap.put("version",version);
            }
            return new ResultBean(ApiResultType.OK,resultmap);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @RequestMapping(value = "/getIncomeTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIncomeTypeList(HttpServletRequest request) {
        return this.getIncomeTypeList(null, request);
    }

    @RequestMapping(value = "/addCommIncomeType", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommIncomeType(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return this.addCommIncomeType(null, request, map);
    }

    @RequestMapping(value = "/deleteCommIncomeType", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteCommIncomeType(HttpServletRequest request, @RequestBody Map<String, List<String>> map) {
        return this.deleteCommIncomeType(null, request, map);
    }
}
