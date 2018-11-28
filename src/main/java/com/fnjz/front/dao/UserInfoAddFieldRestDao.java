package com.fnjz.front.dao;

import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

/**
 * Created by yhang on 2018/11/28.
 */
@MiniDao
public interface UserInfoAddFieldRestDao {

    /**
     * 判断是否存在
     *
     * @param userInfoId
     * @return
     */
    @Sql("select open_id from hbird_user_info_add_field where user_info_in=:userInfoId;")
    String getByUserInfoId(@Param("userInfoId") String userInfoId);

    @Sql("insert into hbird_user_info_add_field (user_info_id,open_id,type,create_date) values (:userInfoId,:openId,1,now());")
    void insert(@Param("userInfoId") String userInfoId, @Param("opendId") String opendId);
}
