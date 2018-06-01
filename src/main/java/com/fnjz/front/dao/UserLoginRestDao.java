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

    //新增  主键自增
    @IdAutoGenerator(generator = "native")
    int insert(@Param("userLoginRestEntity") UserLoginRestEntity userLoginRestEntity);
}
