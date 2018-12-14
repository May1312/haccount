package com.fnjz.front.dao;

import com.fnjz.front.entity.api.usersigninaward.UserSignInAwardRestDTO;
import com.fnjz.front.entity.api.usersigninaward.UserSignInAwardRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * 用户连签奖励领取情况表
 * Created by yhang on 2018/12/08.
 */
@MiniDao
public interface UserSignInAwardRestDao {

    /**
     * 更新
     * @param bean
     */
    void update(@Param("bean") UserSignInAwardRestEntity bean);

    /**
     * 获取用户连签奖励领取情况
     * @param userInfoId
     * @param cycles
     * @return
     */
    @Sql("select cycle,cycle_award_status,get_times from hbird_user_sign_in_award where user_info_id = :userInfoId and cycle in ( ${DaoFormat.getInStrs(cycles)} ) and delflag=0;")
    List<UserSignInAwardRestDTO> listByUserInfoId(@Param("userInfoId") Integer userInfoId,@Param("cycles") List<String> cycles);

    /**
     * 新增
     * @param bean
     */
    @Sql("insert into hbird_user_sign_in_award (`user_info_id`,`category_of_behavior`,`cycle`,`cycle_award_status`,`get_times`,`create_date`,`delflag`) values(:bean.userInfoId,:bean.categoryOfBehavior,:bean.cycle,:bean.cycleAwardStatus,:bean.getTimes,now(),:bean.delflag);")
    void insert(@Param("bean") UserSignInAwardRestEntity bean);

    /**
     * 获取指定周期内奖励可领取次数
     * @param userInfoId
     * @param cycle
     * @return
     */
    @Sql("select get_times,cycle_award_status from hbird_user_sign_in_award where user_info_id=:userInfoId and cycle=:cycle and delflag=0;")
    UserSignInAwardRestEntity getGetTimesAndAwardStatus(@Param("userInfoId") String userInfoId,@Param("cycle") String cycle);

    /**
     * 周期结束 重置全部领取状态
     * @param userInfoId
     */
    @Sql("update hbird_user_sign_in_award set cycle_award_status=3 where user_info_id=:userInfoId and category_of_behavior=:behave and delflag = 0;")
    void updateAllForReset(@Param("userInfoId") Integer userInfoId,@Param("behave")String behave);
}
