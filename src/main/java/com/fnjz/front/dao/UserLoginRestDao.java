package com.fnjz.front.dao;

import com.fnjz.front.entity.api.userlogin.UserLoginRestEntity;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;

/**
 * Created by yhang on 2018/6/1.
 */
@MiniDao
//@Repository
public interface UserLoginRestDao {

    /**
     * 新增用户login表记录
     * @param userLoginRestEntity
     * @return
     */
    @IdAutoGenerator(generator = "native")
    int insert(@Param("userLoginRestEntity") UserLoginRestEntity userLoginRestEntity);
}
