package com.fnjz.front.service.api.registerchannel;

import org.jeecgframework.core.common.service.CommonService;

import java.util.Map;

public interface RegisterChannelRestServiceI extends CommonService{

    /**
     * 插入记录
     * @param channel
     * @param userInfoId
     * @param type
     */
    void insert(String channel, int userInfoId, int type);

    /**
     * 统计当天记账人数    记账笔数分布  0  未记账    1记账    2 记账两笔   3笔及以上
     * @param channel
     * @param s
     */
    Map<String,Object> getTodayStatistics(String channel, String s,int sumNewRegister2);

    /**
     * （记过账的人数）、累计邀请好友数、累计丰丰票数
     * @param wxappletChannel
     * @return
     */
    Map<String,Object> getTotalStatistics(String wxappletChannel);
}
