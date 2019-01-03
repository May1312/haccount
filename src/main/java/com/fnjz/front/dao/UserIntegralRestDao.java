package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userintegral.UserIntegralRestDTO;
import com.fnjz.front.entity.api.userintegral.UserIntegralRestEntity;
import com.fnjz.front.entity.api.userintegral.UserIntegralTopRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.math.BigDecimal;
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
    @Sql("INSERT INTO `hbird_user_integral` (`user_info_id`,`integral_num`,`fengfeng_ticket_id`,`create_date`,`description`,`type`,`category_of_behavior`,`integral_num_double`) VALUES (:userInfoId,:behaviorTicketValue,:id,NOW(),:description,:type,:categoryOfBehavior,:integralDouble);")
    void insertSignInIntegral(@Param("userInfoId") String userInfoId,@Param("id") String id,@Param("behaviorTicketValue") Integer behaviorTicketValue,@Param("description")String description,@Param("type")Integer type,@Param("categoryOfBehavior")Integer categoryOfBehavior,@Param("integralDouble")double integralDouble);

    @Sql("SELECT COALESCE(SUM(integral_num_double),0) from `hbird_user_integral` where `user_info_id`=:userInfoId;")
    double getTotalIntegralBySum(@Param("userInfoId") String userInfoId);

    /**
     * 从用户对应总积分表中获取积分数
     * @param userInfoId
     */
    @Sql("SELECT integral_num_decimal from `hbird_user_total_integrals` where `user_info_id`=:userInfoId;")
    String getTotalIntegral(@Param("userInfoId") String userInfoId);

    @ResultType(UserIntegralRestDTO.class)
    @Sql("SELECT integral_num_double as integral_num,description,create_date FROM hbird_user_integral where user_info_id=:userInfoId ORDER BY create_date desc LIMIT :curpage,:itemPerPage")
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

    /**
     * 查询任务完成情况
     * @param categoryOfBehaviorEnum
     * @param userInfoId
     * @return
     */
    @ResultType(UserIntegralRestEntity.class)
    @Sql("select * from hbird_user_integral where user_info_id=:userInfoId and if(:categoryOfBehaviorEnum=1,category_of_behavior=:categoryOfBehaviorEnum,category_of_behavior=:categoryOfBehaviorEnum and create_date like concat(CURRENT_DATE,'%')) GROUP BY  type;")
    List<UserIntegralRestEntity> getTaskComplete(@Param("categoryOfBehaviorEnum") int categoryOfBehaviorEnum,@Param("userInfoId") String userInfoId);

    /**
     * 获取积分排行榜
     * @return
     */
    //@Sql("SELECT userInfo.nick_name, userInfo.avatar_url, top.integral_num_decimal as integral_num, @rank := @rank + 1 as rank FROM ( SELECT @rank := 0 ) AS rank, ( SELECT user_info_id,integral_num,integral_num_decimal FROM hbird_user_total_integrals ORDER BY integral_num_decimal DESC LIMIT 0, :top ) AS top LEFT JOIN hbird_user_info userInfo ON userInfo.id = top.user_info_id;")
    @Sql("select t.user_info_id,t1.nick_name,t1.avatar_url,t.integral_num_decimal as integral_num from hbird_user_total_integrals t inner join hbird_user_info t1 on t.user_info_id = t1.id order by t.integral_num_decimal desc limit :top;")
    List<UserIntegralTopRestDTO> integralTop(@Param("top") int top);

    /**
     * 查询自有积分数  ---->获取排名
     * @param userInfoId
     * @return
     */
    //@Sql("SELECT result.nick_name, result.avatar_url, result.integral_num_decimal as integral_num, result.rank FROM ( SELECT userInfo.nick_name, userInfo.avatar_url, top.integral_num_decimal, top.user_info_id, @rank := @rank + 1 AS rank FROM ( SELECT @rank := 0 ) AS rank, ( SELECT user_info_id,integral_num_decimal FROM hbird_user_total_integrals ORDER BY integral_num_decimal DESC ) AS top LEFT JOIN hbird_user_info userInfo ON userInfo.id = top.user_info_id ) AS result WHERE result.user_info_id = :userInfoId;")
    @Sql("select count(*) as rank,t.integral_num_decimal as integral_num from hbird_user_total_integrals t where t.integral_num_decimal>=( select t.integral_num_decimal from hbird_user_total_integrals t where t.user_info_id = :userInfoId )")
    UserIntegralTopRestDTO integralForMySelf(@Param("userInfoId") String userInfoId);

    /**
     * 添加商城兑换积分消耗记录
     * @param userInfoId
     * @param id
     * @param behaviorTicketValue
     */
    @Sql("INSERT INTO `hbird_user_integral` (`user_info_id`,`integral_num`,`shopping_mall_integral_exchange_id`,`create_date`,`description`,`category_of_behavior`,`integral_num_double`) VALUES (:userInfoId,:behaviorTicketValue,:id,NOW(),:description,:categoryOfBehavior,:integralDouble);")
    void insertShoppingMallIntegral(@Param("userInfoId") String userInfoId,@Param("shoppingMallIntegralExchangeId") String id,@Param("behaviorTicketValue") String behaviorTicketValue,@Param("description")String description,@Param("categoryOfBehavior")Integer categoryOfBehavior,@Param("integralDouble")double integralDouble);

    /**
     * 赋值总积分表
     * @param userInfoId
     * @param sum
     * @param i
     */
    @Sql("insert into hbird_user_total_integrals (`user_info_id`,`integral_num_decimal`,`type`,`create_date`) values (:userInfoId,:integralNum,:type,now())")
    void insertForTotalIntegral(@Param("userInfoId") String userInfoId,@Param("integralNum") BigDecimal sum,@Param("type") int i);

    @Sql("update hbird_user_total_integrals set integral_num_decimal = integral_num_decimal+:decimal,update_date=now() where user_info_id=:userInfoId;")
    void updateForTotalIntegral(@Param("userInfoId") String userInfoId,@Param("integralNum") Integer integer,@Param("decimal") BigDecimal decimal);
}
