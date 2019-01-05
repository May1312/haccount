package com.fnjz.front.service.api.buriedpoint;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.front.entity.api.buriedpointtype.BuriedPointRestEntity;

/**
 * Created by yhang on 2019/1/5.
 */
public interface BuriedPointServiceI {
    ResultBean getBuriedPointType();

    /**
     * 新增数据
     * @param entity
     */
    void insert(BuriedPointRestEntity entity);
}
