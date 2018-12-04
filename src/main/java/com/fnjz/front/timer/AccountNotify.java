package com.fnjz.front.timer;

import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.front.entity.api.wxappletmessagetemp.WXAppletAccountNotifyTempRestEntity;
import com.fnjz.front.service.api.wxappletmessagetemp.WXAppletMessageTempService;
import com.fnjz.front.utils.WXAppletPushUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.StampedLock;

/**
 * 账单通知定时任务
 * Created by yhang on 2018/12/3.
 */
@Service("accountNotify")
public class AccountNotify implements Job {

    private static final Logger logger = Logger.getLogger(AccountNotify.class);

    @Autowired
    private WXAppletPushUtils wxAppletPushUtils;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private WXAppletMessageTempService wxAppletMessageTempService;

    /**
     * 线程数定义
     */
    private int threadNum = 5;

    /**
     * 锁
     */
    private final StampedLock lock = new StampedLock();

    private List<Integer> lockList = new ArrayList<>();

    public void accountNotify() {

        List<WXAppletAccountNotifyTempRestEntity> list = wxAppletMessageTempService.getAccountNotifyData();
        if (list != null) {
            if (list.size() > 0) {
                Long start = System.currentTimeMillis();
                LocalDate date = LocalDate.now();
                date = date.minusMonths(1);
                DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy年MM月");
                String time = date.format(formatters);
                //当需要推送的用户数大于100   启用多线程
                if (list.size() > 100) {
                    //获取每个线程处理的数据长度
                    int len = list.size() / threadNum;
                    CountDownLatch countDownLatch = new CountDownLatch(threadNum);
                    //创建线程
                    for (int i = 0; i < threadNum; i++) {
                        taskExecutor.execute(() -> {
                            long stamp = lock.writeLock();
                            int thread = lockList.size();
                            lockList.add(1);
                            lock.unlockWrite(stamp);
                            for (int j = (list.size() - 1 - (len * thread)); j > (list.size() - len - (len * thread)); j--) {
                                //发送推送
                                wxappletPush(WXAppletPushUtils.accountNotifyId, time, list.get(j));
                            }
                            //线程执行完
                            countDownLatch.countDown();
                        });
                    }
                    //阻塞线程
                    try {
                        countDownLatch.await();
                        logger.info("======发送完毕===== 耗时:" + (System.currentTimeMillis() - start));
                        wxAppletMessageTempService.deleteDate();
                    } catch (InterruptedException e) {
                        logger.error("accountNotify 异常:" + e.toString());
                    }
                } else {
                    for (int j = 0; j < list.size(); j++) {
                        //发送推送
                        wxappletPush(WXAppletPushUtils.accountNotifyId, time, list.get(j));
                    }
                    logger.info("======发送完毕===== 耗时:" + (System.currentTimeMillis() - start));
                    wxAppletMessageTempService.deleteDate();
                }
            }
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        accountNotify();
    }

    /**
     * 封装message数据---->发送服务通知
     */
    private void wxappletPush(String templateId, String time, WXAppletAccountNotifyTempRestEntity notify) {
        WXAppletMessageBean bean = new WXAppletMessageBean();
        //设置账单名称
        bean.getKeyword1().put("value", time + "账单统计");
        //设置本月支出
        bean.getKeyword2().put("value", notify.getSpend() + "元");
        //设置本月收入
        bean.getKeyword3().put("value", notify.getIncome() + "元");
        //设置备注
        bean.getKeyword4().put("value", "越记账越清晰，记账就能领现金→");
        //设置账单详情
        bean.getKeyword5().put("value", "想了解本月支出排行榜第1位？请点击~");
        wxAppletPushUtils.wxappletPush(templateId, notify.getOpenId(), notify.getFormId(), WXAppletPushUtils.accountNotifyPage, bean);
    }
}
