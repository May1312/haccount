package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/12/18.
 */
@MiniDao
public interface RegisterChannelRestDao {

    @Sql("insert into hbird_register_channel (user_info_id,channel,type,create_date) values (:userInfoId,:channel,:type,now());")
    void insert(@Param("channel") String channel,@Param("userInfoId") int userInfoId,@Param("type") int type);

    /**
     * 统计当天记账人数    记账笔数分布  0  未记账    1记账    2 记账两笔   3笔及以上
     * @param channel
     * @param time
     */
    @Sql("select base3.count as chargeCount,count(base3.count) as userCount from (select count(*) as count from hbird_water_order as base1 INNER JOIN hbird_register_channel as base2 on base1.update_by=base2.user_info_id where base2.channel=:channel and base2.type=1 and base1.create_date like concat(:time,'%') GROUP BY base1.update_by) as base3 group by base3.count;")
    List<Map<String, Object>> getTodayStatisticsForCharge(@Param("channel") String channel, @Param("time") String time);

    /**
     * 邀请好友数
     * @param channel
     * @param time
     * @return
     */
    @Sql("select COALESCE(count(base2.user_info_id),0) from hbird_register_channel as base1 INNER JOIN hbird_user_invite as base2 on base1.user_info_id=base2.user_info_id where base1.channel=:channel and base1.type=1 and base2.create_date like concat(:time,'%');")
    Integer getTodayStatisticsForInvite(@Param("channel") String channel, @Param("time") String time);

    /**
     * 总积分数 todo 徽章上线之后积分数数的统计续做修改
     * @param channel
     * @return
     */
    @Sql("SELECT COALESCE(sum( base2.integral_num_decimal ),0) FROM hbird_register_channel AS base1 INNER JOIN hbird_user_total_integrals AS base2 ON base1.user_info_id = base2.user_info_id WHERE base1.channel =:channel AND base1.type = 1;")
    int getStatisticsForIntegral(@Param("channel") String channel);

    /**
     * 统计记过账的人数
     * @param channel
     * @return
     */
    @Sql("select COALESCE(count(*),0) from (select base2.update_by from hbird_register_channel as base1 INNER JOIN hbird_water_order as base2 on base1.user_info_id=base2.update_by where base1.channel=:channel and base1.type=1 group by base2.update_by) as base3;")
    int getTotalStatisticsForCharge(@Param("channel") String channel);

    /**
     * 统计邀请好友数
     * @param channel
     * @return
     */
    @Sql("select COALESCE(count(base2.user_info_id),0) from hbird_register_channel as base1 INNER JOIN hbird_user_invite as base2 on base1.user_info_id=base2.user_info_id where base1.channel=:channel and base1.type=1;")
    int getTotalStatisticsForInvite(@Param("channel") String channel);
}
