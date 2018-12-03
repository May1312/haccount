package com.fnjz.front.timer;

import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletMessageTempRestEntity;
import com.fnjz.front.service.api.wxappletmessagetemp.WXAppletMessageTempService;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletPushUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.locks.StampedLock;

/**
 * 账单通知定时任务
 * Created by yhang on 2018/11/29.
 */
@Service("accountNotifyTimer")
public class AccountNotifyTimer implements Job {

    private static final Logger logger = Logger.getLogger(AccountNotifyTimer.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private WXAppletPushUtils wxAppletPushUtils;

    @Autowired
    private WarterOrderRestDao warterOrderRestDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private WXAppletMessageTempService wxAppletMessageTempService;

    /**
     * 锁
     */
    private final StampedLock lock = new StampedLock();

    /**
     * 线程数定义
     */
    private int threadNum = 5;

    public void accountNotify() {
        LocalDateTime localDateTime = LocalDateTime.now();
        //读取用户id--->对应openId  所有可以推送通知的用户
        Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID + "*");
        LocalDateTime localDateTime2 = LocalDateTime.now();
        logger.info("==========读取到推送用户id set集合耗时:" + (localDateTime2.toInstant(ZoneOffset.of("+8")).toEpochMilli() - localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
        LocalDate date = LocalDate.now();
        date = date.minusMonths(1);
        LocalDate first = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy年MM月");
        String time = date.format(formatters);
        //当需要推送的用户数大于100   启用多线程
        if (keys.size() > 100) {
            LocalDateTime localDateTime3 = LocalDateTime.now();
            Object[] obj = keys.toArray();
            logger.info("==========set集合转list集合耗时:" + (LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - localDateTime3.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
            keys = null;
            //获取每个线程处理的数据长度
            int len = obj.length/threadNum;
            //创建线程
            for (int i = 0; i < threadNum; i++) {
                taskExecutor.execute(() -> {
                    List<WXAppletMessageTempRestEntity> list = new ArrayList<>(len);
                    for (int j = (obj.length-1); j > (obj.length-len); j--) {
                        //获取openId
                        String openId = redisTemplateUtils.getForString(obj[j]+"");
                        //获取一条有效formId
                        Set keys1 = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
                        Object[] obj2 = keys1.toArray();
                        //倒序
                        Arrays.sort(obj2,Collections.reverseOrder());
                        //获取最新日期一条formId
                        String formId = (String) redisTemplateUtils.popListRight(obj2[0]+"");
                        //封装数据
                        WXAppletMessageTempRestEntity bean = new WXAppletMessageTempRestEntity(Integer.valueOf(StringUtils.substringAfterLast(obj[j]+"",":")),openId,formId);
                        list.add(bean);
                    }
                    //插入mysql
                    wxAppletMessageTempService.foreachInsert(list);
                });
            }
        } else {
            for (Object userInfoId : keys) {
                getFormId(userInfoId + "", first.toString(), end.toString(), time);
            }
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        accountNotify();
    }

    /**
     * 获取月统计数据
     */
    private Map<String, BigDecimal> getMonthStatistics(String userInfoId, String first, String end) {
        return warterOrderRestDao.getAccountForAll(first, end, userInfoId);
    }

    /**
     * 封装message数据---->发送服务通知
     */
    private void wxappletPush(String templateId, String openId, String formId, String time, Map<String, BigDecimal> map) {
        WXAppletMessageBean bean = new WXAppletMessageBean();
        //设置账单名称
        bean.getKeyword1().put("value", time + "账单统计");
        //设置本月支出
        bean.getKeyword2().put("value", map.get("spend") + "元");
        //设置本月收入
        bean.getKeyword3().put("value", map.get("income") + "元");
        //设置备注
        bean.getKeyword4().put("value", "越记账越清晰，记账就能领现金→");
        //设置账单详情
        bean.getKeyword5().put("value", "想了解本月支出排行榜第1位？请点击~");
        wxAppletPushUtils.wxappletPush(templateId, openId, formId, WXAppletPushUtils.accountNotifyPage, bean);
    }

    private void getFormId(String userInfoId, String first, String end, String time) {
        //统计此用户总账本月支出情况
        LocalDateTime localDateTime = LocalDateTime.now();
        Map<String, BigDecimal> monthStatistics = getMonthStatistics(StringUtils.substringAfterLast(userInfoId + "", ":"), first, end);
        logger.info("==========统计一条用户月开销耗时:" + (LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
        LocalDateTime localDateTime2 = LocalDateTime.now();
        String openId = redisTemplateUtils.getForString(userInfoId + "");
        logger.info("==========根据userinfoid 获取openId开销耗时:" + (LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - localDateTime2.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
        //根据openId获取一条有效formid
        LocalDateTime localDateTime3 = LocalDateTime.now();
        Set keys2 = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
        if (keys2.size() > 0) {
            Iterator<String> it = keys2.iterator();
            String key = it.next();
            String formId = redisTemplateUtils.getForString(key);
            logger.info("==========获取一条formId开销耗时::" + (LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
            wxappletPush(WXAppletPushUtils.accountNotifyId, openId, formId, time, monthStatistics);
        }
    }
}
