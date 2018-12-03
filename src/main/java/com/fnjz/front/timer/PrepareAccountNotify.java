package com.fnjz.front.timer;

import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletMessageTempRestEntity;
import com.fnjz.front.service.api.wxappletmessagetemp.WXAppletMessageTempService;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.StampedLock;

/**
 * 账单通知  数据整合  定时任务
 * Created by yhang on 2018/11/29.
 */
@Service("prepareAccountNotify")
public class PrepareAccountNotify implements Job {

    private static final Logger logger = Logger.getLogger(PrepareAccountNotify.class);

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

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
            int len = obj.length / threadNum;
            CountDownLatch countDownLatch = new CountDownLatch(threadNum);
            //创建线程
            for (int i = 0; i < threadNum; i++) {
                taskExecutor.execute(() -> {
                    List<WXAppletMessageTempRestEntity> list = new ArrayList<>(len);
                    for (int j = (obj.length - 1); j > (obj.length - len); j--) {
                        //获取openId
                        String openId = redisTemplateUtils.getForString(obj[j] + "");
                        //获取一条有效formId
                        Set keys1 = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
                        Object[] obj2 = keys1.toArray();
                        //倒序
                        Arrays.sort(obj2, Collections.reverseOrder());
                        //获取最新日期一条formId
                        String formId = (String) redisTemplateUtils.popListRight(obj2[0] + "");
                        //封装数据
                        WXAppletMessageTempRestEntity bean = new WXAppletMessageTempRestEntity(Integer.valueOf(StringUtils.substringAfterLast(obj[j] + "", ":")), openId, formId);
                        list.add(bean);
                    }
                    //插入mysql
                    try {
                        wxAppletMessageTempService.foreachInsert(list);
                    } finally {
                        //线程执行完
                        countDownLatch.countDown();
                        list=null;
                    }
                });
            }
            //阻塞线程
            try {
                countDownLatch.await();
                //统计月支出收入
                wxAppletMessageTempService.foreachInsert2ForMonth(first.toString(), end.toString());
            } catch (InterruptedException e) {
                logger.error("accountNotify 异常:" + e.toString());
            }
        } else {
            List<WXAppletMessageTempRestEntity> list = new ArrayList<>(keys.size());
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                //获取openId
                String openId = redisTemplateUtils.getForString(key);
                //获取一条有效formId
                Set keys1 = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_PUSH + openId + "*");
                Object[] obj2 = keys1.toArray();
                //倒序
                Arrays.sort(obj2, Collections.reverseOrder());
                //获取最新日期一条formId
                String formId = (String) redisTemplateUtils.popListRight(obj2[0] + "");
                //封装数据
                WXAppletMessageTempRestEntity bean = new WXAppletMessageTempRestEntity(Integer.valueOf(StringUtils.substringAfterLast(key, ":")), openId, formId);
                list.add(bean);
            }
            keys=null;
            //插入mysql
            wxAppletMessageTempService.foreachInsert(list);
            //统计月支出收入
            wxAppletMessageTempService.foreachInsert2ForMonth(first.toString(), end.toString());
            list=null;
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        accountNotify();
    }
}
