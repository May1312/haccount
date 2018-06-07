package com.fnjz.back.controller.operating;

import com.fnjz.back.entity.operating.SpendTypeEntity;
import com.fnjz.back.service.operating.SpendTypeServiceI;
import org.apache.log4j.Logger;
import org.hibernate.cache.ehcache.internal.util.HibernateUtil;
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
import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author zhangdaihao
 * @version V1.0
 * @Title: Controller
 * @Description: 支出标签管理
 * @date 2018-06-04 10:57:31
 */
@Controller
@RequestMapping("/spendTypeController")
public class SpendTypeController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(SpendTypeController.class);

    @Autowired
    private SpendTypeServiceI spendTypeService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private Validator validator;


    /**
     * 支出标签管理列表 页面跳转
     *
     * @return
     */
    @RequestMapping(params = "list")
    public ModelAndView list(HttpServletRequest req) {
        if (StringUtil.isNotEmpty(req.getParameter("labelGrade"))) {
            if (req.getParameter("labelGrade").equalsIgnoreCase("2")) {
                return new ModelAndView("com/fnjz/back/operating/spendTypeList2");
            } else if (req.getParameter("labelGrade").equalsIgnoreCase("3")) {
                //父类名称对应id
                List<SpendTypeEntity> SpendTypeEntitys = spendTypeService.findHql("from SpendTypeEntity where  parentId is not null and parentId !=''");
                String parentName = "";
                for (SpendTypeEntity SpendTypeEntity : SpendTypeEntitys) {
                    parentName += SpendTypeEntity.getSpendName() + "_" + SpendTypeEntity.getParentId() + ",";
                }
                if (StringUtil.isNotEmpty(parentName)) {
                    parentName = parentName.substring(0, parentName.length() - 1);
                    req.setAttribute("parentName", parentName);
                }


                return new ModelAndView("com/fnjz/back/operating/spendTypeList3");
            }
        }
        return new ModelAndView("com/fnjz/back/operating/spendTypeList");
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
    public void datagrid(SpendTypeEntity spendType, HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        CriteriaQuery cq = new CriteriaQuery(SpendTypeEntity.class, dataGrid);

        if (StringUtil.isNotEmpty(request.getParameter("labelGrade"))) {
            if (request.getParameter("labelGrade").equalsIgnoreCase("2")) {
                cq.isNull("parentId");
            } else {
                cq.isNotNull("parentId");

            }
        }
        //查询条件组装器
        org.jeecgframework.core.extend.hqlsearch.HqlGenerateUtil.installHql(cq, spendType, request.getParameterMap());
        this.spendTypeService.getDataGridReturn(cq, true);
        TagUtil.datagrid(response, dataGrid);
    }

    @RequestMapping(params = "online")
    @ResponseBody
    public AjaxJson online(HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        String id = request.getParameter("id");
        String msg = "";
        if (StringUtil.isNotEmpty(id)) {
            String sql = "UPDATE hbird_spend_type SET `status`='1'   WHERE id='" + id + "'";
            Integer i = spendTypeService.executeSql(sql);
            if (i > 0) {
                msg = "上线成功";
            } else {
                msg = "上线失败";
            }
        }
        j.setMsg(msg);
        return j;
    }

    /**
     * 删除支出标签管理
     *
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(SpendTypeEntity spendType, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        spendType = systemService.getEntity(SpendTypeEntity.class, spendType.getId());
        message = "支出标签管理删除成功";
        spendTypeService.delete(spendType);
        systemService.addLog(message, Globals.Log_Type_DEL, Globals.Log_Leavel_INFO);

        j.setMsg(message);
        return j;
    }


    /**
     * 添加支出标签管理
     *
     * @param
     * @return
     */
    @RequestMapping(params = "save")
    @ResponseBody
    public AjaxJson save(SpendTypeEntity spendType, HttpServletRequest request) {
        String message = null;
        AjaxJson j = new AjaxJson();
        if (StringUtil.isNotEmpty(spendType.getId())) {
            message = "支出标签管理更新成功";
            SpendTypeEntity t = spendTypeService.get(SpendTypeEntity.class, spendType.getId());
            try {
                MyBeanUtils.copyBeanNotNull2Bean(spendType, t);
                t.setStatus("0");
                spendTypeService.saveOrUpdate(t);

                systemService.addLog(message, Globals.Log_Type_UPDATE, Globals.Log_Leavel_INFO);
                CompareSpendTypePriorty(spendType.getParentId(), spendType.getPriority(), t.getPriority(), "update");
            } catch (Exception e) {
                e.printStackTrace();
                message = "支出标签管理更新失败";

            }
        } else {
            message = "支出标签管理添加成功";
            spendType.setStatus("0");
            spendTypeService.save(spendType);
            systemService.addLog(message, Globals.Log_Type_INSERT, Globals.Log_Leavel_INFO);

            CompareSpendTypePriorty(spendType.getParentId(), spendType.getPriority(), 0, "save");
        }
        j.setMsg(message);

        return j;
    }

    public void CompareSpendTypePriorty(String parentId, int inSertPriority, int Priority, String saveOrUpdagte) {

        String hql = "from SpendTypeEntity where 1=1 ";

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

        List<SpendTypeEntity> list = spendTypeService.findHql(hql);

        //List<SpendTypeEntity> list = spendTypeService.getList(SpendTypeEntity.class);

        Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            SpendTypeEntity spendTypeEntity = list.get(i);
            spendTypeEntity.setPriority(i + 1);
            spendTypeService.updateEntitie(spendTypeEntity);
        }

    }

    //选中常用添加到常用二类中

    public  void addOftenUsed (SpendTypeEntity spendTypeEntity){

        String parentId="";
        String hql = "from SpendTypeEntity where 1=1 and spendName = '常用' ";
        List<SpendTypeEntity> SpendTypeEntitys = spendTypeService.findHql(hql);


        if (SpendTypeEntitys.size()>0){
            SpendTypeEntity spendTypeEntity1 = SpendTypeEntitys.get(0);
            parentId = spendTypeEntity.getId();
        }else {
            SpendTypeEntity spendTypeEntity1 = new SpendTypeEntity();
            spendTypeEntity1.setSpendName("常用");
            spendTypeEntity1.setPriority(0);
            spendTypeEntity1.setStatus("0");
            Serializable save = spendTypeService.save(spendTypeEntity1);
            parentId = spendTypeEntity1.getId();
        }
        //更新三级标签

        spendTypeEntity.setParentId(parentId);
        spendTypeService.saveOrUpdate(spendTypeEntity);
    }

    /**
     * 支出标签管理列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "addorupdate")
    public ModelAndView addorupdate(SpendTypeEntity spendType, HttpServletRequest req) {
        listParentId(req);
        if (StringUtil.isNotEmpty(spendType.getId())) {
            spendType = spendTypeService.getEntity(SpendTypeEntity.class, spendType.getId());
            req.setAttribute("spendTypePage", spendType);
            if (StringUtil.isNotEmpty(spendType.getParentId())) {
                return new ModelAndView("com/fnjz/back/operating/spendType3");
            } else {
                return new ModelAndView("com/fnjz/back/operating/spendType2");
            }
        }

        if (StringUtil.isNotEmpty(req.getParameter("labelGrade"))) {
            if (req.getParameter("labelGrade").equalsIgnoreCase("2")) {
                return new ModelAndView("com/fnjz/back/operating/spendType2");
            } else if (req.getParameter("labelGrade").equalsIgnoreCase("3")) {
                return new ModelAndView("com/fnjz/back/operating/spendType3");
            }
        }

        return new ModelAndView("com/fnjz/back/operating/spendType");

    }

    public void listParentId(HttpServletRequest req) {
        String hql = "from SpendTypeEntity where parentId is  null ";
        List<SpendTypeEntity> twoLabelList = spendTypeService.findByQueryString(hql);
        req.setAttribute("twoLabelList", twoLabelList);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<SpendTypeEntity> list() {
        List<SpendTypeEntity> listSpendTypes = spendTypeService.getList(SpendTypeEntity.class);
        return listSpendTypes;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        SpendTypeEntity task = spendTypeService.get(SpendTypeEntity.class, id);
        if (task == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody SpendTypeEntity spendType, UriComponentsBuilder uriBuilder) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<SpendTypeEntity>> failures = validator.validate(spendType);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        spendTypeService.save(spendType);

        //按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
        String id = spendType.getId();
        URI uri = uriBuilder.path("/rest/spendTypeController/" + id).build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody SpendTypeEntity spendType) {
        //调用JSR303 Bean Validator进行校验，如果出错返回含400错误码及json格式的错误信息.
        Set<ConstraintViolation<SpendTypeEntity>> failures = validator.validate(spendType);
        if (!failures.isEmpty()) {
            return new ResponseEntity(BeanValidators.extractPropertyAndMessage(failures), HttpStatus.BAD_REQUEST);
        }

        //保存
        spendTypeService.saveOrUpdate(spendType);

        //按Restful约定，返回204状态码, 无内容. 也可以返回200状态码.
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        spendTypeService.deleteEntityById(SpendTypeEntity.class, id);
    }
}
