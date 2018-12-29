package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.Map;

/**
 * Created by yhang on 2018/6/1.
 */
@MiniDao
public interface UserInfoRestDao {

    /**
     * 新增用户详情记录  返回主键
     * @param userInfoRestEntity
     * @return
     */
    @IdAutoGenerator(generator = "native")
    int insert(@Param("userInfoRestEntity") UserInfoRestEntity userInfoRestEntity);

    /**
     * 更新用户详情
     * @param userInfoRestEntity
     */
    void update(@Param("userInfoRestEntity")UserInfoRestEntity userInfoRestEntity);

    /**
     * 功能描述: 根据用户id获取用户昵称
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/16 18:24
     */
    @Sql("select IFNULL(u.nick_name,REPLACE(u.mobile, SUBSTR(mobile,4,4), '****')) name  from hbird_user_info u  where id = :userInfoId;")
    String getUserNameByUserId(@Param("userInfoId") Integer userInfoId);

    /**
     * 获取 昵称 头像
     * @param userInfoId
     * @return
     */
    @Sql("select nick_name as nickName,avatar_url as avatarUrl,register_date as registerDate from hbird_user_info where id=:userInfoId;")
    Map<String,Object> getNKAndAUById(@Param("userInfoId") Integer userInfoId);
}
