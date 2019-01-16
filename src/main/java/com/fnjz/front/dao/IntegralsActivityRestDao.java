package com.fnjz.front.dao;

import com.fnjz.front.entity.api.integralsactivity.IntegralsActivityRestEntity;
import com.fnjz.front.entity.api.integralsactivity.UserIntegralsActivityRestDTO;
import com.fnjz.front.entity.api.integralsactivity.UserIntegralsActivitySumRestDTO;
import com.fnjz.front.entity.api.integralsactivityrange.IntegralsActivityRangeRestEntity;
import com.fnjz.front.entity.api.shoppingmallintegralexchange.ReportShopRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2019/1/9.
 */
@MiniDao
public interface IntegralsActivityRestDao {

    /**
     * 获取获取积分活动页头部记录播报
     * @return
     */
    @Sql("SELECT base2.nick_name, base1.get_integrals as value FROM hbird_user_integrals_activity AS base1 INNER JOIN hbird_user_info AS base2 ON base1.user_info_id = base2.id WHERE base1.status=3 ORDER BY base1.id DESC LIMIT 20;")
    List<ReportShopRestDTO> reportForIntegral();

    @Sql("select id,sum(total_integrals+add_integrals) as total_integrals,create_date from hbird_integrals_activity where create_date LIKE concat(:time,'%') order by id desc limit 1;")
    IntegralsActivityRestEntity getActivityInfo(@Param("time") String time);

    @Sql("SELECT status,sum(integrals+get_integrals) as integrals,create_date,charge_date,end_date FROM hbird_user_integrals_activity WHERE user_info_id= :userInfoId and create_date BETWEEN :beginTime AND :endTime group by ia_id order by ia_id desc;")
    List<UserIntegralsActivityRestDTO> getPersonalActivity(@Param("userInfoId") String userInfoId, @Param("beginTime")String beginTime, @Param("endTime")String endTime);

    @Sql("select false_total_users,false_success_users,false_fail_users from hbird_integrals_activity where create_date LIKE concat(:time,'%') order by id desc limit 1;")
    IntegralsActivityRestEntity getLastActivityInfo(@Param("time") String time);

    @Sql("select id,integrals from hbird_user_integrals_activity_range where id=:iarId;")
    IntegralsActivityRangeRestEntity getIntegralsActivityRangeById(@Param("iarId") String iarId);

    @Sql("INSERT INTO `hbird_user_integrals_activity` ( `user_info_id`, `ia_id`, `integrals`, `status`, `create_date` ) VALUES ( :userInfoId, :iaId, :integral, 1, now());")
    void insertUserIntegralActivity(@Param("userInfoId") String userInfoId,@Param("iaId") String iaId,@Param("integral") double integral);

    @Sql("select id,integrals from hbird_user_integrals_activity_range;")
    List<IntegralsActivityRangeRestEntity> getIntegralActivityRange();

    /**
     * 更新期数记录  参与人数
     * @param iaId
     */
    @Sql("UPDATE `hbird_integrals_activity` SET `total_users` = total_users + 1, `total_integrals` = total_integrals+:integral, `false_total_users` = false_total_users + 1, `update_date` = now( ) WHERE `id` = :iaId;")
    void updateIntegralActivityForTotalUsers(@Param("iaId") String iaId,@Param("integral") double integral);

    @Sql("select sum(integrals) as integrals_for_spend,sum(get_integrals) as integrals_for_income from hbird_user_integrals_activity where user_info_id=:userInfoId;")
    UserIntegralsActivitySumRestDTO getPersonalActivityInfo(@Param("userInfoId") String userInfoId);

    @Sql("select integrals,get_integrals,`status`,create_date,charge_date,end_date from hbird_user_integrals_activity where user_info_id=:userInfoId order by create_date desc LIMIT :curpage,:itemPerPage;")
    List<UserIntegralsActivityRestDTO> getPersonalActivityInfoForPage(@Param("userInfoId") String userInfoId,@Param("curpage") int startIndex,@Param("itemPerPage") int pageSize);

    @Sql("select count(id) from hbird_user_integrals_activity where user_info_id=:userInfoId;")
    Integer getCountForUserIntegrals(@Param("userInfoId") String userInfoId);

    @Sql("select id,create_date from hbird_integrals_activity where id=:iaId;")
    IntegralsActivityRestEntity getIntegralsActivityById(@Param("iaId") String iaId);

    /**
     * 查看指定日期用户是否参与记账挑战赛
     * @param userInfoId
     * @param time
     * @return
     */
    @Sql("SELECT id,status,ia_id as iaid FROM hbird_user_integrals_activity WHERE user_info_id =:userInfoId and create_date LIKE concat(:time,'%');")
    Map<String,Integer> checkSignUpByUserInfoIdAndTime(@Param("userInfoId") String userInfoId, @Param("time") String time);

    /**
     * 更新记账挑战赛---->记账状态
     * @param id
     */
    @Sql("UPDATE `hbird_user_integrals_activity` SET `status` = 2, `charge_date` = now( ), `update_date` = now( ) WHERE `id` = :id;")
    void updateUserIntegralActivityForChangeDate(@Param("id") Integer id);

    @Sql("UPDATE `hbird_integrals_activity` SET `success_users` = success_users + 1, `false_success_users` = false_success_users+1,`update_date` = now( ) WHERE `id` = :iaId;")
    void updateIntegralActivityForCharge(@Param("iaId") Integer iaId);
    /**
     * 检查前推两期是否达标---->查看系统结果
     * @param userInfoId
     * @param time
     * @return
     */
    @Sql("SELECT id,(integrals+get_integrals) AS total_integrals, create_date FROM hbird_user_integrals_activity WHERE user_info_id=:userInfoId and create_date LIKE concat( :time, '%') AND STATUS = 3;")
    IntegralsActivityRestEntity checkActivityResult(@Param("userInfoId") String userInfoId,@Param("time") String time);

    @Sql("select count(id) from hbird_user_integrals_activity where user_info_id=:userInfoId and ia_id=:iaId;")
    Integer checkUserSignup(@Param("userInfoId")String userInfoId,@Param("iaId") String iaId);
}
