package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

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
}
