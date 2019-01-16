package com.fnjz.front.service.impl.api.integralsactivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.IntegralsActivityRestDao;
import com.fnjz.front.dao.UserIntegralRestDao;
import com.fnjz.front.entity.api.PageRest;
import com.fnjz.front.entity.api.integralsactivity.IntegralsActivityRestEntity;
import com.fnjz.front.entity.api.integralsactivity.UserIntegralsActivityRestDTO;
import com.fnjz.front.entity.api.integralsactivity.UserIntegralsActivitySumRestDTO;
import com.fnjz.front.entity.api.integralsactivityrange.IntegralsActivityRangeRestEntity;
import com.fnjz.front.entity.api.message.MessageEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ReportShopRestDTO;
import com.fnjz.front.enums.AcquisitionModeEnum;
import com.fnjz.front.enums.CategoryOfBehaviorEnum;
import com.fnjz.front.service.api.integralsactivity.IntegralsActivityService;
import com.fnjz.front.service.api.message.MessageServiceI;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yhang on 2019/1/9.
 */
@Service("integralsActivityService")
@Transactional
public class IntegralsActivityServiceImpl extends CommonServiceImpl implements IntegralsActivityService {

    @Autowired
    private IntegralsActivityRestDao integralsActivityRestDao;

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private UserIntegralRestDao userIntegralRestDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private MessageServiceI messageService;

    //500积分=10元
    private static BigDecimal bigDecimal = new BigDecimal(0.02);

    @Override
    public List<ReportShopRestDTO> reportForIntegral() {
        String forString = redisTemplateUtils.getForString(RedisPrefix.PREFIX_HEAD_REPORT + "integral");
        if (StringUtils.isNotEmpty(forString)) {
            List list = JSON.parseObject(forString, List.class);
            return list;
        } else {
            List<ReportShopRestDTO> reportShopRestDTOS = integralsActivityRestDao.reportForIntegral();
            reportShopRestDTOS.forEach(v -> {
                BigDecimal value =new BigDecimal(v.getValue() + "");
                BigDecimal multiply = value.multiply(bigDecimal);
                //保留两位
                v.setValue2(multiply.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
            });
            if(reportShopRestDTOS!=null){
                if(reportShopRestDTOS.size()>0){
                    redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_HEAD_REPORT + "integral", JSONObject.toJSONString(reportShopRestDTOS), RedisPrefix.SESSION_KEY_TIME, TimeUnit.DAYS);
                }
            }
            return reportShopRestDTOS;
        }
    }

    @Override
    public IntegralsActivityRestEntity getActivityInfo() {
        LocalDate now = LocalDate.now();
        //当前期 数据
        IntegralsActivityRestEntity activityInfo = integralsActivityRestDao.getActivityInfo(now.toString());
        if(activityInfo!=null){
            if(activityInfo.getTotalIntegrals()!=null){
                BigDecimal multiply = activityInfo.getTotalIntegrals().multiply(bigDecimal);
                //保留两位
                activityInfo.setMoney(multiply.setScale(2,BigDecimal.ROUND_HALF_UP));
            }
        }else{
            activityInfo = new IntegralsActivityRestEntity();
        }
        //上期 数据
        IntegralsActivityRestEntity activityInfo2 = integralsActivityRestDao.getLastActivityInfo(now.minusDays(1).toString());
        if(activityInfo2!=null){
            activityInfo.setFalseFailUsers(activityInfo2.getFalseFailUsers());
            activityInfo.setFalseSuccessUsers(activityInfo2.getFalseSuccessUsers());
            activityInfo.setFalseTotalUsers(activityInfo2.getFalseTotalUsers());
        }
        return activityInfo;
    }

    @Override
    public List<UserIntegralsActivityRestDTO> getPersonalActivity(String userInfoId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime after = localDateTime.withHour(23).withMinute(59).withSecond(59);
        LocalDateTime before = localDateTime.minusDays(2).withHour(0).withMinute(0).withSecond(0);
        List<UserIntegralsActivityRestDTO> personalActivityInfo = integralsActivityRestDao.getPersonalActivity(userInfoId, before.toString(), after.toString());
        if (personalActivityInfo != null) {
            if (personalActivityInfo.size() > 0) {
                personalActivityInfo.forEach(v -> {
                    if (v.getStatus() == 1) {
                        v.setDate(v.getCreateDate());
                    } else if (v.getStatus() == 2) {
                        v.setDate(v.getChargeDate());
                    } else if (v.getStatus() == 3) {
                        v.setDate(v.getEndDate());
                    } else if (v.getStatus() == 4) {
                        v.setDate(v.getEndDate());
                    }
                });
            }
        }
        return personalActivityInfo;
    }

    @Override
    public UserIntegralsActivitySumRestDTO getPersonalActivityInfo(String userInfoId) {
        return integralsActivityRestDao.getPersonalActivityInfo(userInfoId);
    }

    @Override
    public IntegralsActivityRangeRestEntity getIntegralsActivityRangeById(String iarId) {
        return integralsActivityRestDao.getIntegralsActivityRangeById(iarId);
    }

    private static String msg = "您已成功参加今天的记账挑战活动,记得明天来记账哦~";
    @Override
    public void toSignup(String userInfoId, String iaId, double integral) {
        //录入 参与记录
        integralsActivityRestDao.insertUserIntegralActivity(userInfoId,iaId,integral);
        //更新期数记录
        integralsActivityRestDao.updateIntegralActivityForTotalUsers(iaId,integral);
        //扣除积分
        userIntegralRestDao.insertSignInIntegral(userInfoId, iaId, null, AcquisitionModeEnum.USER_INTEGRAL_ACTIVITY.getDescription(), AcquisitionModeEnum.USER_INTEGRAL_ACTIVITY.getIndex(), CategoryOfBehaviorEnum.INTEGRALS_ACTIVITY.getIndex(), Double.parseDouble("-"+integral));
        userIntegralRestDao.updateForTotalIntegral(userInfoId, null, new BigDecimal("-"+integral));
        taskExecutor.execute(()->{
            int userInfoId2 = Integer.valueOf(userInfoId);
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUserInfoId(userInfoId2);
            messageEntity.setContent(msg);
            messageEntity.setCreateBy(userInfoId2);
            messageEntity.setStatus(2);
            integralsActivityRestDao.insertMessage(messageEntity);
        });
    }

    @Override
    public List<IntegralsActivityRangeRestEntity> getIntegralActivityRange() {
        return integralsActivityRestDao.getIntegralActivityRange();
    }

    @Override
    public Object getPersonalActivityInfoForPage(String userInfoId, Integer curPage, Integer pageSize) {
        PageRest pageRest = new PageRest();
        pageRest.setPageSize(pageSize);
        pageRest.setCurPage(curPage);
        List<UserIntegralsActivityRestDTO> listForPage = integralsActivityRestDao.getPersonalActivityInfoForPage(userInfoId,pageRest.getStartIndex(),pageRest.getPageSize());
        //获取总条数
        Integer count = integralsActivityRestDao.getCountForUserIntegrals(userInfoId);
        //设置总记录数
        pageRest.setTotalCount(count);
        //设置返回结果
        pageRest.setContent(listForPage);
        return pageRest;
    }

    @Override
    public void chargeToIntegralsActivity(String userInfoId) {
        //判断用户是否参加(查看当前天往前推一天是否存在参与活动记录)---->判断用户是否达标
        LocalDate time = LocalDate.now().minusDays(1);
        Map<String,Integer> map = integralsActivityRestDao.checkSignUpByUserInfoIdAndTime(userInfoId, time.toString());
        if(map!=null){
            if(map.get("status")!=null){
                //已参赛-----> 判断当前状态
                if(map.get("status")==1){
                    //1----->已报名，未记账---->执行更新流程
                    integralsActivityRestDao.updateUserIntegralActivityForChangeDate(map.get("id"));
                    integralsActivityRestDao.updateIntegralActivityForCharge(map.get("iaid"));
                }
            }
        }
    }

    @Override
    public IntegralsActivityRestEntity getIntegralsActivityById(String iaId) {
        return integralsActivityRestDao.getIntegralsActivityById(iaId);
    }

    private static String success = "success";
    /**
     * 检查前推两期是否达标---->查看系统结果
     * @param userInfoId
     * @return
     */
    @Override
    public IntegralsActivityRestEntity checkActivityResult(String userInfoId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate time = now.toLocalDate().minusDays(2);
        IntegralsActivityRestEntity integralsActivityRestEntity = integralsActivityRestDao.checkActivityResult(userInfoId, time.toString());
        if(integralsActivityRestEntity!=null){
            if(integralsActivityRestEntity.getId()!=null){
                //读取一次
                LocalDateTime time2 = LocalDate.now().atTime(23, 59, 59);
                //凌晨时间戳-当前时间戳
                long cacheTime = time2.toEpochSecond(ZoneOffset.of("+8")) - now.toEpochSecond(ZoneOffset.of("+8"));
                redisTemplateUtils.cacheForString(RedisPrefix.PREFIX_INTEGRALS_ACTIVITY + userInfoId, success, cacheTime, TimeUnit.SECONDS);
                return integralsActivityRestEntity;
            }
        }
        return null;
    }

    @Override
    public boolean checkUserSignup(String userInfoId, String iaId) {
        Integer status = integralsActivityRestDao.checkUserSignup(userInfoId,iaId);
        return status==null?true:(status==0?true:false);
    }

    @Test
    public void run() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime after = localDateTime.withHour(23).withMinute(59).withSecond(59);
        LocalDateTime before = localDateTime.minusDays(2).withHour(0).withMinute(0).withSecond(0);
        System.out.println(before.toString());
        System.out.println(after.toString());
        BigDecimal bigDecimal = new BigDecimal(6.00235);
        System.out.println(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
