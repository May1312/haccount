package com.fnjz.front.controller.api.warterorder;

import com.alibaba.fastjson.JSON;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.controller.api.common.ClockInDays;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.entity.api.warterorder.WXAppletWarterOrderRestInfoDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestDTO;
import com.fnjz.front.entity.api.warterorder.WarterOrderRestNewLabel;
import com.fnjz.front.service.api.warterorder.WarterOrderRestServiceI;
import com.fnjz.front.utils.CommonUtils;
import com.fnjz.front.utils.ParamValidateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: 账本流水表相关
 * @date 2018-06-14 13:15:47
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class WarterOrderRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(WarterOrderRestController.class);

    @Autowired
    private WarterOrderRestServiceI warterOrderRestService;
    @Autowired
    private RedisTemplateUtils redisTemplateUtils;
    @Autowired
    private ClockInDays clockInDays;

    /**
     * 账本流水表相关列表 页面跳转
     *
     * @return
     */
    @ApiOperation(value = "记账功能")
    @RequestMapping(value = "/toCharge/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toCharge(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, HttpServletRequest request, @RequestBody WarterOrderRestNewLabel charge) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkToCharge(charge);
        if (rb != null) {
            return rb;
        }
        String code = (String) request.getAttribute("code");
        String shareCode = (String) request.getAttribute("shareCode");
        String userInfoId = (String) request.getAttribute("userInfoId");
        String useAccountrCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
        UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
        //获取到账本id 插入记录 TODO 当前账本为1，后台可以获取，后期 账本为多个时，需要传入指定的账本id

        //1 为即时记账类型    2 为分期记账类型
        if (charge.getOrderType() == 1 && charge.getIsStaged() == 1) {
            //使用度必须为空
            if (charge.getUseDegree() != null) {
                charge.setUseDegree(null);
            }
            //设置创建时间
            charge.setCreateDate(new Date());
            //设置更新时间
            charge.setUpdateDate(new Date());
            //绑定账本id
            charge.setAccountBookId(userLoginRestEntity.getAccountBookId());
            //绑定创建者id
            charge.setCreateBy(userLoginRestEntity.getUserInfoId());
            //绑定创建者名称
            charge.setCreateName(code);
            //设置记录状态
            charge.setDelflag(0);
            charge.setId(CommonUtils.getAccountOrder());
            warterOrderRestService.insert(charge, code, userLoginRestEntity.getAccountBookId());
            //打卡统计
            myCount(shareCode, userLoginRestEntity);
            return CommonUtils.returnCharge(charge.getId());
        } else if (charge.getOrderType() == 1 && charge.getIsStaged() == 2) {
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
        //转义emoji表情
        if (StringUtils.isNotEmpty(charge.getRemark())) {
            //charge.setRemark(EmojiUtils.emojiToAlias(charge.getRemark()));
            charge.setRemark(charge.getRemark());
        }
        charge.setId(CommonUtils.getAccountOrder());
        try {
            warterOrderRestService.insert(charge, code, userLoginRestEntity.getAccountBookId());
            //打卡统计
            myCount(shareCode, userLoginRestEntity);
            //返回记账id
            return CommonUtils.returnCharge(charge.getId());
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "记账功能")
    @RequestMapping(value = "/toChargev2/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toChargev2(@PathVariable("type") String type, HttpServletRequest request, @RequestBody WarterOrderRestNewLabel charge) {
        System.out.println("登录终端：" + type);
        ResultBean rb = ParamValidateUtils.checkToCharge(charge);
        if (rb != null) {
            return rb;
        }
        String code = (String) request.getAttribute("code");
        String shareCode = (String) request.getAttribute("shareCode");
        String userInfoId = (String) request.getAttribute("userInfoId");
        //1 为即时记账类型    2 为分期记账类型
        if (charge.getOrderType() == 1 && charge.getIsStaged() == 1) {
            //使用度必须为空
            if (charge.getUseDegree() != null) {
                charge.setUseDegree(null);
            }
            //设置创建时间
            charge.setCreateDate(new Date());
            //设置更新时间
            charge.setUpdateDate(new Date());
            //绑定创建者id
            charge.setCreateBy(Integer.valueOf(userInfoId));
            //初始修改者id
            charge.setUpdateBy(Integer.valueOf(userInfoId));
            //绑定创建者名称
            charge.setCreateName(code);
            //设置记录状态
            charge.setDelflag(0);
            charge.setId(CommonUtils.getAccountOrder());
            warterOrderRestService.insertv2(charge);
            //打卡统计
            myCount(shareCode, null);
            return CommonUtils.returnCharge(charge.getId());
        } else if (charge.getOrderType() == 1 && charge.getIsStaged() == 2) {
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
        //绑定创建者id
        charge.setCreateBy(Integer.valueOf(userInfoId));
        //初始修改者id
        charge.setUpdateBy(Integer.valueOf(userInfoId));
        //绑定创建者名称
        charge.setCreateName(code);
        //设置记录状态
        charge.setDelflag(0);
        charge.setId(CommonUtils.getAccountOrder());
        try {
            warterOrderRestService.insertv2(charge);
            //打卡统计
            myCount(shareCode, null);
            //返回记账id
            return CommonUtils.returnCharge(null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 返回一个月数据,重置用户 账本缓存
     *
     * @param type
     * @param request
     * @param year
     * @param month
     * @return
     */
    @ApiOperation(value = "获取流水分页列表")
    @RequestMapping(value = "/warterOrderList/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean warterOrderList(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,
                                      HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "curPage", required = false) Integer curPage, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        System.out.println("登录终端：" + type);
        logger.info("获取流水分页列表接口: year-->" + year + "  month-->" + month);
        String time = ParamValidateUtils.getTime(year, month);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            //连续打卡统计
            clockInDays.clockInDays(shareCode);
            Map<String, Object> json = warterOrderRestService.findListForPage(time, userLoginRestEntity.getAccountBookId() + "", curPage, pageSize);
            return new ResultBean(ApiResultType.OK, json);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "获取流水分页列表")
    @RequestMapping(value = "/warterOrderListv2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean warterOrderListv2(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,
                                        HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "curPage", required = false) Integer curPage, @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestParam(value = "abId", required = false) Integer abId) {
        System.out.println("登录终端：" + type);
        logger.info("获取流水分页列表接口: year-->" + year + "  month-->" + month);
        String time = ParamValidateUtils.getTime(year, month);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            //连续打卡统计
            clockInDays.clockInDays(shareCode);
            Map<String, Object> json = warterOrderRestService.findListForPagev2(time, userLoginRestEntity.getAccountBookId() + "", curPage, pageSize, abId, userInfoId);
            return new ResultBean(ApiResultType.OK, json);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "根据单笔记账号获取订单详情")
    @RequestMapping(value = "/getOrderInfo/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getOrderInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestParam String id) {
        System.out.println("登录终端：" + type);
        if (StringUtils.isEmpty(id)) {
            return new ResultBean(ApiResultType.ORDER_ID_IS_NULL, null);
        }
        try {
            //获取单笔详情   TODO 现阶段只根据详情id， 后续要加上userid   account book id 判断！！
            WarterOrderRestDTO task = warterOrderRestService.findById(id);
            if (task != null) {
                //转义表情
                if (StringUtils.isNotEmpty(task.getRemark())) {
                    //task.setRemark(EmojiUtils.aliasToEmoji(task.getRemark()));
                    task.setRemark(task.getRemark());
                }
                return new ResultBean(ApiResultType.OK, task);
            }
            return new ResultBean(ApiResultType.GET_ORDER_ERROR, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 流水详情获取   多账本版
     *
     * @param type
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOrderInfov2/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getOrderInfov2(@PathVariable("type") String type, @RequestParam(required = false) String id, @RequestParam(required = false) Integer memberFlag) {
        System.out.println("登录终端：" + type);
        if (StringUtils.isEmpty(id)) {
            return new ResultBean(ApiResultType.ORDER_ID_IS_NULL, null);
        }
        try {
            WXAppletWarterOrderRestInfoDTO task = warterOrderRestService.findByIdv2(id, memberFlag);
            return new ResultBean(ApiResultType.OK, task);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "修改单笔记账订单详情")
    @RequestMapping(value = "/updateOrderInfo/{type}", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateOrderInfo(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody WarterOrderRestNewLabel charge, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        //判断单笔记账id
        if (charge.getId() == null) {
            return new ResultBean(ApiResultType.ORDER_ID_IS_NULL, null);
        }
        String code = (String) request.getAttribute("code");
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (charge.getOrderType() != null) {
            if (charge.getOrderType() == 1) {
                //使用度必须为空
                if (charge.getUseDegree() != null) {
                    charge.setUseDegree(null);
                }
            } else if (charge.getOrderType() == 2) {
                //愉悦度必须为空
                if (charge.getSpendHappiness() != null) {
                    charge.setSpendHappiness(null);
                }
                if (charge.getIsStaged() != null) {
                    charge.setIsStaged(null);
                }
            }
        }
        //设置创建时间
        charge.setUpdateDate(new Date());
        //绑定修改者id
        charge.setUpdateBy(Integer.valueOf(userInfoId));
        //绑定修改者名称
        charge.setUpdateName(code);
        //设置记录状态
        try {
            warterOrderRestService.update(charge);
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "删除单笔记账订单详情")
    @RequestMapping(value = "/deleteOrder/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteOrder(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type, @RequestBody @ApiIgnore Map<String, String> map, HttpServletRequest request) {
        System.out.println("登录终端：" + type);
        if (StringUtils.isEmpty(map.get("id"))) {
            return new ResultBean(ApiResultType.ORDER_ID_IS_NULL, null);
        }
        try {
            //获取当前用户信息
            String userInfoId = (String) request.getAttribute("userInfoId");
            String shareCode = (String) request.getAttribute("shareCode");
            String code = (String) request.getAttribute("code");
            //执行更新
            int i = warterOrderRestService.deleteOrder(map.get("id"), userInfoId, code);
            if (i < 1) {
                return new ResultBean(ApiResultType.DELETE_RECORD_ERROR, null);
            }
            //统计记账总笔数-1
            Map s = redisTemplateUtils.getMyCount(shareCode);
            if (s.size() > 0) {
                if (s.containsKey("chargeTotal")) {
                    //统计记账总笔数
                    redisTemplateUtils.incrementMyCountTotal(shareCode, "chargeTotal", 2);
                }
            } //为空不处理
            return new ResultBean(ApiResultType.OK, null);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "获取年份月份对应支出收入统计")
    @RequestMapping(value = "/getAccountByTime/{type}", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getAccountByTime(@ApiParam(value = "可选  ios/android/wxapplet") @PathVariable("type") String type,
                                       HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month) {
        System.out.println("登录终端：" + type);
        logger.info("获取年份月份对应支出收入统计: year-->" + year + "  month-->" + month);
        String time = ParamValidateUtils.getTime(year, month);
        try {
            String shareCode = (String) request.getAttribute("shareCode");
            String userInfoId = (String) request.getAttribute("userInfoId");
            String useAccountrCache = redisTemplateUtils.getUseAccountCache(Integer.valueOf(userInfoId), shareCode);
            UserAccountBookRestEntity userLoginRestEntity = JSON.parseObject(useAccountrCache, UserAccountBookRestEntity.class);
            Map<String, BigDecimal> map = warterOrderRestService.getAccount(time, userLoginRestEntity.getAccountBookId() + "");
            return new ResultBean(ApiResultType.OK, map);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 记账笔数统计公用方法
     */
    private void myCount(String shareCode, UserAccountBookRestEntity userAccountBookRestEntity) {
        //统计记账总笔数+1
        Map s = redisTemplateUtils.getMyCount(shareCode);
        if (s.size() > 0) {
            if (s.containsKey("chargeTotal")) {
                //直接递增
                redisTemplateUtils.incrementMyCountTotal(shareCode, "chargeTotal", 1);
            }//else {
            //为空情况 TODO 存在高并发记账不准确情况 不做缓存修改
            //统计记账总笔数
            //int chargeTotal = warterOrderRestService.chargeTotal(userAccountBookRestEntity.getAccountBookId());
            //myCountRestDTO.setChargeTotal(chargeTotal);
            //s.put("chargeTotal",chargeTotal+"");
            //重新设置redis
            //redisTemplateUtils.updateMyCount(shareCode, s);
        }
    }

    @RequestMapping(value = "/toCharge", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toCharge(HttpServletRequest request, @RequestBody WarterOrderRestNewLabel charge) {
        return this.toCharge(null, request, charge);
    }

    @RequestMapping(value = "/toChargev2", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toChargev2(HttpServletRequest request, @RequestBody WarterOrderRestNewLabel charge) {
        return this.toChargev2(null, request, charge);
    }

    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getOrderInfo(@RequestParam String id) {
        return this.getOrderInfo(null, id);
    }

    @RequestMapping(value = "/getOrderInfov2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getOrderInfov2(@RequestParam(required = false) String id, @RequestParam(required = false) Integer memberFlag) {
        return this.getOrderInfov2(null, id, memberFlag);
    }

    @RequestMapping(value = "/warterOrderList", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean warterOrderList(HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "curPage", required = false) Integer curPage, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return this.warterOrderList(null, request, year, month, curPage, pageSize);
    }

    @RequestMapping(value = "/warterOrderListv2", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean warterOrderListv2(HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month, @RequestParam(value = "curPage", required = false) Integer curPage, @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestParam(value = "abId", required = false) Integer abId) {
        return this.warterOrderListv2(null, request, year, month, curPage, pageSize, abId);
    }

    @RequestMapping(value = "/updateOrderInfo", method = RequestMethod.PUT)
    @ResponseBody
    public ResultBean updateOrderInfo(@RequestBody WarterOrderRestNewLabel charge, HttpServletRequest request) {
        return this.updateOrderInfo(null, charge, request);
    }

    @RequestMapping(value = "/deleteOrder", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultBean deleteOrder(@RequestBody Map<String, String> map, HttpServletRequest request) {
        return this.deleteOrder(null, map, request);
    }

    @RequestMapping(value = "/getAccountByTime", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getAccountByTime(HttpServletRequest request, @RequestParam(value = "year", required = false) String year, @RequestParam(value = "month", required = false) String month) {
        return this.getAccountByTime(null, request, year, month);
    }
}
