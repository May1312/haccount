package com.fnjz.front.service.api.channel;

/**
 * Created by yhang on 2019/1/5.
 */
public interface ChannelRestServiceI {
    /**
     * 根据渠道标识获取渠道id
     * @param androidChannel
     * @return
     */
    Integer getIdByChannelNid(String androidChannel);
}
