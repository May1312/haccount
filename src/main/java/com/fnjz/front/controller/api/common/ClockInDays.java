package com.fnjz.front.controller.api.common;

import com.fnjz.front.utils.DateUtils;
import com.fnjz.front.utils.RedisTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 连续打卡通用类
 * Created by yhang on 2018/9/8.
 */
@Component
public class ClockInDays {

    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    public void clockInDays(String shareCode){
        final String shareCode2 =shareCode;
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //连续打卡统计
                Map s = redisTemplateUtils.getMyCount(shareCode2);
                if (s.size() > 0) {
                    if (s.get("clockInDays") == null && s.get("clockInTime") == null) {
                        //首次打卡
                        s.put("clockInDays", 1);
                        s.put("clockInTime", (System.currentTimeMillis() + ""));
                        redisTemplateUtils.updateMyCount(shareCode2, s);
                    } else {
                        //判断打卡间隔
                        //获取下一天凌晨时间间隔
                        Date nextDay = DateUtils.getNextDay(new Date(Long.valueOf(s.get("clockInTime") + "")));
                        //获取当天凌晨范围
                        Date dateOfBegin = DateUtils.fetchBeginOfDay(nextDay);
                        Date dateOfEnd = DateUtils.fetchEndOfDay(nextDay);
                        long now = System.currentTimeMillis();
                        if (now > dateOfBegin.getTime() && now < dateOfEnd.getTime()) {
                            //打卡成功
                            s.put("clockInDays", (Integer.valueOf(s.get("clockInDays") + "") + 1));
                            s.put("clockInTime", (System.currentTimeMillis() + ""));
                            redisTemplateUtils.updateMyCount(shareCode2, s);
                        } else if (now > dateOfEnd.getTime()) {
                            //置空
                            s.put("clockInDays", 1);
                            s.put("clockInTime", System.currentTimeMillis() + "");
                            redisTemplateUtils.updateMyCount(shareCode2, s);
                        }
                    }
                } else {
                    s.put("clockInDays", 1);
                    s.put("clockInTime", (System.currentTimeMillis() + ""));
                    redisTemplateUtils.updateMyCount(shareCode2, s);
                }
            }
        });
    }
}
