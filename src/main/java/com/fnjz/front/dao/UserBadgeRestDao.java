package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userbadge.BadgeLabelRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoCheckRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/12/17.
 */
@MiniDao
public interface UserBadgeRestDao {

    /**
     * 获取我的徽章
     *
     * @param userInfoId
     * @return
     */
    @ResultType(UserBadgeRestDTO.class)
    @Sql("select base1.*,base2.unlock_icon as icon,base2.badge_name from (SELECT base3.NAME AS badgeTypeName, count( base2.badge_type_id ) AS myBadges, max(base2.id) as badge_id, base3.priority,base1.create_date FROM hbird_user_badge AS base1 INNER JOIN hbird_badge as base2 ON base1.badge_id = base2.id INNER JOIN hbird_badge_type AS base3 ON base2.badge_type_id = base3.id WHERE user_info_id =:userInfoId AND base2.`status` = 1 AND base3.`status` = 1 GROUP BY base2.badge_type_id) as base1 inner join hbird_badge as base2 ON base1.badge_id = base2.id")
    List<UserBadgeRestDTO> getMyBadges(@Param("userInfoId") String userInfoId);

    /**
     * 获取所有徽章类型数量+返回优先级最高的未解锁icon
     *
     * @return
     */
    @ResultType(UserBadgeRestDTO.class)
    @Sql("SELECT base2.id as badgeTypeId,base2.`name`as badge_type_name, count(base1.badge_type_id) as totalBadges, base1.lock_icon as icon,base1.badge_name, base2.priority FROM hbird_badge AS base1 INNER JOIN hbird_badge_type AS base2 ON base1.badge_type_id = base2.id WHERE base1.`status` =1 AND base2.`status` = 1 group by base2.id order by base2.priority desc;")
    List<UserBadgeRestDTO> getAllBadges();

    /**
     * 获取某一具体类型徽章获取情况---->已获得徽章详情
     *
     * @param btId
     * @return
     */
    @Sql("SELECT base2.badge_name,base2.unlock_icon as icon,base2.percentage,base1.create_date,base1.salary,base2.words,base1.rank,base2.priority,base3.`name` as badge_type_name FROM hbird_user_badge AS base1 INNER JOIN hbird_badge AS base2 ON base1.badge_id = base2.id inner join hbird_badge_type as base3 on base2.badge_type_id=base3.id WHERE base1.user_info_id = :userInfoId AND base1.badge_type_id = :btId;")
    List<UserBadgeInfoRestDTO> getMyBadgeInfoForUnlock(@Param("userInfoId") String userInfoId, @Param("btId") Integer btId);

    @Sql("SELECT id as badge_id,badge_name,lock_icon as icon,percentage,words,priority FROM hbird_badge WHERE badge_type_id = :btId and status=1;")
    List<UserBadgeInfoRestDTO> getMyBadgeInfoForAll(@Param("btId") Integer btId);

    /**
     * 获取徽章类型 ---> 对应标签
     * @return
     */
    @Sql("select base1.id as badge_type_id,base3.income_name as label_name,base3.id as label_id from hbird_badge_type as base1 inner join hbird_badge_type_label as base2 on base1.id=base2.badge_type_id inner join hbird_income_type as base3 on base2.type_id=base3.id;")
    List<BadgeLabelRestDTO> getSysBadgeLabel();

    /**
     * 获取指定徽章类型 用户最新解锁情况(用户弹框分享)
     * @param updateBy
     * @return
     */
    @Sql("SELECT base2.badge_name, base2.unlock_icon AS icon, base2.percentage, base1.create_date, base1.salary, base2.words, base1.rank, base2.priority, base3.`name` AS badge_type_name,base2.id as badge_id FROM hbird_user_badge AS base1 INNER JOIN hbird_badge AS base2 ON base1.badge_id = base2.id INNER JOIN hbird_badge_type AS base3 ON base2.badge_type_id = base3.id  WHERE base1.user_info_id = :userInfoId AND base1.badge_type_id = :btId order by base1.id desc limit 1;")
    UserBadgeInfoCheckRestDTO getLatestBadge(@Param("btId") Integer btId, @Param("userInfoId") Integer updateBy);

    /**
     * 获取下一徽章id
     * @param badgeTypeId
     * @param badgeId
     * @return
     */
    @Sql("select base1.id,base1.badge_name,base2.name as badge_type_name from hbird_badge as base1 inner join hbird_badge_type as base2 on base1.badge_type_id=base2.id where badge_type_id=:btId and base1.id>:badgeId limit 1;")
    Map<String,Object> getNextBadgeId(@Param("btId")Integer badgeTypeId, @Param("badgeId") Integer badgeId);

    /**
     * 获取指定徽章类型  最大排名数
     * @param badgeTypeId
     * @return
     */
    @Sql("select rank from hbird_user_badge where badge_type_id=:btId order by id desc limit 1;")
    Integer getRankBybtid(@Param("btId")Integer badgeTypeId);

    @Sql("insert into hbird_user_badge ( `user_info_id`, `badge_id`, `badge_type_id`, `salary`, `rank`, `create_date`) VALUES (:userInfoId,:badgeId,:btId,:salary,:rank,now())")
    void insert(@Param("userInfoId") Integer updateBy,@Param("badgeId") Integer badgeId,@Param("btId") Integer badgeTypeId,@Param("salary") BigDecimal money,@Param("rank") int i);

    @Sql("UPDATE `hbird_user_badge` SET `badge_id` = :badgeId,`create_date`=now(), `update_date` = now() WHERE user_info_id=:userInfoId and badge_type_id=:btId;")
    void updateBadgeId(@Param("userInfoId") String userInfoId,@Param("btId") Integer badgeTypeId,@Param("badgeId") Integer badgeId);

    /**
     * 获取指定徽章类型的解锁徽章icon
     * @param badgeId
     * @return
     */
    @Sql("select unlock_icon from hbird_badge where id=:badgeId;")
    String getUnlockIcon(@Param("badgeId") Integer badgeId);

    /**
     * 根据id获取徽章类型名称
     * @param btId
     * @return
     */
    @Sql("select name from hbird_badge_type where id=:btId;")
    String getBadgeTypeNameById(@Param("btId") Integer btId);

    @Sql("select count(base1.id) as myBadges,(select count(id) from hbird_badge where badge_type_id=:btId) as totalBadges from hbird_user_badge as base1 where user_info_id=:userInfoId and base1.badge_type_id=:btId;")
    Map<String,Object> getMyBadgesAndTotalBadges(@Param("userInfoId") Integer userInfoId,@Param("btId")Integer btId);
}
