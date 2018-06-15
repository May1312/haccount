package com.fnjz.front.controller.api.warterorder;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.ValidateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

import com.fnjz.front.entity.api.warterorder.WarterOrderRestEntity;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.jeecgframework.core.beanvalidator.BeanValidators;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 账本流水表相关
 * @date 2018-06-14 13:15:47
 */
@Controller
@RequestMapping("/api/v1")
public class WarterOrderRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(WarterOrderRestController.class);

    @Autowired
    private WarterOrderRestServiceI warterOrderRestService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserLoginRestServiceI userLoginRestServiceI;

    /**
     * 账本流水表相关列表 页面跳转
     *
     * @return
     */
    @ApiOperation(value = "记账功能")
    @RequestMapping(value = "/toCharge/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toCharge(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody WarterOrderRestEntity charge) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //校验记账时间
        if (charge.getChargeDate() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_SPENDDATE_ERROR);
            return rb;
        }
        //校验金额
        if (charge.getMoney() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_IS_NULL);
            return rb;
        }
        if (!ValidateUtils.checkDecimal(charge.getMoney() + "")) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_ERROR);
            return rb;
        }
        //判断支出收入类型
        if (charge.getOrderType() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_TYPE_ERROR);
            return rb;
        }
        //校验二三级类目 id
        if (StringUtils.isEmpty(charge.getTypePid())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }
        if (StringUtils.isEmpty(charge.getTypeId())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }

        if (charge.getOrderType() == 1) {
            //支出类型判断即时和分期类型
            if (charge.getIsStaged() == null) {
                rb.setFailMsg(ApiResultType.ACCOUNT_TYPE_ERROR);
                return rb;
            }
        }
        String code = (String) request.getAttribute("code");
        String userInfoId = (String) request.getAttribute("userInfoId");
        String useAccountrCache = getUseAccountCache(Integer.valueOf(userInfoId), code);
        UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
        //获取到账本id 插入记录 TODO 当前账本为1，后台可以获取，后期 账本为多个时，需要传入指定的账本id

        //1 为即时记账类型    2 为分期记账类型
        if (charge.getIsStaged() == 1 && charge.getOrderType() == 1) {
            //使用度必须为空
            if (charge.getUseDegree() != null) {
                charge.setUseDegree(null);
            }
            //设置创建时间
            charge.setCreateDate(new Date());
            //绑定账本id
            charge.setAccountBookId(userLoginRestEntity.getAccountBookId());
            //绑定创建者id
            charge.setCreateBy(userLoginRestEntity.getUserInfoId());
            //绑定创建者名称
            charge.setCreateName(code);
            //设置记录状态
            charge.setDelflag(0);
            warterOrderRestService.save(charge);
            rb.setSucResult(ApiResultType.OK);
            logger.info("单笔支出记账完成");
            return rb;

        } else if (charge.getIsStaged() == 2 && charge.getOrderType() == 1) {
            Map map = new HashMap<>();
            map.put("msg", "分期功能未开放");
            rb.setResult(map);
            return rb;
        }

        //收入类型愉悦度必须为空
        if (charge.getSpendHappiness() != null) {
            charge.setSpendHappiness(null);
        }
        //收入类型即时/分期必须为空
        if (charge.getIsStaged() != null) {
            charge.setIsStaged(null);
        }
        //设置创建时间
        charge.setCreateDate(new Date());
        //绑定账本id
        charge.setAccountBookId(userLoginRestEntity.getAccountBookId());
        //绑定创建者id
        charge.setCreateBy(userLoginRestEntity.getUserInfoId());
        //绑定创建者名称
        charge.setCreateName(code);
        //设置记录状态
        charge.setDelflag(0);
        try {
            warterOrderRestService.save(charge);
            rb.setSucResult(ApiResultType.OK);
            logger.info("单笔收入记账完成");
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @ApiOperation(value = "获取流水分页列表")
    @RequestMapping(value = "/warterOrderList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean warterOrderList(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,
                                   HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "curPage", required = false) String curPage, @RequestParam(value = "pageSize", required = false) String pageSize) {
        System.out.println("登录终端：" + type);
        logger.info("获取流水分页列表接口: year-->" + year + "  month-->" + month + "  curPage-->" + curPage + "  pageSize-->" + pageSize);
        String time = null;
        if (StringUtils.isEmpty(year) && StringUtils.isEmpty(month)) {
            //都为空情况下 获取当年当月
            time = DateUtils.getCurrentYearMonth();
        } else if (StringUtils.isEmpty(year) && StringUtils.isNotEmpty(month)) {
            //获取当年
            year = DateUtils.getCurrentYear() + "";
            time = year + "-" + month;
        } else if (StringUtils.isNotEmpty(year) && StringUtils.isEmpty(month)) {
            //获取当月
            month = DateUtils.getCurrentMonth();
            time = year + "-" + month;
        }
        if (StringUtils.isEmpty(time)) {
            if (!StringUtils.startsWithIgnoreCase(month, "0")
                    && month.length() < 2) {
                month = "0" + month;
            }
            time = year + "-" + month;
        }
        ResultBean rb = new ResultBean();
        try {
            String code = (String) request.getAttribute("code");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = getUseAccountCache(Integer.valueOf(userInfoId), code);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            WarterOrderRestEntity warter = new WarterOrderRestEntity();
            PageRest page = warterOrderRestService.findListForPage(time, userLoginRestEntity.getAccountBookId() + "", Integer.valueOf(curPage), Integer.valueOf(pageSize));
            System.out.println(page);
            rb.setSucResult(ApiResultType.OK);
            rb.setResult(page);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @ApiOperation(value = "根据单笔记账号获取订单详情")
    @RequestMapping(value = "/getOrderInfo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getOrderInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestParam String orderId) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        if (StringUtils.isEmpty(orderId)) {
            rb.setFailMsg(ApiResultType.ORDER_ID_IS_NULL);
            return rb;
        }
        try {
            //获取单笔详情   TODO 现阶段只根据详情id， 后续要加上userid   account book id 判断！！
            WarterOrderRestEntity task = warterOrderRestService.findUniqueByProperty(WarterOrderRestEntity.class, "id", orderId);
            if (task != null) {
                rb.setSucResult(ApiResultType.OK);
                rb.setResult(task);
                return rb;
            }
            rb.setFailMsg(ApiResultType.GET_ORDER_ERROR);
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
        return rb;
    }

    @ApiOperation(value = "修改单笔记账订单详情")
    @RequestMapping(value = "/updateOrderInfo/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateOrderInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody WarterOrderRestEntity charge,HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //判断单笔记账id
        if (charge.getId() == null) {
            rb.setFailMsg(ApiResultType.ORDER_ID_IS_NULL);
            return rb;
        }
        //校验金额
        if (charge.getMoney() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_IS_NULL);
            return rb;
        }
        if (!ValidateUtils.checkDecimal(charge.getMoney() + "")) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_ERROR);
            return rb;
        }
        //判断支出收入类型
        if (charge.getOrderType() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_TYPE_ERROR);
            return rb;
        }
        //校验二三级类目 id
        if (StringUtils.isEmpty(charge.getTypePid())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }
        if (StringUtils.isEmpty(charge.getTypeId())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }

        if (charge.getOrderType() == 1) {
            //支出类型判断即时和分期类型
            if (charge.getIsStaged() == null) {
                rb.setFailMsg(ApiResultType.ACCOUNT_TYPE_ERROR);
                return rb;
            }
        }
        String code = (String) request.getAttribute("code");
        String userInfoId = (String) request.getAttribute("userInfoId");
        String useAccountrCache = getUseAccountCache(Integer.valueOf(userInfoId), code);
        UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
        //获取到账本id 更新记录 TODO 当前账本为1，后台可以获取，后期 账本为多个时，需要传入指定的账本id

        //1 为即时记账类型    2 为分期记账类型
        if (charge.getIsStaged() == 1 && charge.getOrderType() == 1) {
            //使用度必须为空
            if (charge.getUseDegree() != null) {
                charge.setUseDegree(null);
            }
            //设置更新时间
            charge.setUpdateDate(new Date());
            //绑定账本id
            charge.setAccountBookId(userLoginRestEntity.getAccountBookId());
            //绑定修改者id
            charge.setUpdateBy(userLoginRestEntity.getUserInfoId());
            //绑定修改者名称
            charge.setUpdateName(code);
            //设置记录状态
            charge.setDelflag(0);
            warterOrderRestService.update(charge);
            rb.setSucResult(ApiResultType.OK);
            logger.info("单笔支出记账更新完成");
            return rb;
        } else if (charge.getIsStaged() == 2 && charge.getOrderType() == 1) {
            Map map = new HashMap<>();
            map.put("msg", "分期功能未开放");
            rb.setResult(map);
            return rb;
        }

        //收入类型愉悦度必须为空
        if (charge.getSpendHappiness() != null) {
            charge.setSpendHappiness(null);
        }
        //收入类型即时/分期必须为空
        if (charge.getIsStaged() != null) {
            charge.setIsStaged(null);
        }
        //设置创建时间
        charge.setCreateDate(new Date());
        //绑定账本id
        charge.setAccountBookId(userLoginRestEntity.getAccountBookId());
        //绑定修改者id
        charge.setUpdateBy(userLoginRestEntity.getUserInfoId());
        //绑定修改者名称
        charge.setUpdateName(code);
        //设置记录状态
        charge.setDelflag(0);
        try {
            warterOrderRestService.update(charge);
            rb.setSucResult(ApiResultType.OK);
            logger.info("单笔收入记账更新完成");
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    @ApiOperation(value = "修改单笔记账订单详情")
    @RequestMapping(value = "/deleteOrder/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteOrder(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map,HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        if(StringUtils.isEmpty(map.get("id"))){
            rb.setFailMsg(ApiResultType.ORDER_ID_IS_NULL);
            return rb;
        }
        try {
            WarterOrderRestEntity task = warterOrderRestService.findUniqueByProperty(WarterOrderRestEntity.class, "id", map.get("id"));
            if (task == null) {
                rb.setFailMsg(ApiResultType.GET_ORDER_ERROR);
                return rb;
            }
            //获取当前用户信息
            String userInfoId = (String) request.getAttribute("userInfoId");
            String code = (String) request.getAttribute("code");
            //执行更新
            int i = warterOrderRestService.deleteOrder(map.get("id"),userInfoId,code);
            if (i < 1) {
                rb.setFailMsg(ApiResultType.DELETE_RECORD_ERROR);
                return rb;
            }
            rb.setSucResult(ApiResultType.OK);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    //从cache获取用户账本信息通用方法
    private String getUseAccountCache(int userInfoId, String code) {
        String user_account = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user_account)) {
            UserAccountBookRestEntity task = warterOrderRestService.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            //设置redis缓存 缓存用户账本信息 30天
            String r_user_account = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code, r_user_account, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return r_user_account;
        }
        return user_account;
    }

    //从cache获取用户信息通用方法
    private String getUserCache(String code) {
        String user = (String) redisTemplate.opsForValue().get(code);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user)) {
            UserLoginRestEntity task;
            //判断code类型
            if (ValidateUtils.isMobile(code)) {
                task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "mobile", code);
            } else {
                task = userLoginRestServiceI.findUniqueByProperty(UserLoginRestEntity.class, "wechat_auth", code);
            }
            //设置redis缓存 缓存用户信息 30天 毫秒
            String r_user = JSON.toJSONString(task);
            updateCache(r_user, code);
            return r_user;
        }
        return user;
    }

    //更新redis缓存通用方法
    private void updateCache(String user, String code) {
        redisTemplate.opsForValue().set(code, user, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
    }

    @RequestMapping(value = "/toCharge", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toCharge(HttpServletRequest request, @RequestBody WarterOrderRestEntity charge) {
        return this.toCharge(null, request,charge);
    }

    @RequestMapping(value = "/warterOrderList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean warterOrderList(HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "curPage", required = false) String curPage, @RequestParam(value = "pageSize", required = false) String pageSize) {
        return this.warterOrderList(null, request,year,month,curPage,pageSize);
    }

    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getOrderInfo(@RequestParam String orderId) {
        return this.getOrderInfo(null, orderId);
    }

    @RequestMapping(value = "/updateOrderInfo", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateOrderInfo(@RequestBody WarterOrderRestEntity charge,HttpServletRequest request) {
        return this.updateOrderInfo(null, charge,request);
    }

    @RequestMapping(value = "/deleteOrder", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteOrder(@RequestBody @ApiIgnore Map<String, String> map,HttpServletRequest request) {
        return this.deleteOrder(null, map,request);
    }
}
