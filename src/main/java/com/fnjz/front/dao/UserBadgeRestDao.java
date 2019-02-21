package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userbadge.UserBadgeInfoRestDTO;
import com.fnjz.front.entity.api.userbadge.UserBadgeRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

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
    @Sql("select base1.*,base2.unlock_icon as icon,base2.badge_name from (SELECT base3.NAME AS badgeTypeName, count( base2.badge_type_id ) AS myBadges, max(base2.id) as badge_id, base3.priority FROM hbird_user_badge AS base1 INNER JOIN hbird_badge as base2 ON base1.badge_id = base2.id INNER JOIN hbird_badge_type AS base3 ON base2.badge_type_id = base3.id WHERE user_info_id =:userInfoId AND base2.`status` = 1 AND base3.`status` = 1 GROUP BY base2.badge_type_id) as base1 inner join hbird_badge as base2 ON base1.badge_id = base2.id")
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

    @Sql("SELECT badge_name,lock_icon as icon,percentage,words,priority FROM hbird_badge WHERE badge_type_id = :btId and status=1;")
    List<UserBadgeInfoRestDTO> getMyBadgeInfoForAll(@Param("btId") Integer btId);
}
