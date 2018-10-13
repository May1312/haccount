package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userintegral.UserIntegralRestDTO;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

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
    @Sql("INSERT INTO `hbird_user_integral` (`user_info_id`,`integral_num`,`fengfeng_ticket_id`,`create_date`,`description`) VALUES (:userInfoId,:behaviorTicketValue,:id,NOW(),:description);")
    void insertSignInIntegral(@Param("userInfoId") String userInfoId,@Param("id") String id,@Param("behaviorTicketValue") Integer behaviorTicketValue,@Param("type")String acquisitionMode,@Param("description")String description);

    @Sql("SELECT COALESCE(SUM(integral_num),0) from `hbird_user_integral` where `user_info_id`=:userInfoId;")
    int getTotalIntegral(@Param("userInfoId") String userInfoId);

    @ResultType(UserIntegralRestEntity.class)
    @Sql("SELECT integral_num,description,create_date FROM hbird_user_integral where user_info_id=:userInfoId ORDER BY create_date LIMIT :curpage,:itemPerPage")
    List<UserIntegralRestDTO> listForPage(@Param("userInfoId")String userInfoId, @Param("curpage") Integer curpage, @Param("itemPerPage") Integer itemPerPage);

    /**
     * 分页统计
     * @param userInfoId
     * @return
     */
    @Sql("select count(*) from hbird_user_integral where user_info_id=:userInfoId")
    Integer getCount(@Param("userInfoId")String userInfoId);
}
