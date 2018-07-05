package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userinfo.UserInfoRestEntity;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;

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
}
