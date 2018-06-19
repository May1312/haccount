package com.fnjz.back.controller.operating;

import com.fnjz.back.entity.operating.IncomeTypeEntity;
import com.fnjz.back.service.operating.IncomeTypeServiceI;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 收入标签管理
 * @date 2018-06-04 10:58:12
 */
@Controller
@RequestMapping("/incomeTypeController")
public class IncomeTypeController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IncomeTypeController.class);

    @Autowired
    private IncomeTypeServiceI incomeTypeService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Validator validator;


    /**
     * 收入标签管理列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest req) {

        if (StringUtil.isNotEmpty(req.getParameter("labelGrade"))) {
            if (req.getParameter("labelGrade").equalsIgnoreCase("2")) {
                return new ModelAndView("com/fnjz/back/operating/incomeTypeList2");
            } else if (req.getParameter("labelGrade").equalsIgnoreCase("3")) {

                parentIdToName(req);
                return new ModelAndView("com/fnjz/back/operating/incomeTypeList3");
            }
        }
        return new ModelAndView("com/fnjz/back/operating/incomeTypeList");
    }

    public void parentIdToName(HttpServletRequest req) {
        //父类名称对应id
        List<IncomeTypeEntity> IncomeTypeEntitys = incomeTypeService.findHql("from IncomeTypeEntity where  parentId is  null ");
        String parentName = "";
        for (IncomeTypeEntity incomeTypeEntity : IncomeTypeEntitys) {
            parentName += incomeTypeEntity.getIncomeName() + "_" + incomeTypeEntity.getId() + ",";
        }
        if (StringUtil.isNotEmpty(parentName)) {
            parentName = parentName.substring(0, parentName.length() - 1);
            req.setAttribute("parentName", parentName);
        }
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
    public void datagrid(IncomeTypeEntity incomeType, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        parentIdToName(request);
        CriteriaQuery cq = new CriteriaQuery(IncomeTypeEntity.class, dataGrid);

        if (StringUtil.isNotEmpty(request.getParameter("labelGrade"))) {
            if (request.getParameter("labelGrade").equalsIgnoreCase("2")) {
                cq.isNull("parentId");
            } else {
                cq.isNotNull("parentId");
            }
        }


        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, incomeType, request.getParameterMap());
        this.incomeTypeService.getDataGridReturn(cq, true);

        TagUtil.datagrid(response, dataGrid);
    }

    /**
     * 删除收入标签管理
     *
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(IncomeTypeEntity incomeType, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        incomeType = systemService.getEntity(IncomeTypeEntity.class, incomeType.getId());
        message = "收入标签管理删除成功";
        incomeTypeService.delete(incomeType);
        systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);

        j.setMsg(message);
        return j;
    }


    /**
     * 添加收入标签管理
     *
     * @param
     * @return
     */
    @RequestMapping(params = "save")
    @ResponseBody
    public AjaxJson save(IncomeTypeEntity incomeType, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        if (StringUtil.isNotEmpty(incomeType.getId())) {
            message = "收入标签管理更新成功";
            IncomeTypeEntity t = incomeTypeService.get(IncomeTypeEntity.class, incomeType.getId());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(incomeType, t);
                t.setStatus("0");
                incomeTypeService.saveOrUpdate(t);
                systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
                CompareIncomeTypePriorty(incomeType.getParentId(), incomeType.getPriority(), t.getPriority(), "update");
            } catch (Exception e) {
                e.printStackTrace();
                message = "收入标签管理更新失败";
            }
        } else {
            message = "收入标签管理添加成功";
            incomeType.setStatus("0");
            incomeTypeService.save(incomeType);
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);
            CompareIncomeTypePriorty(incomeType.getParentId(), incomeType.getPriority(), 0, "update");
        }
        j.setMsg(message);

        return j;
    }

    @RequestMapping(params = "online")
    @ResponseBody
    public AjaxJson online(HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String id = request.getParameter("id");
        String msg = "";
        if (StringUtil.isNotEmpty(id)) {
            String sql = "UPDATE hbird_income_type SET `status`='1'   WHERE id='" + id + "'";
            Integer i = incomeTypeService.executeSql(sql);
            if (i > 0) {
                msg = "上线成功";
            } else {
                msg = "上线失败";
            }
        }
        j.setMsg(msg);
        return j;
    }


    public void CompareIncomeTypePriorty(String parentId, int inSertPriority, int Priority, String saveOrUpdagte) {

        String hql = "from IncomeTypeEntity where 1=1 ";

        if (StringUtil.isEmpty(parentId)) {
            hql += " and parentId is null or parentId = '' ";
        } else {
            hql += " and parentId is not null or parentId !='' ";
        }

        if (saveOrUpdagte.equalsIgnoreCase("save")) {
            hql += " order by createDate desc";
        } else {
            try {
                if (inSertPriority > Priority) {
                    hql += " order by updateDate desc ,createDate asc";
                } else {
                    hql += " order by updateDate desc ,createDate desc";
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }


        List<IncomeTypeEntity> list = incomeTypeService.findHql(hql);

        //List<SpendTypeEntity> list = spendTypeService.getList(SpendTypeEntity.class);

        Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            IncomeTypeEntity incomeTypeEntity = list.get(i);
            incomeTypeEntity.setPriority(i + 1);
            incomeTypeService.updateEntitie(incomeTypeEntity);
        }


    }

    /**
     * 收入标签管理列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdate")
    public ModelAndView addorupdate(IncomeTypeEntity incomeType, HttpServletRequest req) {
        listParentId(req);
        if (StringUtil.isNotEmpty(incomeType.getId())) {
            incomeType = incomeTypeService.getEntity(IncomeTypeEntity.class, incomeType.getId());
            req.setAttribute("incomeTypePage", incomeType);
            if (StringUtil.isNotEmpty(incomeType.getParentId())) {
                return new ModelAndView("com/fnjz/back/operating/incomeType3");
            } else {
                return new ModelAndView("com/fnjz/back/operating/incomeType2");
            }
        }

        if (StringUtil.isNotEmpty(req.getParameter("labelGrade"))) {
            if (req.getParameter("labelGrade").equalsIgnoreCase("2")) {
                return new ModelAndView("com/fnjz/back/operating/incomeType2");
            } else if (req.getParameter("labelGrade").equalsIgnoreCase("3")) {
                return new ModelAndView("com/fnjz/back/operating/incomeType3");
            }
        }

        return new ModelAndView("com/fnjz/back/operating/incomeType");

    }

    public void listParentId(HttpServletRequest req) {
        String hql = "from IncomeTypeEntity where parentId is  null ";
        List<IncomeTypeEntity> twoLabelList = incomeTypeService.findByQueryString(hql);
        req.setAttribute("twoLabelList", twoLabelList);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<IncomeTypeEntity> list() {
        List<IncomeTypeEntity> listIncomeTypes = incomeTypeService.getList(IncomeTypeEntity.class);
        return listIncomeTypes;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        IncomeTypeEntity task = incomeTypeService.get(IncomeTypeEntity.class, id);
        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody IncomeTypeEntity incomeType, UriComponentsBuilder uriBuilder) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<IncomeTypeEntity>> failures = validator.validate(incomeType);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        incomeTypeService.save(incomeType);

        //按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
        String id = incomeType.getId();
        URI uri = uriBuilder.path("/rest/incomeTypeController/" + id).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody IncomeTypeEntity incomeType) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<IncomeTypeEntity>> failures = validator.validate(incomeType);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        incomeTypeService.saveOrUpdate(incomeType);

        //按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        incomeTypeService.deleteEntityById(IncomeTypeEntity.class, id);
    }
}
