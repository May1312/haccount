package com.fnjz.front.controller.api.usercommtypepriority;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.usercommtypepriority.UserCommTypePriorityRestEntity;
import com.fnjz.front.utils.ParamValidateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.jeecgframework.core.common.controller.BaseController;
import com.fnjz.front.service.api.usercommtypepriority.UserCommTypePriorityRestServiceI;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 用户所属类目排序表相关
 * @date 2018-06-21 15:47:15
 */
@Controller
@RequestMapping("/api/v1")
public class UserCommTypePriorityRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserCommTypePriorityRestController.class);

    @Autowired
    private UserCommTypePriorityRestServiceI userCommTypePriorityRestService;

    @ApiOperation(value = "上传/修改用户所属类目排序关系")
    @RequestMapping(value = "/uploadUserTypePriority/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadUserTypePriority(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String, Object> map) {
        logger.info("请求源:"+type);
        ResultBean rb = ParamValidateUtils.checkUserTypePriority(map);
        if (rb != null) {
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            JSONArray relation1 = JSONArray.fromObject((ArrayList) map.get("relation"));
            UserCommTypePriorityRestEntity userCommTypePriorityRestEntity = new UserCommTypePriorityRestEntity(Integer.valueOf(userInfoId),Integer.valueOf(map.get("type") + ""),relation1.toString());
            userCommTypePriorityRestService.saveOrUpdateRelation(userInfoId,userCommTypePriorityRestEntity);
            return new ResultBean(ApiResultType.OK,null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR,null);
        }
    }

    @RequestMapping(value = "/uploadUserTypePriority", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean uploadUserTypePriority(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        return this.uploadUserTypePriority(null, request, map);
    }
}
