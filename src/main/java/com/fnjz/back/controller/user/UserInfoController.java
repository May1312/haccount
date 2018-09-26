package com.fnjz.back.controller.user;

import com.fnjz.back.entity.user.UserInfoEntity;
import com.fnjz.back.service.user.UserInfoServiceI;
import com.fnjz.front.utils.ShareCodeUtil;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
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
 * @Description: 用户信息
 * @date 2018-06-01 14:59:20
 */
@Controller
@RequestMapping("/userInfoController")
public class UserInfoController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoServiceI userInfoService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Validator validator;
    @Autowired(required = false)
    private SessionFactory sessionFactory;


    /**
     * 用户信息列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {

        return new ModelAndView("com/fnjz/back/user/userInfoList");
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
    public void datagrid(UserInfoEntity userInfo, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        if (StringUtil.isNotEmpty(userInfo.getId())){
            userInfo.setId(ShareCodeUtil.sharecode2id(String.valueOf(userInfo.getId())));
        }
        CriteriaQuery cq = new CriteriaQuery(UserInfoEntity.class, dataGrid);
        if (StringUtil.isNotEmpty(userInfo.getMobileSystem()) && userInfo.getMobileSystem().equals("xiaochengxu")){
            cq.isNull("mobileSystem");
            userInfo.setMobileSystem(null);
        }
        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, userInfo, request.getParameterMap());

        this.userInfoService.getDataGridReturn(cq, true);
        //蜂鸟id
        List<UserInfoEntity> results = dataGrid.getResults();
        for (UserInfoEntity result : results) {
            result.setId(Integer.parseInt(ShareCodeUtil.id2sharecode(result.getId())));
        }
        //sessionFactory.getCurrentSession().evict(results);
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 删除用户信息
     *
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(UserInfoEntity userInfo, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        userInfo = systemService.getEntity(UserInfoEntity.class, userInfo.getId());
        message = "用户信息删除成功";
        userInfoService.delete(userInfo);
        systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);

        j.setMsg(message);
        return j;
    }


    /**
     * 添加用户信息
     *
     * @param
     * @return
     */
    @RequestMapping(params = "save")
    @ResponseBody
    public AjaxJson save(UserInfoEntity userInfo, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        if (StringUtil.isNotEmpty(userInfo.getId())) {
            message = "用户信息更新成功";
            UserInfoEntity t = userInfoService.get(UserInfoEntity.class, userInfo.getId());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(userInfo, t);
                userInfoService.saveOrUpdate(t);
                systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
            } catch (Exception e) {
                e.printStackTrace();
                message = "用户信息更新失败";
            }
        } else {
            message = "用户信息添加成功";
            userInfoService.save(userInfo);
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * 用户信息列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdate")
    public ModelAndView addorupdate(UserInfoEntity userInfo, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(userInfo.getId())) {
            userInfo = userInfoService.getEntity(UserInfoEntity.class, userInfo.getId());
            req.setAttribute("userInfoPage", userInfo);
        }
        return new ModelAndView("com/fnjz/back/user/userInfo");
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<UserInfoEntity> list() {
        List<UserInfoEntity> listUserInfos = userInfoService.getList(UserInfoEntity.class);
        return listUserInfos;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        UserInfoEntity task = userInfoService.get(UserInfoEntity.class, id);
        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody UserInfoEntity userInfo, UriComponentsBuilder uriBuilder) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<UserInfoEntity>> failures = validator.validate(userInfo);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        userInfoService.save(userInfo);

        //按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
        String id = String.valueOf(userInfo.getId());
        URI uri = uriBuilder.path("/rest/userInfoController/" + id).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody UserInfoEntity userInfo) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<UserInfoEntity>> failures = validator.validate(userInfo);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        userInfoService.saveOrUpdate(userInfo);

        //按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        userInfoService.deleteEntityById(UserInfoEntity.class, id);
    }
}
