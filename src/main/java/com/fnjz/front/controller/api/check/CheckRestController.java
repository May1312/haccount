package com.fnjz.front.controller.api.check;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.controller.api.common.ClockInDays;
import com.fnjz.front.entity.api.check.LabelVersionRestDTO;
import com.fnjz.front.entity.api.check.SystemParamCheckRestDTO;
import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import com.fnjz.front.service.api.checkrest.CheckRestServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @version V1.0
 * @Title: Controller
 * @Description: app启动检查类目更新, 返回同步时间
 * @date 2018-06-26 13:11:13
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class CheckRestController extends BaseController {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(CheckRestController.class);

    @Autowired
    private CheckRestServiceI checkRestServiceI;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private ClockInDays clockInDays;

    /**
     * 入参 系统支出/收入表最后更新时间/个人常用支出/收入表最后更新时间/个人排序关系表最后更新时间
     *
     * @param type
     * @param systemParamCheckRestDTO
     * @return
     */
    @RequestMapping(value = "/checkSystemParam/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkSystemParam(@PathVariable("type") String type, HttpServletRequest request, @RequestBody SystemParamCheckRestDTO systemParamCheckRestDTO) {
        System.out.println("登录终端：" + type);
        Map<String, Object> map;
        //判断是否包含token
        Object containsToken = request.getAttribute("containsToken");
        if (null != containsToken) {
            boolean flag = Boolean.valueOf(containsToken + "");
            if (!flag) {
                //只查询系统类目信息
                if (StringUtils.isEmpty(systemParamCheckRestDTO.getSysSpendTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getSysIncomeTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommUseSpendTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommUseIncomeTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommTypePriorityVersion())) {
                    try {
                        //不查询个人相关
                        map = checkRestServiceI.getSysAndUserSpendAndSynInterval(null, null);
                    } catch (Exception e) {
                        logger.error(e.toString());
                        return new ResultBean(ApiResultType.SERVER_ERROR, null);
                    }
                    return new ResultBean(ApiResultType.OK, map);
                }
                //正常
                try {
                    map = checkRestServiceI.checkParamVersion2(null, systemParamCheckRestDTO, null, null);
                    return new ResultBean(ApiResultType.OK, map);
                } catch (Exception e) {
                    logger.error(e.toString());
                    return new ResultBean(ApiResultType.SERVER_ERROR, null);
                }
            }
        }
        //正常流程执行
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
        //连续打卡统计
        clockInDays.clockInDays(shareCode);
        if (StringUtils.isEmpty(systemParamCheckRestDTO.getSysSpendTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getSysIncomeTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommUseSpendTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommUseIncomeTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommTypePriorityVersion()) || StringUtils.isNotEmpty(systemParamCheckRestDTO.getSysSpendTypeVersion()) && StringUtils.isNotEmpty(systemParamCheckRestDTO.getSysIncomeTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommUseSpendTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommUseIncomeTypeVersion()) && StringUtils.isEmpty(systemParamCheckRestDTO.getUserCommTypePriorityVersion())) {
            //返回所有
            try {
                map = checkRestServiceI.getSysAndUserSpendAndSynInterval2(shareCode, userInfoId, userAccountBookRestEntityCache.getAccountBookId() + "");
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
            return new ResultBean(ApiResultType.OK, map);
        }
        try {
            map = checkRestServiceI.checkParamVersion2(shareCode, systemParamCheckRestDTO, userAccountBookRestEntityCache.getAccountBookId() + "", userInfoId);
            return new ResultBean(ApiResultType.OK, map);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 多账本唤起检查
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkSystemParamv2/{type}", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkSystemParamv2(@PathVariable("type") String type, HttpServletRequest request, @RequestBody LabelVersionRestDTO labelVersion) {
        System.out.println("登录终端：" + type);
        Map<String, Object> map;
        //正常流程执行
        String userInfoId = (String) request.getAttribute("userInfoId");
        String shareCode = (String) request.getAttribute("shareCode");
        UserAccountBookRestEntity userAccountBookRestEntityCache = redisTemplateUtils.getUserAccountBookRestEntityCache(Integer.valueOf(userInfoId), shareCode);
        //连续打卡统计
        clockInDays.clockInDays(shareCode);
            //返回所有
            try {
                map = checkRestServiceI.getUserPrivateLabelAndSynInterval(shareCode, userInfoId, userAccountBookRestEntityCache.getAccountBookId() + "",labelVersion);
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
            return new ResultBean(ApiResultType.OK, map);
    }

    @RequestMapping(value = "/checkSystemParam", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkSystemParam(HttpServletRequest request, @RequestBody SystemParamCheckRestDTO systemParamCheckRestDTO) {
        return this.checkSystemParam(null, request, systemParamCheckRestDTO);
    }

    @RequestMapping(value = "/checkSystemParamv2", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean checkSystemParamv2(HttpServletRequest request, @RequestBody LabelVersionRestDTO labelVersion) {
        return this.checkSystemParamv2(null, request, labelVersion);
    }
}
