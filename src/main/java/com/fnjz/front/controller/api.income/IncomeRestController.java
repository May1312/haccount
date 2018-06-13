package com.fnjz.front.controller.api.income;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.income.IncomeRestEntity;
import com.fnjz.front.entity.api.spend.SpendRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import com.fnjz.front.service.api.income.IncomeRestServiceI;
import com.fnjz.front.service.api.userlogin.UserLoginRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.ValidateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 账本-收入表相关
 * @date 2018-06-06 13:27:56
 */
@Controller
@RequestMapping("/api/v1")
public class IncomeRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(IncomeRestController.class);

    @Autowired
    private IncomeRestServiceI incomeRestService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserLoginRestServiceI userLoginRestService;

    /**
     * 收入记账功能
     *
     * @return
     */
    @ApiOperation(value = "收入记账")
    @RequestMapping(value = "/incomeToCharge/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean incomeToCharge(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody IncomeRestEntity income) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //校验记账时间
        if (income.getIncomeDate() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_SPENDDATE_ERROR);
            return rb;
        }
        //校验金额
        if (income.getIncomeMoney() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_IS_NULL);
            return rb;
        }
        if (!ValidateUtils.checkDecimal(income.getIncomeMoney() + "")) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_ERROR);
            return rb;
        }
        //校验二三级类目 id
        if (StringUtils.isEmpty(income.getIncomeTypePid())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }
        if (StringUtils.isEmpty(income.getIncomeTypeId())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }
        try {
            String code = (String) request.getAttribute("code");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = getUseAccountrCache(Integer.valueOf(userInfoId), code);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            //获取到账本id 插入记录 TODO 当前账本为1，后台可以获取，后期 账本为多个时，需要传入指定的账本id
            //设置单笔记录号
            income.setIncomeOrder(CommonUtils.getAccountOrder());
            //设置创建时间
            income.setCreateDate(new Date());
            //绑定账本id
            income.setAccountBookId(userLoginRestEntity.getAccountBookId());
            //绑定创建者id
            income.setCreateBy(userLoginRestEntity.getUserInfoId());
            //绑定创建者名称
            income.setCreateName(code);
            //设置记录状态
            income.setDelflag(0);
            incomeRestService.save(income);
            rb.setSucResult(ApiResultType.OK);
            return rb;
        } catch (Exception e) {
            logger.error(e.toString());
            rb.setFailMsg(ApiResultType.SERVER_ERROR);
            return rb;
        }
    }

    //从cache获取用户账本信息通用方法
    private String getUseAccountrCache(int userInfoId, String code) {
        String user_account = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user_account)) {
            UserAccountBookRestEntity task = incomeRestService.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            //设置redis缓存 缓存用户账本信息 30天
            String r_user_account = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code, r_user_account, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return r_user_account;
        }
        return user_account;
    }

    @RequestMapping(value = "/incomeToCharge", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean incomeToCharge( HttpServletRequest request,@RequestBody IncomeRestEntity income) {
        return this.incomeToCharge(null,request,income);
    }
}
