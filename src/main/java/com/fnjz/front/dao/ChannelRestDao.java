package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

/**
 * Created by yhang on 2019/1/5.
 */
@MiniDao
public interface ChannelRestDao {

    @Sql("select id from hbird_channel where channel_nid=:channelNid;")
    Integer getIdByChannelNid(@Param("channelNid") String channelNid);
}
