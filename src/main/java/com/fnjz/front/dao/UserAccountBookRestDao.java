package com.fnjz.front.dao;

import com.fnjz.front.entity.api.useraccountbook.UserAccountBookRestEntity;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;

/**
 * Created by yhang on 2018/6/1.
 */
@MiniDao
public interface UserAccountBookRestDao {
    /**
     * 新增用户账本关系记录 返回主键
     * @param userAccountBookRestEntity
     * @return
     */
    @IdAutoGenerator(generator = "native")
    int insert(@Param("userAccountBookRestEntity") UserAccountBookRestEntity userAccountBookRestEntity);
}
