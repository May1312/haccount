package com.fnjz.front.service.api.apps;

import com.fnjz.front.entity.api.apps.AppsRestDTO;
import org.jeecgframework.core.common.service.CommonService;

public interface AppsRestServiceI extends CommonService{

    /**
     * app检查更新
     * @param version
     * @param flag
     * @return
     */
    AppsRestDTO appCheck(String version, Integer flag);
}
