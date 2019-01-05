package com.fnjz.front.service.impl.api.buriedpoint;

import com.fnjz.commonbean.ResultBean;
import com.fnjz.constants.ApiResultType;
import com.fnjz.front.dao.BuriedPointRestDao;
import com.fnjz.front.entity.api.buriedpointtype.BuriedPointRestEntity;
import com.fnjz.front.entity.api.buriedpointtype.BuriedPointTypeRestEntity;
import com.fnjz.front.service.api.buriedpoint.BuriedPointServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yhang on 2019/1/5.
 */
@Service("buriedPointServiceI")
public class BuriedPointServiceImpl  implements BuriedPointServiceI {

    @Autowired
    private BuriedPointRestDao buriedPointRestDao;

    @Override
    public ResultBean getBuriedPointType() {
        List<BuriedPointTypeRestEntity> list = buriedPointRestDao.getBuriedPointType();
        return new ResultBean(ApiResultType.OK,list);
    }

    @Override
    public void insert(BuriedPointRestEntity entity) {
        buriedPointRestDao.insert(entity);
    }
}
