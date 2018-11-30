package com.fnjz.front.timer;

import com.fnjz.commonbean.WXAppletMessageBean;
import com.fnjz.constants.RedisPrefix;
import com.fnjz.front.dao.WarterOrderRestDao;
import com.fnjz.front.utils.RedisTemplateUtils;
import com.fnjz.front.utils.WXAppletPushUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.Set;

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

    public void accountNotify(){
        //读取用户id--->对应openId  所有可以推送通知的用户
        Set keys = redisTemplateUtils.getKeys(RedisPrefix.PREFIX_WXAPPLET_USERINFOID_OPENID+ "*");
        LocalDate date = LocalDate.now();
        date = date.minusMonths(1);
        LocalDate first = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy年MM月");
        String time = date.format(formatters);
        if(keys.size()>100){
            //当需要推送的用户数大于100 多线程开启
        }else{
            for (Object userInfoId : keys) {
                //统计此用户总账本月支出情况
                Map<String, BigDecimal> monthStatistics = getMonthStatistics(userInfoId+"",first.toString(),end.toString());
                String openId = redisTemplateUtils.getForString(userInfoId+"");
                //根据openId
                //wxappletPush(WXAppletPushUtils.accountNotifyId, split[0], formId,"pages/mine/index/main",time, monthStatistics);
                //删除key
                //redisTemplateUtils.deleteKey(key);
            }
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        accountNotify();
    }

    /**
     * 获取月统计数据
     */
    private Map<String,BigDecimal> getMonthStatistics(String userInfoId,String first,String end){
        return warterOrderRestDao.getAccountForAll(first, end, userInfoId);
    }

    /**
     * 封装message数据---->发送服务通知
     */
    private void wxappletPush(String templateId,String openId,String formId,String page,String time,Map<String,BigDecimal> map){
        WXAppletMessageBean bean = new WXAppletMessageBean();
        //设置账单名称
        bean.getKeyword1().put("value",time+"账单统计");
        //设置本月支出
        bean.getKeyword2().put("value",map.get("spend")+"元");
        //设置本月收入
        bean.getKeyword3().put("value",map.get("income")+"元");
        //设置备注
        bean.getKeyword4().put("value","越记账越清晰，记账就能领现金→");
        //设置账单详情
        bean.getKeyword5().put("value","想了解本月支出排行榜第1位？请点击~");
        wxAppletPushUtils.wxappletPush(templateId,openId,formId,"pages/chart/index/main",bean);
    }
}
