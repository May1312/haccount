package com.fnjz.front.dao;

import com.fnjz.front.entity.api.UserInviteRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created by yhang on 2018/10/17.
 */
@MiniDao
public interface UserInviteRestDao {
    /**
     * 新增记录
     * @param inviteCode
     * @param insertId
     * @return
     */
    @Sql("insert into hbird_user_invite (user_info_id,invite_user_info_id,type,create_date) values (:userInfoId,:inviteUserInfoId,1,now());")
    int insert(@Param("userInfoId") int inviteCode, @Param("inviteUserInfoId") int insertId);

    /**
     * 获取邀请人数
     * @param userInfoId
     * @return
     */
    @Sql("select count(id) from hbird_user_invite where user_info_id =:userInfoId and type=1;")
    int getCountForInvitedUsers(@Param("userInfoId") String userInfoId);

    /**
     * 获取邀请人数  按天
     * @param userInfoId
     * @return
     */
    @Sql("select count(id) from hbird_user_invite where user_info_id =:userInfoId and type=1 and create_date like CONCAT(DATE_FORMAT( now( ), '%Y-%m-%d' ),'%');")
    int getCountForInvitedUsersv2(@Param("userInfoId") String userInfoId);

    @ResultType(UserInviteRestDTO.class)
    @Sql("SELECT userinfo.nick_name,userinfo.avatar_url,userinfo.register_date FROM hbird_user_invite invite RIGHT JOIN hbird_user_info userinfo on invite.invite_user_info_id=userinfo.id where invite.user_info_id=:userInfoId and type=1 ORDER BY invite.create_date desc LIMIT :curpage,:itemPerPage;")
    List<UserInviteRestDTO> listForPage(@Param("userInfoId") String userInfoId,@Param("curpage") int startIndex,@Param("itemPerPage") int pageSize);

    /**
     * 返回积分数
     * @param userInfoId
     * @param startIndex
     * @param pageSize
     * @return
     */
    @Sql(" SELECT base3.nick_name, base3.avatar_url, base3.register_date, COALESCE ( sum( base4.integral_num_double ), 0 ) AS integral_num,base4.type FROM ( SELECT base1.invite_user_info_id, base2.nick_name, base2.avatar_url, base2.register_date FROM hbird_user_invite AS base1 INNER JOIN hbird_user_info AS base2 ON base1.invite_user_info_id = base2.id WHERE base1.user_info_id = :userInfoId AND base1.type = 1 ORDER BY base1.create_date DESC LIMIT :curpage,:itemPerPage ) AS base3 left JOIN hbird_user_integral AS base4 ON base3.invite_user_info_id = base4.user_info_id WHERE ( base4.integral_num_double IS NULL OR base4.integral_num_double > 0 ) GROUP BY base3.invite_user_info_id order by base3.invite_user_info_id desc;")
    List<UserInviteRestDTO> listForPagev2(@Param("userInfoId") String userInfoId,@Param("curpage") int startIndex,@Param("itemPerPage") int pageSize);

    /**
     * 积分返利   查询当前用户的邀请人昵称
     * @param userInfoId
     * @param beginTime
     * @param type   1 小程序邀请类型
     * @return
     */
    @Sql("select base2.nick_name as nickname,base1.user_info_id as userinfoid from hbird_user_invite as base1 JOIN hbird_user_info as base2 on base1.invite_user_info_id=base2.id where invite_user_info_id=:userInfoId and type=:type and create_date>=:beginTime;")
    Map<String,Object> getInvitedUserNickName(@Param("userInfoId") String userInfoId, @Param("beginTime") String beginTime, @Param("type") int type);

    /**
     * 查看是否存在上级
     * @param inviteUserInfoId
     * @return
     */
    @Sql("select COALESCE(count(id),0) from hbird_user_invite where invite_user_info_id=:inviteUserInfoId;")
    Integer checkExists(@Param("inviteUserInfoId") int inviteUserInfoId);

    /**
     * 好友邀请好友数
     * @param userInfoId
     * @return
     */
    @Sql("select count(id) from (select invite_user_info_id from hbird_user_invite where user_info_id =:userInfoId and type=1) as base1 INNER JOIN hbird_user_invite as base2 on base1.invite_user_info_id=base2.user_info_id;")
    Integer getCountForReInvitedUsers(@Param("userInfoId") String userInfoId);
}
