package com.fnjz.front.service.impl.api.apps;

import com.fnjz.front.dao.AppsRestDao;
import com.fnjz.front.entity.api.apps.AppsRestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnjz.front.service.api.apps.AppsRestServiceI;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;

@Service("appsRestService")
@Transactional
public class AppsRestServiceImpl extends CommonServiceImpl implements AppsRestServiceI {

    @Autowired
    private AppsRestDao appsRestDao;

    @Override
    public AppsRestDTO appCheck(String version, Integer flag) {
        return appsRestDao.appCheck(version,flag);
    }
}