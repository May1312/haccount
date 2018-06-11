package com.fnjz.front.controller.api.usercommusespend;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private SystemService systemService;


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

    @RequestMapping(value = "/getSpendTypeList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean list(HttpServletRequest request) {
        return this.list(null,request);
    }
}
