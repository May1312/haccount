package com.fnjz.front.dao;

import com.fnjz.front.entity.api.accountbook.AccountBookRestEntity;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;

/**
 * Created by yhang on 2018/6/1.
 */
@MiniDao
public interface AccountBookRestDao {
    /**
     * 新增用户账本记录 返回主键
     * @param accountBookRestEntity
     * @return
     */
    @IdAutoGenerator(generator = "native")
    int insert(@Param("accountBookRestEntity") AccountBookRestEntity accountBookRestEntity);
}
