package com.fnjz.front.dao;

import com.fnjz.front.entity.api.banner.BannerRestDTO;
import com.fnjz.front.entity.api.homewindow.HomeWindowRestDTO;
import org.jeecgframework.minidao.annotation.MiniDao;
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
    @Sql("select * from hbird_home_window where status=1 and if(uptime is null,1=1,uptime<=CURRENT_TIMESTAMP) and if(uptime is null,1=1,downtime>=CURRENT_TIMESTAMP) order by priority;")
    List<HomeWindowRestDTO> listForWindow();

    @ResultType(BannerRestDTO.class)
    @Sql("select * from hbird_banner where status=1 and if(uptime is null,1=1,uptime<=CURRENT_TIMESTAMP) and if(uptime is null,1=1,downtime>=CURRENT_TIMESTAMP) order by priority;")
    List<BannerRestDTO> listForSlideShow();
}
