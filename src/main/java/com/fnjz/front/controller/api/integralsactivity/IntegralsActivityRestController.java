package com.fnjz.front.controller.api.integralsactivity;

import cn.jiguang.common.utils.StringUtils;
import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.integralsactivity.IntegralsActivityRestEntity;
import com.fnjz.front.entity.api.integralsactivityrange.IntegralsActivityRangeRestEntity;
import com.fnjz.front.service.api.integralsactivity.IntegralsActivityService;
import com.fnjz.front.service.api.userintegral.UserIntegralRestServiceI;
import com.fnjz.front.utils.RedisLockUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.core.util.StringUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * 积分活动相关
 * Created by yhang on 2019/1/9.
 */
@Controller
@RequestMapping(RedisPrefix.BASE_URL)
public class IntegralsActivityRestController {

    private static final Logger logger = Logger.getLogger(IntegralsActivityRestController.class);

    @Autowired
    private IntegralsActivityService integralsActivityService;

    @Autowired
    private RedisLockUtils redisLock;

    @Autowired
    private UserIntegralRestServiceI userIntegralRestServiceI;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    /**
     * 获取积分活动页头部记录播报
     * @return
     */
    @RequestMapping(value = {"/reportForIntegral", "/reportForIntegral/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean reportForIntegral() {
        try {
            return new ResultBean(ApiResultType.OK,integralsActivityService.reportForIntegral());
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取期数数据
     * @return
     */
    @RequestMapping(value = {"/getActivityInfo", "/getActivityInfo/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getActivityInfo() {
        try {
            return new ResultBean(ApiResultType.OK,integralsActivityService.getActivityInfo());
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取个人期数数据
     * @return
     */
    @RequestMapping(value = {"/getPersonalActivity", "/getPersonalActivity/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonalActivity(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            return new ResultBean(ApiResultType.OK,integralsActivityService.getPersonalActivity(userInfoId));
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 检查前推两期是否达标---->查看系统结果
     * @return
     */
    @RequestMapping(value = {"/checkActivityResult", "/checkActivityResult/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean checkActivityResult(HttpServletRequest request) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        try {
            //查看是否存在缓存
            String forString = redisTemplateUtils.getForString(RedisPrefix.PREFIX_INTEGRALS_ACTIVITY + userInfoId);
            if(StringUtils.isNotEmpty(forString)){
                return new ResultBean(ApiResultType.OK,null);
            }else{
                return new ResultBean(ApiResultType.OK,integralsActivityService.checkActivityResult(userInfoId));
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 获取我的记录中数据
     * @return
     */
    @RequestMapping(value = {"/getPersonalActivityInfo", "/getPersonalActivityInfo/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonalActivityInfo(HttpServletRequest request,@RequestParam(value="curPage",required = false) Integer curPage, @RequestParam(value="pageSize",required = false) Integer pageSize) {
        String userInfoId = (String) request.getAttribute("userInfoId");
        if(curPage!=null&&pageSize!=null){
            return new ResultBean(ApiResultType.OK,integralsActivityService.getPersonalActivityInfoForPage(userInfoId, curPage, pageSize));
        }else{
            try {
                return new ResultBean(ApiResultType.OK,integralsActivityService.getPersonalActivityInfo(userInfoId));
            } catch (Exception e) {
                logger.error(e.toString());
                return new ResultBean(ApiResultType.SERVER_ERROR, null);
            }
        }
    }

    /**
     * 获取积分参与范围
     * @return
     */
    @RequestMapping(value = {"/getIntegralActivityRange", "/getIntegralActivityRange/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getIntegralActivityRange() {
        try {
            return new ResultBean(ApiResultType.OK,integralsActivityService.getIntegralActivityRange());
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    /**
     * 报名接口
     * @return
     */
    @RequestMapping(value = {"/toSignup", "/toSignup/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultBean toSignup(HttpServletRequest request, @RequestBody Map<String,String> map) {
        if(StringUtil.isEmpty(map.get("iaId")) || StringUtil.isEmpty(map.get("iarId"))){
            return new ResultBean(ApiResultType.REQ_PARAMS_ERROR,null);
        }
        String userInfoId = (String) request.getAttribute("userInfoId");
        if (!redisLock.lock("integral:"+userInfoId+"_"+map.get("iaId"))) {
            return new ResultBean(ApiResultType.NOT_ALLOW_TO_EXCHANGE, null);
        }
        //获取用户参与的积分区间
        IntegralsActivityRangeRestEntity integralsActivityRangeRestEntity = integralsActivityService.getIntegralsActivityRangeById(map.get("iarId"));
        //获取期数信息
        IntegralsActivityRestEntity integralsActivityRestEntity = integralsActivityService.getIntegralsActivityById(map.get("iarId"));
        if(integralsActivityRestEntity!=null){
            if(integralsActivityRestEntity.getCreateDate()!=null){
                LocalDateTime localDateTime = LocalDateTime.ofInstant(integralsActivityRestEntity.getCreateDate().toInstant(), ZoneId.systemDefault()).withHour(23).withMinute(59).withSecond(59);
                //不允许报名
                if(LocalDateTime.now().isAfter(localDateTime)){
                    //释放锁
                    redisLock.unlock("integral:"+userInfoId+"_"+map.get("iaId"));
                    return new ResultBean(ApiResultType.ACTIVITY_IS_END, null);
                }
            }
        }
        //查看积分数是否达标
        double integralTotal = userIntegralRestServiceI.getUserTotalIntegral(userInfoId);
        //判断用户积分数
        if (integralTotal < integralsActivityRangeRestEntity.getIntegrals().doubleValue()) {
            //释放锁
            redisLock.unlock("integral:"+userInfoId+"_"+map.get("iaId"));
            return new ResultBean(ApiResultType.INTEGRAL_EXCHANGE_NOT_ALLOW, null);
        }
        try {
            integralsActivityService.toSignup(userInfoId,map.get("iaId"),integralsActivityRangeRestEntity.getIntegrals().doubleValue());
            //释放锁
            redisLock.unlock("integral:"+userInfoId+"_"+map.get("iaId"));
            return new ResultBean(ApiResultType.OK,null);
        } catch (Exception e) {
            logger.error(e.toString());
            //释放锁
            redisLock.unlock("integral:"+userInfoId+"_"+map.get("iaId"));
            return new ResultBean(ApiResultType.SERVER_ERROR, null);
        }
    }

    @Test
    public void run() {
        Date date = new Date(1547222400000L);//凌晨截止
        Date date2 = new Date(1547222399000L);//59分
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date2.toInstant(), zone);
        System.out.println(localDateTime2.isBefore(localDateTime));
    }
}
