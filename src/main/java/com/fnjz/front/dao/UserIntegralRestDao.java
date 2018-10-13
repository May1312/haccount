package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

/**
 * Created by yhang on 2018/10/13.
 */
@MiniDao
public interface UserIntegralRestDao {

    /**
     * 添加签到积分领取记录
     * @param userInfoId
     * @param id
     * @param behaviorTicketValue
     */
    @Sql("INSERT INTO `hbird_user_integral` (`user_info_id`,`integral_num`,`fengfeng_ticket_id`,`create_date`) VALUES (:userInfoId,:behaviorTicketValue,:id,NOW());")
    void insertSignInIntegral(@Param("userInfoId") String userInfoId,@Param("id") String id,@Param("behaviorTicketValue") Integer behaviorTicketValue);
}
