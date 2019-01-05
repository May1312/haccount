package com.fnjz.front.service.impl.api.channel;

import com.fnjz.front.dao.ChannelRestDao;
import com.fnjz.front.service.api.channel.ChannelRestServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yhang on 2019/1/5.
 */
@Service("channelRestServiceI")
public class ChannelRestServiceImpl implements ChannelRestServiceI {

    @Autowired
    private ChannelRestDao channelRestDao;

    @Override
    public Integer getIdByChannelNid(String androidChannel) {
        return channelRestDao.getIdByChannelNid(androidChannel);
    }
}
