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

    //新增  主键自增
    @IdAutoGenerator(generator = "native")
    int insert(@Param("userInfoRestEntity") UserInfoRestEntity userInfoRestEntity);

    void update(@Param("userInfoRestEntity")UserInfoRestEntity userInfoRestEntity);
}
