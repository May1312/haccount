package com.fnjz.front.service.api.checkrest;

import java.util.Map;

/**
 * Created by yhang on 2018/8/31.
 */
public interface CheckRestServiceI {
    /**
     * 获取系统类目/用户常用类目/类目排序关系接口
     * @return
     */
    Map<String,Object> getSysAndUserSpendAndSynInterval(String userInfoId);
}
