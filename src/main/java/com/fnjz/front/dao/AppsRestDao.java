package com.fnjz.front.dao;

import com.fnjz.front.entity.api.apps.AppsRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/6/13.
 */
@MiniDao
public interface AppsRestDao {

    /**
     * app检查更新
     * @param version
     * @param flag
     * @return
     */
    @ResultType(AppsRestDTO.class)
    @Sql("SELECT ha.mobile_system,ha.install_status,ha.url,ha.size,ha.update_log,ha.create_date,ha.version FROM hbird_apps ha,(SELECT max(version) as version FROM `hbird_apps` where version > :version AND mobile_system = :flag and app_status = 1)as a where ha.version = a.version;")
    AppsRestDTO appCheck(@Param("version") String version,@Param("flag") Integer flag);

}
