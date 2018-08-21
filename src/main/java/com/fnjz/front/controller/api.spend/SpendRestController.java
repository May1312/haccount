package com.fnjz.front.controller.api.spend;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.spend.SpendRestEntity;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.spend.SpendRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.ValidateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 账本-支出表相关   废弃api 走流水接口
 * @date 2018-06-06 11:59:47
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class SpendRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(SpendRestController.class);

    @Autowired
    private SpendRestServiceI spendRestService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 支出记账功能
     *
     * @return
     */
    @ApiOperation(value = "支出记账")
    @RequestMapping(value = "/spendToCharge/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean chargeToAccound(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody SpendRestEntity spend) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        //校验记账时间
        if (spend.getSpendDate() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_SPENDDATE_ERROR);
            return rb;
        }
        //校验金额
        if (spend.getSpendMoney() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_IS_NULL);
            return rb;
        }
        if (!ValidateUtils.checkDecimal(spend.getSpendMoney() + "")) {
            rb.setFailMsg(ApiResultType.ACCOUNT_MONEY_ERROR);
            return rb;
        }
        //校验二三级类目 id
        if (StringUtils.isEmpty(spend.getSpendTypePid())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }
        if (StringUtils.isEmpty(spend.getSpendTypeId())) {
            rb.setFailMsg(ApiResultType.ACCOUNT_PARAMS_ERROR);
            return rb;
        }
        //判断新增类型
        if (spend.getIsStaged() == null) {
            rb.setFailMsg(ApiResultType.ACCOUNT_TYPE_ERROR);
            return rb;
        }
        //1 为即时记账类型    2 为分期记账类型
        if (spend.getIsStaged() == 1) {
            try {
                String code = (String) request.getAttribute("code");
                String key = (String) request.getAttribute("key");
                String userInfoId = (String) request.getAttribute("userInfoId");
                String useAccountrCache = getUseAccountCache(Integer.valueOf(userInfoId), key);
                UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
                //获取到账本id 插入记录 TODO 当前账本为1，后台可以获取，后期 账本为多个时，需要传入指定的账本id
                //设置单笔记录号
                spend.setSpendOrder(CommonUtils.getAccountOrder());
                //设置创建时间
                spend.setCreateDate(new Date());
                //绑定账本id
                spend.setAccountBookId(userLoginRestEntity.getAccountBookId());
                //绑定创建者id
                spend.setCreateBy(userLoginRestEntity.getUserInfoId());
                //绑定创建者名称
                spend.setCreateName(code);
                //设置记录状态
                spend.setDelflag(0);
                spendRestService.save(spend);
                rb.setSucResult(ApiResultType.OK);
                return rb;
            } catch (Exception e) {
                logger.error(e.toString());
                rb.setFailMsg(ApiResultType.SERVER_ERROR);
                return rb;
            }
        } else {
            Map map = new HashMap<>();
            map.put("msg", "分期功能未开放");
            rb.setResult(map);
            return rb;
        }
    }

    @ApiOperation(value = "获取支出分页列表")
    @RequestMapping(value = "/getSpendList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getSpendList(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,
                                   HttpServletRequest request, @RequestParam String curPage,@RequestParam String pageSize) {
        System.out.println("登录终端：" + type);
        ResultBean rb = new ResultBean();
        try {
            String code = (String) request.getAttribute("code");
            String key = (String) request.getAttribute("key");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = getUseAccountCache(Integer.valueOf(userInfoId), key);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            SpendRestEntity se = new SpendRestEntity();
            PageRest page = spendRestService.findListForPage(userLoginRestEntity.getAccountBookId()+"",Integer.valueOf(curPage),Integer.valueOf(pageSize));
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


    //从cache获取用户账本信息通用方法
    private String getUseAccountCache(int userInfoId, String code) {
        String user_account = (String) redisTemplate.opsForValue().get(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code);
        //为null 重新获取缓存
        if (StringUtils.isEmpty(user_account)) {
            UserAccountBookRestEntity task = spendRestService.findUniqueByProperty(UserAccountBookRestEntity.class, "userInfoId", userInfoId);
            //设置redis缓存 缓存用户账本信息 30天
            String r_user_account = JSON.toJSONString(task);
            redisTemplate.opsForValue().set(RedisPrefix.PREFIX_USER_ACCOUNT_BOOK + code, r_user_account, RedisPrefix.USER_VALID_TIME, TimeUnit.DAYS);
            return r_user_account;
        }
        return user_account;
    }

    @RequestMapping(value = "/spendToCharge", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean chargeToAccound(HttpServletRequest request, @RequestBody SpendRestEntity spend) {
        return this.chargeToAccound(null, request, spend);
    }
}
