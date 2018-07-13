package com.fnjz.back.controller.appinfo;

import com.fnjz.back.entity.appinfo.AppVersionEntity;
import com.fnjz.back.service.appinfo.AppVersionServiceI;
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
 * @Description: App版本升级
 * @date 2018-05-29 13:48:12
 */
@Controller
@RequestMapping("/appVersionController")
public class AppVersionController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(AppVersionController.class);

    @Autowired
    private AppVersionServiceI appVersionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Validator validator;


    /**
     * App版本升级列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest request) {
        return new ModelAndView("com/fnjz/back/appinfo/appVersionList");
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
    public void datagrid(AppVersionEntity appVersion, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(AppVersionEntity.class, dataGrid);
        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, appVersion, request.getParameterMap());
        this.appVersionService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 删除App版本升级
     *
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(AppVersionEntity appVersion, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        appVersion = systemService.getEntity(AppVersionEntity.class, appVersion.getId());
        message = "App版本升级删除成功";
        appVersionService.delete(appVersion);
        systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);

        j.setMsg(message);
        return j;
    }


    /**
     * 添加App版本升级
     *
     * @param
     * @return
     */
    @RequestMapping(params = "save")
    @ResponseBody
    public AjaxJson save(AppVersionEntity appVersion, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        if (StringUtil.isNotEmpty(appVersion.getId())) {
            message = "App版本升级更新成功";
            AppVersionEntity t = appVersionService.get(AppVersionEntity.class, appVersion.getId());
            try {
                if (appVersion.getUrl().contains(",")) {
                    String getSignInfo = appVersion.getUrl().substring(appVersion.getUrl().indexOf(",") + 1);
                    appVersion.setUrl(getSignInfo);
                    t.setUrl(getSignInfo);
                }
                MyBeanUtils.copyBeanNotNull2Bean(appVersion, t);
                appVersionService.saveOrUpdate(t);
                systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
            } catch (Exception e) {
                e.printStackTrace();
                message = "App版本升级更新失败";
            }
        } else {
            message = "App版本升级添加成功";
            if (appVersion.getUrl().startsWith(",")){
                appVersion.setUrl(appVersion.getUrl().replaceAll(",",""));
            }
            appVersionService.save(appVersion);
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
        }
        j.setMsg(message);
        return j;
    }

    /**
     * App版本升级列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdate")
    public ModelAndView addorupdate(AppVersionEntity appVersion, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(appVersion.getId())) {
            appVersion = appVersionService.getEntity(AppVersionEntity.class, appVersion.getId());
            req.setAttribute("appVersionPage", appVersion);
        }
        return new ModelAndView("com/fnjz/back/appinfo/appVersion");
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<AppVersionEntity> list() {
        List<AppVersionEntity> listAppVersions = appVersionService.getList(AppVersionEntity.class);
        return listAppVersions;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        AppVersionEntity task = appVersionService.get(AppVersionEntity.class, id);
        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody AppVersionEntity appVersion, UriComponentsBuilder uriBuilder) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<AppVersionEntity>> failures = validator.validate(appVersion);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        appVersionService.save(appVersion);

        //按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
        String id = appVersion.getId();
        URI uri = uriBuilder.path("/rest/appVersionController/" + id).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody AppVersionEntity appVersion) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<AppVersionEntity>> failures = validator.validate(appVersion);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        appVersionService.saveOrUpdate(appVersion);

        //按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        appVersionService.deleteEntityById(AppVersionEntity.class, id);
    }
}
