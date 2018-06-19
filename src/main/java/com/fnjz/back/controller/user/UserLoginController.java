package com.fnjz.back.controller.user;

import com.fnjz.back.entity.user.UserLoginEntity;
import com.fnjz.back.service.user.UserLoginServiceI;
import org.apache.log4j.Logger;
import org.jeecgframework.core.beanvalidator.BeanValidators;
import org.jeecgframework.core.common.controller.BaseController;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.AjaxJson;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.tag.core.easyui.TagUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 用户注册登录信息
 * @date 2018-05-29 15:40:52
 */
@Controller
@RequestMapping("/userLoginController")
public class UserLoginController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserLoginController.class);

    @Autowired
    private UserLoginServiceI userLoginService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Validator validator;


    /**
     * 用户注册登录信息列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {
        return new ModelAndView("com/fnjz/back/user/userLoginList");
    }

    /**
     * easyui AJAX请求数据
     *
     * @param request
     * @param response
     * @param dataGrid
     * @param
     */

    @RequestMapping(params = "datagrid")
    public void datagrid(UserLoginEntity userLogin, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(UserLoginEntity.class, dataGrid);
        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userLogin, request.getParameterMap());
        this.userLoginService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 删除用户注册登录信息
     *
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(UserLoginEntity userLogin, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        userLogin = systemService.getEntity(UserLoginEntity.class, userLogin.getId());
        message = "用户注册登录信息删除成功";
        userLoginService.delete(userLogin);
        systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);

        j.setMsg(message);
        return j;
    }


    /**
     * 添加用户注册登录信息
     *
     * @param
     * @return
     */
    @RequestMapping(params = "save")
    @ResponseBody
    public AjaxJson save(UserLoginEntity userLogin, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        if (StringUtil.isNotEmpty(userLogin.getId())) {
            message = "用户注册登录信息更新成功";
            UserLoginEntity t = userLoginService.get(UserLoginEntity.class, userLogin.getId());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(userLogin, t);
                userLoginService.saveOrUpdate(t);
                systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
            } catch (Exception e) {
                e.printStackTrace();
                message = "用户注册登录信息更新失败";
            }
        } else {
            message = "用户注册登录信息添加成功";
            userLoginService.save(userLogin);
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 用户注册登录信息列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdate")
    public ModelAndView addorupdate(UserLoginEntity userLogin, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(userLogin.getId())) {
            userLogin = userLoginService.getEntity(UserLoginEntity.class, userLogin.getId());
            req.setAttribute("userLoginPage", userLogin);
        }
        return new ModelAndView("com/fnjz/back/user/userLogin");
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<UserLoginEntity> list() {
        List<UserLoginEntity> listUserLogins = userLoginService.getList(UserLoginEntity.class);
        return listUserLogins;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        UserLoginEntity task = userLoginService.get(UserLoginEntity.class, id);
        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody UserLoginEntity userLogin, UriComponentsBuilder uriBuilder) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<UserLoginEntity>> failures = validator.validate(userLogin);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        userLoginService.save(userLogin);

        //按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
        String id = String.valueOf(userLogin.getId());
        URI uri = uriBuilder.path("/rest/userLoginController/" + id).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody UserLoginEntity userLogin) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<UserLoginEntity>> failures = validator.validate(userLogin);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        userLoginService.saveOrUpdate(userLogin);

        //按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        userLoginService.deleteEntityById(UserLoginEntity.class, id);
    }
}
