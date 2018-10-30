package com.fnjz.front.dao;

import com.fnjz.front.entity.api.banner.BannerRestDTO;
import com.fnjz.front.entity.api.homewindow.HomeWindowRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2018/10/19.
 */
@MiniDao
public interface HomeWindowRestDao {
    /**
     * 获取首页弹框
     * @return
     */
    @ResultType(HomeWindowRestDTO.class)
    @Sql("SELECT * FROM hbird_home_window WHERE STATUS = 1 AND IF ( uptime IS NULL, 1 = 1, uptime <= CURRENT_TIMESTAMP ) AND IF ( uptime IS NULL, 1 = 1, downtime >= CURRENT_TIMESTAMP ) AND IF ( :type = 'android' and :version is not null, android_show_version <=:version, 1 = 1 ) AND IF ( :type = 'ios' and :version is not null, ios_show_version <=:version, 1 = 1 ) ORDER BY priority,create_date;")
    List<HomeWindowRestDTO> listForWindow(@Param("type") String type,@Param("version") String version);

    @ResultType(BannerRestDTO.class)
    @Sql("select * from hbird_banner where status=1 and if(uptime is null,1=1,uptime<=CURRENT_TIMESTAMP) and if(uptime is null,1=1,downtime>=CURRENT_TIMESTAMP) order by priority,create_date;")
    List<BannerRestDTO> listForSlideShow();
}
