package com.fnjz.front.service.impl.api.registerchannel;

import com.fnjz.front.dao.RegisterChannelRestDao;
import com.fnjz.front.service.api.registerchannel.RegisterChannelRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service("registerChannelRestService")
@Transactional
public class RegisterChannelRestServiceImpl extends CommonServiceImpl implements RegisterChannelRestServiceI {

    @Autowired
    private RegisterChannelRestDao registerChannelRestDao;

    @Override
    public void insert(String channel, int userInfoId, int type) {
        registerChannelRestDao.insert(channel,userInfoId,type);
    }

    //统计当天记账人数    记账笔数分布  0  未记账    1记账    2 记账两笔   3笔及以上
    @Override
    public Map<String,Object> getTodayStatistics(String channel, String time,int sumNewRegister2) {
        List<Map<String, Object>> todayStatistics = registerChannelRestDao.getTodayStatisticsForCharge(channel, time);
        //map中userCount累加为当天记账人数
        AtomicInteger userCount= new AtomicInteger(0);
        Map<String,Object> map = new HashMap<>();
        todayStatistics.forEach(v->{
            //chargeCount记账笔数 对应  userCount用户数
            Integer chargeCount = Integer.valueOf(v.get("chargeCount")+"");
            Integer userCount1 = Integer.valueOf(v.get("userCount") + "");
            //判断记账笔数区间
            if(chargeCount>=3){
                //todo 比较字符串！！！ 先这么得吧
                map.put(">=3",userCount1);
                if(map.containsKey(">=3")){
                    map.put(">=3",Integer.valueOf(map.get(">=3")+"")+userCount1);
                }else{
                    map.put(">=3",userCount1);
                }
            }else{
                map.put(chargeCount+"",userCount1);
            }
            userCount.addAndGet(userCount1);
        });
        if(!map.containsKey("1")){
            map.put("1",0);
        }
        if(!map.containsKey("2")){
            map.put("2",0);
        }
        if(!map.containsKey(">=3")){
            map.put(">=3",0);
        }
        map.put("0",sumNewRegister2-userCount.intValue());
        Map map1= new TreeMap();
        map.forEach((i,v)->{
            map1.put("记账笔数分布["+i+"笔]",v);
        });
        map1.put("["+time+"]"+"记账人数:",userCount.intValue());
        Map<String,Object> map2 = new TreeMap<>(Comparator.reverseOrder());
        //邀请好友数  丰丰票数
        int inviteFriends = registerChannelRestDao.getTodayStatisticsForInvite(channel,time);
        map1.put("["+time+"]"+"邀请好友数:",inviteFriends);
        map2.put("日新增数据",map1);
        return map2;
    }

    /**
     * （记过账的人数）、累计邀请好友数、累计丰丰票数
     * @param channel
     * @return
     */
    @Override
    public Map<String, Object> getTotalStatistics(String channel) {
        //记账人数
        int chargeCount = registerChannelRestDao.getTotalStatisticsForCharge(channel);
        //累计总积分数
        int totalIntegral = registerChannelRestDao.getStatisticsForIntegral(channel);
        //累计邀请好友数
        int totalInvite = registerChannelRestDao.getTotalStatisticsForInvite(channel);
        Map<String,Object> map = new TreeMap<>();
        map.put("总记账人数",chargeCount);
        map.put("总邀请好友数",totalInvite);
        map.put("总积分数",totalIntegral);
        return map;
    }

}