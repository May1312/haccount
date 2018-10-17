package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userintegral.UserIntegralRestDTO;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.Date;
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
    @Sql("INSERT INTO `hbird_user_integral` (`user_info_id`,`integral_num`,`fengfeng_ticket_id`,`create_date`,`description`,`type`,`category_of_behavior`) VALUES (:userInfoId,:behaviorTicketValue,:id,NOW(),:description,:type,:categoryOfBehavior);")
    void insertSignInIntegral(@Param("userInfoId") String userInfoId,@Param("id") String id,@Param("behaviorTicketValue") Integer behaviorTicketValue,@Param("description")String description,@Param("type")Integer type,@Param("categoryOfBehavior")Integer categoryOfBehavior);

    @Sql("SELECT COALESCE(SUM(integral_num),0) from `hbird_user_integral` where `user_info_id`=:userInfoId;")
    int getTotalIntegral(@Param("userInfoId") String userInfoId);

    @ResultType(UserIntegralRestDTO.class)
    @Sql("SELECT integral_num,description,create_date FROM hbird_user_integral where user_info_id=:userInfoId ORDER BY create_date LIMIT :curpage,:itemPerPage")
    List<UserIntegralRestDTO> listForPage(@Param("userInfoId")String userInfoId, @Param("curpage") Integer curpage, @Param("itemPerPage") Integer itemPerPage);

    /**
     * 分页统计
     * @param userInfoId
     * @return
     */
    @Sql("select count(*) from hbird_user_integral where user_info_id=:userInfoId")
    Integer getCount(@Param("userInfoId")String userInfoId);

    /**
     * 统计积分流水中  连签 7/14/21/28签到历史记录  用户恢复历史
     * @param userInfoId
     * @param index
     * @param index1
     * @param index2
     * @param index3
     * @return
     */
    @ResultType(UserIntegralRestEntity.class)
    @Sql("SELECT * from hbird_user_integral where user_info_id=:userInfoId and create_date >=:date and type in(:index,:index1,:index2,:index3) order by create_date desc limit 0,4;")
    List<UserIntegralRestEntity> getCurrentCycleIntegralForRecover(@Param("userInfoId") String userInfoId, @Param("date")Date date, @Param("index") int index, @Param("index1") int index1, @Param("index2") int index2, @Param("index3") int index3);

    /**
     * 根据行为类别/获取方式  判断是否已领取
     * @param categoryOfBehaviorEnum
     * @param acquisitionModeEnum
     * @param userInfoId
     * @return
     */
    @Sql("select count(id) from hbird_user_integral where user_info_id=:userInfoId and if(:categoryOfBehaviorEnum=1,type=:acquisitionModeEnum,type=:acquisitionModeEnum and create_date like concat(CURRENT_DATE,'%'));")
    int checkTaskComplete(@Param("categoryOfBehaviorEnum") int categoryOfBehaviorEnum,@Param("acquisitionModeEnum") int acquisitionModeEnum,@Param("userInfoId") String userInfoId);
}
