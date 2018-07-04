package com.fnjz.front.controller.api.usercommuseincome;

import javax.servlet.http.HttpServletRequest;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.entity.api.incometype.IncomeTypeRestEntity;
import com.fnjz.front.service.api.incometype.IncomeTypeRestServiceI;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.jeecgframework.core.common.controller.BaseController;
import com.fnjz.front.service.api.usercommuseincome.UserCommUseIncomeRestServiceI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 用户常用收入类目表
 * @date 2018-06-06 13:24:06
 */
@Controller
@RequestMapping("/api/v1")
public class UserCommUseIncomeRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserCommUseIncomeRestController.class);

    @Autowired
    private UserCommUseIncomeRestServiceI userCommUseIncomeRestService;
    @Autowired
    private IncomeTypeRestServiceI incomeTypeRestServiceI;

    @ApiOperation(value = "获取收入类目列表")
    @RequestMapping(value = "/getIncomeTypeList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIncomeTypeList(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        ResultBean rb = new ResultBean();
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            //传入当前用户详情id
            Map<String, Object> map = userCommUseIncomeRestService.getListById(userInfoId);
            rb.setSucResult(ApiResultType.OK);
            rb.setResult(map);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @ApiOperation(value = "用户常用收入类目添加")
    @RequestMapping(value = "/addCommIncomeType/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommIncomeType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,String> map) {
        ResultBean rb = new ResultBean();
        if(StringUtils.isEmpty(map.get("incomeTypeId"))){
            rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_NULL);
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            //传入当前用户详情id
            //判断用户常用标签表里是否已存在
            boolean flag = userCommUseIncomeRestService.findByUserInfoIdAndId(userInfoId,map.get("incomeTypeId"));
            if(flag){
                rb.setFailMsg(ApiResultType.SPEND_TYPE_IS_ADDED);
                return rb;
            }
            //判断类目是否存在  TODO 如何区分是用户创建类目还是系统类目？？
            IncomeTypeRestEntity task = incomeTypeRestServiceI.findUniqueByProperty(IncomeTypeRestEntity.class, "id", map.get("incomeTypeId"));
            if(task==null){
                rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_NOT_EXIST);
                return rb;
            }
            if(task!=null && StringUtils.isEmpty(task.getParentId())){
                rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_ERROR);
                return rb;
            }
            userCommUseIncomeRestService.insertCommIncomeType(userInfoId,task);
            rb.setSucResult(ApiResultType.OK);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @ApiOperation(value = "用户常用收入类目删除")
    @RequestMapping(value = "/deleteCommIncomeType/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteCommIncomeType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,List<String>> map) {
        ResultBean rb = new ResultBean();
        if(map.get("incomeTypeIds")==null){
            rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_NULL);
            return rb;
        }
        if(map.get("incomeTypeIds").size()<1){
            rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_NULL);
            return rb;
        }
        try {
            String userInfoId = (String) request.getAttribute("userInfoId");
            userCommUseIncomeRestService.deleteCommIncomeType(userInfoId,map.get("incomeTypeIds"));
            rb.setSucResult(ApiResultType.OK);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @RequestMapping(value = "/getIncomeTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIncomeTypeList(HttpServletRequest request) {
        return this.getIncomeTypeList(null,request);
    }

    @RequestMapping(value = "/addCommIncomeType", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommIncomeType(HttpServletRequest request,@RequestBody Map<String,String> map) {
        return this.addCommIncomeType(null,request,map);
    }

    @RequestMapping(value = "/deleteCommIncomeType", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteCommIncomeType(HttpServletRequest request,@RequestBody Map<String,List<String>> map) {
        return this.deleteCommIncomeType(null,request,map);
    }
}
