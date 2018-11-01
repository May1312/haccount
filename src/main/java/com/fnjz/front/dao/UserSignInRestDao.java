package com.fnjz.front.dao;

import com.fnjz.front.entity.api.usersignin.UserSignInRestDTO;
import com.fnjz.front.entity.api.usersignin.UserSignInRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/10/10.
 */
@MiniDao
public interface UserSignInRestDao {

    /**
     * 签到
     * @param userInfoId
     */
    @Sql("INSERT INTO `hbird_account`.`hbird_user_sign_in`(`user_info_id`, `sign_in_date`, `status`, `create_date`) VALUES (:userInfoId, NOW(), :status, NOW());")
    void signIn(@Param("userInfoId") String userInfoId,@Param("status") Integer status);

    /**
     * 补签
     * @param userInfoId
     */
    @Sql("INSERT INTO `hbird_account`.`hbird_user_sign_in`(`user_info_id`, `sign_in_date`, `status`, `create_date`) VALUES (:userInfoId, :signInDate, :status, NOW());")
    void reSignIn(@Param("userInfoId") String userInfoId,@Param("status") Integer status,@Param("signInDate") String signInDate);

    /**
     * 查看当天是否已签到
     * @param userInfoId
     * @return
     */
    @Sql("SELECT count(id) FROM `hbird_account`.`hbird_user_sign_in` WHERE `user_info_id` = :userInfoId and `sign_in_date`=CURDATE();")
    int checkSignInForCurrentDay(@Param("userInfoId") String userInfoId);

    /**
     * 查看前一天是否存在打卡记录
     * @return
     */
    @ResultType(Integer.class)
    @Sql("SELECT count(id) FROM `hbird_account`.`hbird_user_sign_in` WHERE `user_info_id` = :userInfoId and `sign_in_date`=DATE_SUB(CURDATE(),INTERVAL 1 DAY);")
    int checkSignInForBeforeCurrentDay(@Param("userInfoId") String userInfoId);

    /**
     * 获取最近一次 --->第一次签到记录
     * @param userInfoId
     * @return
     */
    @Sql("select * from `hbird_account`.`hbird_user_sign_in` WHERE `user_info_id` = :userInfoId and status=1 order by sign_in_date DESC limit 0,1;")
    UserSignInRestEntity getSignInForFisrtDesc(@Param("userInfoId") String userInfoId);

    /**
     * 获取最近两次次 --->第一次签到记录
     * @param userInfoId
     * @return
     */
    @ResultType(UserSignInRestEntity.class)
    @Sql("select * from `hbird_account`.`hbird_user_sign_in` WHERE `user_info_id` = :userInfoId and status=1 order by sign_in_date DESC limit 0,2;")
    List<UserSignInRestEntity> getSignInForSecondDesc(@Param("userInfoId") String userInfoId);

    /**
     * 获取当前周签到情况
     * @param userInfoId
     * @return
     */
    @Sql("SELECT * FROM hbird_user_sign_in WHERE user_info_id = :userInfoId AND sign_in_date BETWEEN(SELECT date_sub( curdate( ), INTERVAL WEEKDAY( curdate( ) ) DAY)) and CURRENT_DATE ORDER BY sign_in_date ASC;")
    List<UserSignInRestEntity> getSignInForCurrentWeek(@Param("userInfoId") String userInfoId);

    @Sql("SELECT sign_in_date FROM hbird_user_sign_in WHERE user_info_id = :userInfoId AND sign_in_date like concat(:time,'%');")
    List<UserSignInRestDTO> getSignInForMonth(@Param("userInfoId") String userInfoId, @Param("time") String time);

    /**
     * 根据指定时间查询是否存在签到记录
     * @param userInfoId
     * @param signInDate
     * @return
     */
    @Sql("SELECT count(id) FROM `hbird_account`.`hbird_user_sign_in` WHERE `user_info_id` = :userInfoId and `sign_in_date`=:signInDate;")
    int checkSignInForSignInDay(@Param("userInfoId") String userInfoId, @Param("signInDate") String signInDate);

    /**
     * 修改签到记录中status状态
     * @param userInfoId
     * @param localDate
     */
    @Sql("UPDATE `hbird_account`.`hbird_user_sign_in` set `status` = if(:status is null,null,:status) WHERE `user_info_id` = :userInfoId and sign_in_date=:signInDate;")
    void updateSignInStatusBySignInDate(@Param("userInfoId") String userInfoId,@Param("status") Integer status, @Param("signInDate") String localDate);
}
