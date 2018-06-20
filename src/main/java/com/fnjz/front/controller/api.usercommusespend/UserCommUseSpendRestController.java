package com.fnjz.front.controller.api.usercommusespend;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fnjz.back.entity.operating.SpendTypeEntity;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.service.api.spendtype.SpendTypeRestServiceI;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.pojo.base.TSDepart;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.core.util.MyBeanUtils;

import com.fnjz.front.entity.api.usercommusespend.UserCommUseSpendRestEntity;
import com.fnjz.front.service.api.usercommusespend.UserCommUseSpendRestServiceI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.jeecgframework.core.beanvalidator.BeanValidators;

import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 用户常用支出类目表
 * @date 2018-06-06 13:25:22
 */
@Controller
@RequestMapping("/api/v1")
public class UserCommUseSpendRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserCommUseSpendRestController.class);

    @Autowired
    private UserCommUseSpendRestServiceI userCommUseSpendRestService;
    @Autowired
    private SpendTypeRestServiceI spendTypeRestServiceI;

    @ApiOperation(value = "获取支出类目列表")
    @RequestMapping(value = "/getSpendTypeList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean list(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request) {
        ResultBean rb = new ResultBean();
        try {
            String user_info_id = (String) request.getAttribute("userInfoId");
            //传入当前用户详情id
            Map<String, Object> map = userCommUseSpendRestService.getListById(user_info_id);
            rb.setSucResult(ApiResultType.OK);
            rb.setResult(map);
            return rb;
        } catch (Exception e) {
            e.printStackTrace();
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @ApiOperation(value = "用户常用支出类目添加")
    @RequestMapping(value = "/addCommSpendType/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommSpendType(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody Map<String,String> map) {
        ResultBean rb = new ResultBean();
        if(StringUtils.isEmpty(map.get("spendTypeId"))){
            rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_NULL);
            return rb;
        }
        try {
            String user_info_id = (String) request.getAttribute("userInfoId");
            //传入当前用户详情id
            //判断用户常用标签表里是否已存在
            boolean flag = userCommUseSpendRestService.findByUserInfoIdAndId(user_info_id,map.get("spendTypeId"));
            if(flag){
                rb.setFailMsg(ApiResultType.SPEND_TYPE_IS_ADDED);
                return rb;
            }
            //判断类目是否存在  TODO 如何区分是用户创建类目还是系统类目？？
            SpendTypeEntity task = spendTypeRestServiceI.findUniqueByProperty(SpendTypeEntity.class, "id", map.get("spendTypeId"));
            if(task==null){
                rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_NOT_EXIST);
                return rb;
            }
            if(task!=null && StringUtils.isEmpty(task.getParentId())){
                rb.setFailMsg(ApiResultType.SPEND_TYPE_ID_IS_ERROR);
                return rb;
            }
            userCommUseSpendRestService.insertCommSpendType(user_info_id,task);
            rb.setSucResult(ApiResultType.OK);
            return rb;
        } catch (Exception e) {
            e.printStackTrace();
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @RequestMapping(value = "/getSpendTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean list(HttpServletRequest request) {
        return this.list(null,request);
    }

    @RequestMapping(value = "/addCommSpendType", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean addCommSpendType(HttpServletRequest request,@RequestBody Map<String,String> map) {
        return this.addCommSpendType(null,request,map);
    }
}
