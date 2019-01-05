package com.fnjz.front.dao;

import com.fnjz.front.entity.api.buriedpointtype.BuriedPointRestEntity;
import com.fnjz.front.entity.api.buriedpointtype.BuriedPointTypeRestEntity;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.Sql;

import java.util.List;

/**
 * Created by yhang on 2019/1/5.
 */
@MiniDao
public interface BuriedPointRestDao {

    @Sql("select * from hbird_buried_point_type;")
    List<BuriedPointTypeRestEntity> getBuriedPointType();

    /**
     * 新增
     * @param entity
     */
    @Sql("insert into hbird_buried_point (user_info_id,device_num,point_type_id,client_id,brand,model,wechat_version,system,platform,create_time) values (:entity.userInfoId,:entity.deviceNum,:entity.pointTypeId,:entity.clientId,:entity.brand,:entity.model,:entity.wechatVersion,:entity.system,:entity.platform,now());")
    void insert(@Param("entity") BuriedPointRestEntity entity);
}
