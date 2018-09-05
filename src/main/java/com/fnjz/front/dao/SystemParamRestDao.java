package com.fnjz.front.dao;

import com.fnjz.front.entity.api.systemparam.SystemParamRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * 系统参数检查表dao
 * Created by yhang on 2018/9/4.
 */
@MiniDao
public interface SystemParamRestDao {

    /**
     * 获取系统参数---->version最大版本
     * @return
     */
    @Sql("select param_type,max(version) as version from hbird_system_param group by param_type;")
    List<SystemParamRestEntity> getSystemParam();
}
