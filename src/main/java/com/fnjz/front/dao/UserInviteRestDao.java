package com.fnjz.front.dao;

import com.fnjz.front.entity.api.UserInviteRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

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

    @ResultType(UserInviteRestDTO.class)
    @Sql("SELECT userinfo.nick_name,userinfo.avatar_url,userinfo.register_date FROM hbird_user_invite invite RIGHT JOIN hbird_user_info userinfo on invite.user_info_id=userinfo.id where invite.user_info_id=:userInfoId ORDER BY invite.create_date LIMIT :curpage,:itemPerPage;")
    List<UserInviteRestDTO> listForPage(@Param("userInfoId") String userInfoId,@Param("curpage") int startIndex,@Param("itemPerPage") int pageSize);

    @Sql("select count(*) from hbird_user_invite where user_info_id=:userInfoId")
    Integer getCount(@Param("userInfoId") String userInfoId);
}
